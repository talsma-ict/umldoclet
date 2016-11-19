/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.umldoclet.model;

import com.sun.javadoc.*;
import nl.talsmasoftware.umldoclet.rendering.Renderer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static nl.talsmasoftware.umldoclet.logging.LogSupport.trace;
import static nl.talsmasoftware.umldoclet.logging.LogSupport.warn;

/**
 * @author Sjoerd Talsma
 */
public class Model {
    private static final Set<String> OPTIONAL_TYPES = unmodifiableSet(new LinkedHashSet<>(asList(
            "java.util.Optional", "com.google.common.base.Optional")));

    /**
     * Returns whether the the given element is deprecated;
     * it has the {@literal @}{@link Deprecated} annotation
     * or the {@literal @}deprecated JavaDoc tag.
     * <p/>
     * If the element itself is not deprecated, the method checks whether the superclass or containing class
     * is deprecated.
     *
     * @param element The element being inspected for deprecation.
     * @return {@code true} if the specified {@code element} is deprecated, {@code false} if it is not.
     */
    public static boolean isDeprecated(ProgramElementDoc element) {
        // Is the element itself deprecated?
        if (element == null) return false;
        else if (element.tags("deprecated").length > 0) return true;
        for (AnnotationDesc annotation : element.annotations()) {
            if (Deprecated.class.getName().equals(annotation.annotationType().qualifiedName())) {
                return true;
            }
        }

        // Element itself is not deprecated.
        // Could it be contained in a deprecated class or extend a deprecated superclass?
        return isDeprecated(element.containingClass())
                || (element instanceof ClassDoc && isDeprecated(((ClassDoc) element).superclass()));
    }

    /**
     * Return whether tested type is in the same package or a subpackage of the given package name.
     *
     * @param packageName The name of the package to be tested for.
     * @param type        The tested type.
     * @return whether the tested type is within the specified package.
     */
    public static boolean isInSameOrSubPackage(final String packageName, final Type type) {
        if (packageName != null && type != null) try {
            String testedPackageNm = type.asClassDoc().containingPackage().name();
            return packageName.equals(testedPackageNm) || testedPackageNm.startsWith(packageName + ".");
        } catch (RuntimeException rte) {
            warn("Cannot determine whether type \"{0}\" is within the package \"{1}\".",
                    type, packageName, rte);
        }
        return false;
    }

    /**
     * Finds a renderer in a haystack of renderers.
     *
     * @param haystack The renderers to search through.
     * @param needle   The renderer to be found.
     * @param <R>      The type of the renderer being searched.
     * @return The found renderer (from the haystack) or <code>null</code> if it couldn't be found.
     */
    public static <R extends Renderer> R find(Iterable<? extends R> haystack, R needle) {
        if (haystack != null && needle != null) for (R straw : haystack) if (needle.equals(straw)) return straw;
        return null;
    }

    /**
     * This method returns the parameterized type of the generic optional type, if it is an optional type.<br>
     * Both java 8 <code>Optional</code> objects and google Guava's <code>Optional</code> objects are supported.
     *
     * @param type The type to check if it is an optional type.
     * @return The type of the optional object, or <code>null</code> if the specified type was not an optional.
     */
    public static Type optionalType(final Type type) {
        final List<Type> chain = superclassChainTo(type, new ArrayList<Type>(), OPTIONAL_TYPES);
        if (chain != null) {
            for (int i = chain.size() - 1; i >= 0; i--) {
                ParameterizedType genericTp = chain.get(i).asParameterizedType();
                Type[] typeArgs = genericTp != null ? genericTp.typeArguments() : null;
                if (typeArgs != null && typeArgs.length == 1) {
                    return typeArgs[0];
                }
            }
        }
        return null;
    }

    /**
     * This method returns the parameterized type of the generic iterable type, if it can be determined.<br>
     * Both Array types and subtypes of {@link Iterable} are supported.
     *
     * @param type The type to be inspected whether it is iterable.
     * @return The type of the iterable, or <code>null</code> if the specified type was not an iterable or that could
     * not be determined (possibly due to missing JavaDoc or class availability).
     */
    public static Type iterableType(final Type type) {
        final String dimension = type != null ? type.dimension() : null;
        final Type iterableType = dimension != null && dimension.startsWith("[") ? type     // Array
                : isSubtypeOf(Iterable.class, type) ? firstGenericTypeArgumentOf(type)      // subtype of Iterable
                : null;
        trace(iterableType == null ? "Type does not seem to be iterable: {0}."
                : "Generic type is {1} for iterable {0}.", type, iterableType);
        return iterableType;
    }

    /**
     * This method tries hard to determine whether the specified JavaDoc <code>type</code> is in fact a subtype of the
     * specified class or interface. Obviously, the class or interface must be on the classpath of the doclet, so this
     * usually only works for included JDK classes.
     *
     * @param javaClass The java class to be checked against as supertype.
     * @param type      The JavaDoc type to be inspected.
     * @return <code>true</code> if the JavaDoc type is known to be a subtype of the given class.
     * <code>false</code> if this is not the case or if it just cannot be determined.
     */
    public static boolean isSubtypeOf(final Class<?> javaClass, final Type type) {
        boolean isSubtype = false;
        if (javaClass != null && type != null) {
            if (javaClass.getName().equals(type.typeName())) isSubtype = true;
            else {
                final Class<?> typeClass = tryLoadClass(type);
                if (typeClass != null && javaClass.isAssignableFrom(typeClass)) isSubtype = true;
                else {
                    final Type supertype = supertypeOf(type);
                    isSubtype = supertype != null && isSubtypeOf(javaClass, supertype);
                }
            }
        }
        trace("{0} {1} a subtype of {2}.", type, isSubtype ? "is" : "is not", javaClass);
        return isSubtype;
    }

    private static Type firstGenericTypeArgumentOf(Type type) {
        if (type == null) return null;
        Type genericType = firstGenericTypeArgumentOf(supertypeOf(type));
        if (genericType == null) {
            ParameterizedType parameterized = type.asParameterizedType();
            if (parameterized != null) {
                Type[] typeArgs = parameterized.typeArguments();
                if (typeArgs != null && typeArgs.length > 0) genericType = typeArgs[0];
            }
        }
        trace("First generic type is {1} for: {0}.", type, genericType);
        return genericType;
    }

    private static Class<?> tryLoadClass(final Type type) {
        try {
            return Class.forName(type.qualifiedTypeName());
        } catch (ClassNotFoundException | LinkageError | RuntimeException e) {
            trace("Not a class or unavailable on the classpath: {0}", type);
            return null;
        }
    }

    /**
     * Returns the supertype of the given type.<br>
     * However, there is a BIG caveat: The supertype must be in the set of documented classes for this to work.
     * For instance, there is no guarantee to be able to check whether some type is subclass of java.util.Collection.
     *
     * @param type The type to return the supertype of (if known / documented).
     * @return The supertype of the given type, or <code>null</code> if not available.
     */
    public static Type supertypeOf(Type type) {
        Type supertype = null;
        if (type != null) {
            final ClassDoc classDoc = type.asClassDoc();
            if (classDoc != null) {
                supertype = classDoc.superclassType();
                if (supertype == null) { // Could the type actually be on the classpath? (most likely for JDK classes)
                    final Class<?> clazz = tryLoadClass(type);
                    final Class<?> superclass = clazz != null ? clazz.getSuperclass() : null;
                    if (superclass != null) try {
                        return classDoc.findClass(superclass.getName());
                    } catch (RuntimeException e) {
                        trace("Error looking for javadoc of {0}: {1}", superclass, e.toString());
                    }
                }
            }
        }
        return supertype;
    }

    private static List<Type> superclassChainTo(final Type type, List<Type> chain, Set<String> requestedTypes) {
        if (type == null || chain == null || requestedTypes == null) return null;
        chain.add(type);
        return requestedTypes.contains(type.qualifiedTypeName()) ? chain
                : superclassChainTo(supertypeOf(type), chain, requestedTypes);
    }

}
