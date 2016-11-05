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
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.rendering.Renderer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

/**
 * @author Sjoerd Talsma
 */
public class Model {
    private static final Set<String> OPTIONAL_TYPES = unmodifiableSet(new LinkedHashSet<>(asList(
            "java.util.Optional", "com.google.common.base.Optional")));
    // TODO: Obviously this is far from ideal! also missing all classes from j.u.concurrent
    private static final Set<String> ITERABLE_TYPES = unmodifiableSet(new LinkedHashSet<>(asList(
            "java.lang.Iterable", "java.util.Collection", "java.util.AbstractCollection",
            "java.util.AbstractList", "java.util.AbstractQueue", "java.util.AbstractSequenctialList",
            "java.util.AbstractSet", "java.util.Set", "java.util.List", "java.util.Stack",
            "java.util.SortedSet", "java.util.NavigableSet", "java.util.HashSet",
            "java.util.TreeSet", "java.util.LinkedHashSet", "java.util.ArrayList",
            "java.util.LinkedList", "java.util.Vector", "java.util.Queue", "java.util.EnumSet")));

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
        if (element == null) {
            return false;
        } else if (element.tags("deprecated").length > 0) {
            return true;
        }
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
     * @param testedType  The tested type.
     * @return whether the tested type is within the specified package.
     */
    public static boolean isInSameOrSubPackage(String packageName, Type testedType) {
        if (packageName != null && testedType != null) try {
            String testedPackageNm = testedType.asClassDoc().containingPackage().name();
            return packageName.equals(testedPackageNm) || testedPackageNm.startsWith(packageName + ".");
        } catch (RuntimeException rte) {
            LogSupport.warn("Cannot determine whether type \"{0}\" is within the package \"{1}\".",
                    testedType, packageName, rte);
        }
        return false;
    }

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

    public static Type iterableType(final Type type) {
        // check for T[] ...
        if (type != null && "[]".equals(type.dimension())) return type;
        // then theck for Iterable<T> ...
        final List<Type> chain = superclassChainTo(type, new ArrayList<Type>(), ITERABLE_TYPES);
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
     * Returns the supertype of the given type.<br>
     * However, there is a BIG caveat: The supertype must be in the set of documented classes for this to work.
     * For instance, there is no guarantee to be able to check whether some type is subclass of java.util.Collection.
     *
     * @param type The type to return the supertype of (if known / documented).
     * @return The supertype of the given type, or <code>null</code> if not available.
     */
    public static Type supertypeOf(Type type) {
        if (type != null) {
            final ClassDoc classDoc = type.asClassDoc();
            if (classDoc != null) return classDoc.superclassType();
        }
        return null;
    }

    private static List<Type> superclassChainTo(final Type type, List<Type> chain, Set<String> requestedTypes) {
        if (type == null || chain == null || requestedTypes == null) return null;
        chain.add(type);
        return requestedTypes.contains(type.qualifiedTypeName()) ? chain
                : superclassChainTo(supertypeOf(type), chain, requestedTypes);
    }

}
