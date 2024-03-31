/*
 * Copyright 2016-2024 Talsma ICT
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
package nl.talsmasoftware.umldoclet.uml.util;

import nl.talsmasoftware.umldoclet.configuration.Visibility;
import nl.talsmasoftware.umldoclet.uml.Field;
import nl.talsmasoftware.umldoclet.uml.Method;
import nl.talsmasoftware.umldoclet.uml.Parameters;
import nl.talsmasoftware.umldoclet.uml.Type;
import nl.talsmasoftware.umldoclet.uml.TypeMember;
import nl.talsmasoftware.umldoclet.uml.TypeName;
import nl.talsmasoftware.umldoclet.uml.UMLNode;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.util.Collections.emptySet;

/**
 * Class representing a property of a Java Bean.
 *
 * <p>
 * Each java bean contains properties that have getter and setter methods allowing access to a single field.
 *
 * <p>
 * Also see: <a href="https://en.wikipedia.org/wiki/JavaBeans">JavaBeans definition on Wikipedia</a>
 * or the <a href="http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html">Official
 * JavaBeans 1.01 Specification</a>.
 */
public class JavaBeanProperty {
    private final String name;

    private Field field;
    private Method getter;
    private Method setter;

    private static final int startIndexGetSet = 3;
    private static final int startIndexIs = 2;

    private JavaBeanProperty(String name) {
        this.name = name;
    }

    /**
     * This method detects the JavaBean properties from the uml {@linkplain Type} model of a Java class.
     *
     * <p>
     * The following will be detected as <a href="https://en.wikipedia.org/wiki/JavaBeans">JavaBean</a> property:
     * <ul>
     *     <li>A public {@linkplain Field}</li>
     *     <li>A public getter {@linkplain Method} (including isXyz() boolean getters)</li>
     *     <li>A public setter {@linkplain Method}</li>
     * </ul>
     *
     * @param type The uml model of a java type.
     * @return The detected JavaBean poperties in that type.
     */
    public static Collection<JavaBeanProperty> detectFrom(Type type) {
        if (type == null) return emptySet();
        final Map<String, JavaBeanProperty> propertiesByName = new LinkedHashMap<>();
        type.getChildren().stream()
                .filter(TypeMember.class::isInstance).map(TypeMember.class::cast)
                .filter(typeMember -> Visibility.PUBLIC.equals(typeMember.getVisibility()))
                .forEach(typeMember ->
                        propertyNameOf(typeMember).ifPresent(propertyName ->
                                propertiesByName.computeIfAbsent(propertyName, JavaBeanProperty::new)
                                        .add(typeMember)));
        return propertiesByName.values();
    }

    /**
     * This method checks if a type member matches the JavaBean propertyName convention and returns the
     * property name if it does.
     *
     * <p>
     * {@linkplain Field} names are returned as-is.
     * For getter/setter {@linkplain Method methods} the {@code "get"},  {@code "is"} or {@code "set"} prefix
     * is removed and the initial character of the remaining string is converted to lowercase.
     *
     * @param member The type member to evaluate.
     * @return The property name if the typemember is either a Field or a JavaBean getter/setter method.
     */
    private static Optional<String> propertyNameOf(TypeMember member) {
        Optional<String> propertyName = Optional.empty();
        if (member instanceof Field) {
            propertyName = Optional.of(member.name);
        } else if (member instanceof Method) {
            propertyName = propertyNameOfAccessor((Method) member);
        }
        return propertyName;
    }

    /**
     * Adds a detected {@linkplain Field} or {@linkplain Method} to the property.
     *
     * <p>
     * A javabean property normally consist of a private {@linkplain Field} and public getter and setter
     * {@linkplain Method methods}.
     *
     * <p>
     * This method assumes that the member conforms to the correct naming convention for JavaBeans,
     * no additional checks are performed.
     *
     * @param member The member to add to this property.
     */
    private void add(TypeMember member) {
        if (member instanceof Field) {
            this.field = (Field) member;
        } else if (member instanceof Method) {
            if (member.name.startsWith("set")) {
                this.setter = (Method) member;
            } else {
                this.getter = (Method) member;
            }
        }
    }

    /**
     * Remove the getter and setter methods from the parent and replace them with a field.
     *
     * <p>
     * <strong>Note:</strong> this method modifies the {@linkplain Type} parent in-place and therefore
     * is <strong>not</strong>> considered thread-safe!
     */
    void replaceGetterAndSetterByField() {
        if (getter != null && setter != null) {
            // Convert the getter into a field for UML rendering purposes.
            final Type type = (Type) getter.getParent();
            field = new Field(type, name, getter.type);
            field.setVisibility(getter.getVisibility());
            type.removeChildren(this::isSameProperty);
            type.addChild(field);
        }
    }

    /**
     * Test whether the {@linkplain #propertyNameOf(TypeMember) property name of} the specified UML node
     * matches the {@code name} of property.
     *
     * <p>
     * Although the method accepts any {@linkplain UMLNode} argument, only {@linkplain Field} and {@linkplain Method}
     * instances can ever obtain a positive result.
     *
     * @param node The UML node to check
     * @return {@code true} if this node is a {@linkplain Field} or {@linkplain Method} corresponding to this
     * JavaBean property.
     */
    private boolean isSameProperty(UMLNode node) {
        return node instanceof TypeMember && propertyNameOf((TypeMember) node).filter(name::equals).isPresent();
    }

    /**
     * Implements the {@linkplain #propertyNameOf(TypeMember) 'property name of'} evaluation for methods.
     *
     * @param method The getter/setter method to return the property name of.
     * @return The property name of the getter/setter method or {@code empty()} if the method did not start with
     * {@code "get"}, {@code "is"} or {@code "set"}.
     * @see #propertyNameOf(TypeMember)
     */

    /**
     * Introduced startIndexGetSet, startIndexIs constants to remove Magic numbers.
     */
    private static Optional<String> propertyNameOfAccessor(Method method) {
        Optional<String> propertyName = Optional.empty();
        if (!method.isStatic && !method.isAbstract) {
            if (isGetterMethod(method) || isSetterMethod(method)) {
                // Method name without 'get' / 'set' decapitalized
                propertyName = Optional.of(decapitalize(method.name.substring(startIndexGetSet)));
            } else if (isBooleanGetterMethod(method)) {
                // Method name without 'is' decapitalized
                propertyName = Optional.of(decapitalize(method.name.substring(startIndexIs)));
            }
        }
        return propertyName;
    }

    private static boolean isGetterMethod(Method method) {
        return method.type != null && method.name.startsWith("get") && parameterCount(method) == 0;
    }

    private static boolean isBooleanGetterMethod(Method method) {
        return isBooleanType(method.type) && method.name.startsWith("is") && parameterCount(method) == 0;
    }

    private static boolean isSetterMethod(Method method) {
        return method.name.startsWith("set") && parameterCount(method) == 1;
    }

    /**
     * Counts the parameters of a typemember.
     *
     * <p>
     * This method is only practically useful for {@linkplain Method} members. This counting method may become obsolete
     * after a suitable simplification of method parameters.
     *
     * @param method The method to count the parameters of.
     * @return The total number of children in {@code Parameters} children of the member.
     */
    private static int parameterCount(Method method) {
        return method.getChildren().stream()
                .filter(Parameters.class::isInstance)
                .map(UMLNode::getChildren).mapToInt(Collection::size)
                .sum();
    }

    /**
     * Changes the first character of the string into a lowercase character.
     *
     * @param value The value to decapitalize.
     * @return The decapitalized value.
     */
    private static String decapitalize(String value) {
        if (value != null && !value.isEmpty() && isUpperCase(value.charAt(0))) {
            char[] chars = value.toCharArray();
            chars[0] = toLowerCase(chars[0]);
            return new String(chars);
        }
        return value;
    }

    /**
     * Determines whether the type is a boolean type.
     *
     * <p>
     * This should be either the primitive {@code boolean} or the {@code java.lang.Boolean} wrapper.
     *
     * @param type The type to check if it represents a boolean.
     * @return {@code true} if the type is a boolean type, otherwise {@code false}.
     */
    private static boolean isBooleanType(TypeName type) {
        return type != null && ("boolean".equals(type.qualified) || "java.lang.Boolean".equals(type.qualified));
    }

}
