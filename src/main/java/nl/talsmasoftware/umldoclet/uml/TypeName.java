/*
 * Copyright 2016-2026 Talsma ICT
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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.configuration.TypeDisplay;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/// Class representing a type name.
///
///
/// This is less simple than it sounds: A type basically has a 'qualified name' and a 'simple name'
/// (these may be equal).
///
///
/// Also, if the type is a generic type, the actual type parameters can be seen as 'part' of the name:
/// The names of `List<String>` and `List<Integer>` are different, while in Java the actual
/// types are equal (due to erasure of the generic type).
///
/// @author Sjoerd Talsma
public class TypeName {
    public final String packagename;
    public final String simple;
    public final String qualified;
    private final TypeName[] generics;

    /// Creates a new type name.
    ///
    /// @param packagename   The name of the package.
    /// @param simpleName    The simple name of the type.
    /// @param qualifiedName The qualified name of the type.
    /// @param generics      The generic type parameters.
    public TypeName(String packagename, String simpleName, String qualifiedName, TypeName... generics) {
        this.packagename = packagename;
        this.simple = simpleName;
        this.qualified = requireNonNull(qualifiedName, "Type has no qualified name");
        this.generics = generics.clone();
    }

    /// @return The generic type parameters.
    public TypeName[] getGenerics() {
        return generics.clone();
    }

    private static boolean isQualified(TypeDisplay display) {
        return display != null && display.name().startsWith("QUALIFIED");
    }

    /// Returns the qualified name with a custom separator.
    ///
    /// @param separator The separator to use.
    /// @return The qualified name with the custom separator.
    public String getQualified(String separator) {
        int plen = packagename == null ? 0 : packagename.length();
        if (qualified.length() > plen && plen > 0 && separator != null && !separator.isEmpty()) {
            return packagename + separator + qualified.substring(plen + 1);
        }
        return qualified;
    }

    /// Returns the UML representation of this type name.
    ///
    /// @param display   The display mode.
    /// @param namespace The namespace to use for relative names.
    /// @return The UML representation.
    protected String toUml(TypeDisplay display, Namespace namespace) {
        StringBuilder output = new StringBuilder();
        if (display == null) display = TypeDisplay.SIMPLE;
        if (!TypeDisplay.NONE.equals(display)) try {

            if (namespace != null && this.qualified.startsWith(namespace.name + ".")) {
                output.append(this.qualified.substring(namespace.name.length() + 1));
            } else if (isQualified(display)) {
                output.append(this.qualified);
            } else {
                output.append(this.simple);
            }
            writeGenericsTo(output, TypeDisplay.QUALIFIED_GENERICS.equals(display) ? display : TypeDisplay.SIMPLE);

        } catch (IOException ioe) {
            throw new IllegalStateException("I/O error writing type name \"" + qualified + "\" to the output: "
                    + ioe.getMessage(), ioe);
        }
        return output.toString();
    }

    private <A extends Appendable> A writeGenericsTo(A output, TypeDisplay genericDisplay) throws IOException {
        if (generics.length > 0) {
            StringBuilder buffer = new StringBuilder();
            String sep = "<";
            for (TypeName generic : generics) {
                buffer.append(sep).append(generic.toUml(genericDisplay, null));
                sep = ", ";
            }
            String res = buffer.toString();
            if (isMarkupTag(res)) {
                res = "<\u200B" + res.substring(1); // Insert zero-width-space character between < and markup character.
            }
            output.append(res).append(">");
        }
        return output;
    }

    private static boolean isMarkupTag(String value) {
        return value.equalsIgnoreCase("<u")
                || value.equalsIgnoreCase("<b")
                || value.equalsIgnoreCase("<i");
    }

    @Override
    public int hashCode() {
        return qualified.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other != null && getClass().equals(other.getClass())
                && this.qualified.equals(((TypeName) other).qualified)
        );
    }

    @Override
    public String toString() {
        return toUml(TypeDisplay.SIMPLE, null);
    }

    /// Representation of an array type.
    public static class Array extends TypeName {
        private Array(TypeName componentType) {
            super(componentType.packagename, componentType.simple, componentType.qualified, componentType.generics);
        }

        /// Creates a new array type for the given component type.
        ///
        /// @param componentType The component type of the array.
        /// @return The array type.
        public static Array of(TypeName componentType) {
            return new Array(requireNonNull(componentType, "Component type of array is <null>."));
        }

        @Override
        protected String toUml(TypeDisplay display, Namespace namespace) {
            return super.toUml(display, namespace) + "[]";
        }
    }

    /// Representation of a type variable (generic parameter).
    public static class Variable extends TypeName {
        private final String variable;
        private final boolean isExtends;

        private Variable(String variable, TypeName bound, boolean isExtends) {
            super(bound.packagename, bound.simple, bound.qualified, bound.generics);
            this.variable = variable;
            this.isExtends = isExtends;
        }

        /// Creates a new type variable with an `extends` bound.
        ///
        /// @param variable The name of the variable.
        /// @param bound    The upper bound.
        /// @return The type variable.
        public static Variable extendsBound(String variable, TypeName bound) {
            return new Variable(variable, requireNonNull(bound, "Upper bound is <null>."), true);
        }

        /// Creates a new type variable with a `super` bound.
        ///
        /// @param variable The name of the variable.
        /// @param bound    The lower bound.
        /// @return The type variable.
        public static Variable superBound(String variable, TypeName bound) {
            return new Variable(variable, requireNonNull(bound, "Lower bound is <null>."), false);
        }

        @Override
        protected String toUml(TypeDisplay display, Namespace namespace) {
            return String.format("%s %s %s", variable, isExtends ? "extends" : "super", super.toUml(display, namespace));
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && this.isExtends == ((Variable) other).isExtends;
        }
    }
}
