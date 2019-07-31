/*
 * Copyright 2016-2019 Talsma ICT
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

import static java.lang.Character.isLowerCase;
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
class JavaBeanProperty {
    private final String name;

    private Field field;
    private Method getter;
    private Method setter;

    JavaBeanProperty(String name) {
        this.name = name;
    }

    static Collection<JavaBeanProperty> detectFrom(Type type) {
        if (type == null) return emptySet();
        final Map<String, JavaBeanProperty> byName = new LinkedHashMap<>();
        type.getChildren().stream()
                .filter(TypeMember.class::isInstance).map(TypeMember.class::cast)
                .forEach(typeMember ->
                        propertyNameOf(typeMember).ifPresent(propertyName ->
                                byName.computeIfAbsent(propertyName, JavaBeanProperty::new)
                                        .add(typeMember)));
        return byName.values();
    }

    private static Optional<String> propertyNameOf(TypeMember member) {
        if (member instanceof Field) {
            return Optional.of(member.name);
        } else if (member instanceof Method) {
            if (member.name.startsWith("get") && member.type != null && parameterCount(member) == 0) {
                return Optional.of(decapitalize(member.name.substring(3)));
            } else if (member.name.startsWith("is") && isBooleanType(member.type) && parameterCount(member) == 0) {
                return Optional.of(decapitalize(member.name.substring(2)));
            } else if (member.name.startsWith("set") && parameterCount(member) == 1) {
                return Optional.of(decapitalize(member.name.substring(3)));
            }
        }
        return Optional.empty();
    }

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
        if (field == null && getter != null && setter != null) {
            // Convert the getter into a field for UML rendering purposes.
            final Type type = (Type) getter.getParent();
            field = new Field(type, name, getter.type);
            field.setVisibility(getter.getVisibility());
            type.removeChildren(child -> getter.equals(child) || setter.equals(child));
            type.addChild(field);
        }
    }

    /**
     * Counts the parameters of a typemember.
     *
     * @param member The type member to count the parameters of.
     * @return The total number of children in {@code Parameters} children of the member.
     */
    private static int parameterCount(TypeMember member) {
        return member.getChildren().stream()
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
        if (value != null && !value.isEmpty() && !isLowerCase(value.charAt(0))) {
            char[] chars = value.toCharArray();
            chars[0] = toLowerCase(chars[0]);
            value = new String(chars);
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
