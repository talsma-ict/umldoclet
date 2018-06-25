/*
 * Copyright 2016-2018 Talsma ICT
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

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * Class representing a type name.
 * <p>
 * This is less simple than it sounds: A type basically has a 'qualified name' and a 'simple name'
 * (these may be equal).
 * <p>
 * Also, if the type is a generic type, the actual type parameters can be seen as 'part' of the name:
 * The names of {@code List<String>} and {@code List<Integer>} are different, while in Java the actual
 * types are equal (due to erasure of the generic type).
 *
 * @author Sjoerd Talsma
 */
public class TypeName implements Comparable<TypeName> {
    public final String simple, qualified;
    private final TypeName[] generics;

    public TypeName(String simpleName, String qualifiedName, TypeName... generics) {
        this.simple = simpleName;
        this.qualified = qualifiedName;
        this.generics = generics.clone();
    }

    public TypeName[] getGenerics() {
        return generics.clone();
    }

    private static boolean isQualified(TypeDisplay display) {
        return display != null && display.name().startsWith("QUALIFIED");
    }

    protected String toUml(TypeDisplay display, Namespace namespace) {
        StringBuilder output = new StringBuilder();
        if (display == null) display = TypeDisplay.SIMPLE;
        if (!TypeDisplay.NONE.equals(display)) try {

            if (namespace != null && this.qualified.startsWith(namespace.name + ".")) {
                String name = this.qualified.substring(namespace.name.length() + 1);
                if (name.indexOf('.') > 0) name = this.qualified;
                output.append(name);
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
    public int compareTo(TypeName other) {
        requireNonNull(other, "Cannot compare with type name <null>.");
        return comparing((TypeName type) -> type.qualified.toLowerCase())
                .thenComparing(type -> type.qualified)
                .compare(this, other);
    }

    @Override
    public int hashCode() {
        return qualified.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof TypeName && this.compareTo((TypeName) other) == 0);
    }

    @Override
    public String toString() {
        return toUml(TypeDisplay.SIMPLE, null);
    }

    public static class Array extends TypeName {
        private Array(TypeName componentType) {
            super(componentType.simple, componentType.qualified, componentType.generics);
        }

        public static Array of(TypeName componentType) {
            return new Array(requireNonNull(componentType, "Component type of array is <null>."));
        }

        @Override
        protected String toUml(TypeDisplay display, Namespace namespace) {
            return super.toUml(display, namespace) + "[]";
        }
    }

    public static class Variable extends TypeName {
        private final String variable;
        private final boolean isExtends;

        private Variable(String variable, TypeName bound, boolean isExtends) {
            super(bound.simple, bound.qualified, bound.generics);
            this.variable = variable;
            this.isExtends = isExtends;
        }

        public static Variable extendsBound(String variable, TypeName bound) {
            return new Variable(variable, requireNonNull(bound, "Upper bound is <null>."), true);
        }

        public static Variable superBound(String variable, TypeName bound) {
            return new Variable(variable, requireNonNull(bound, "Lower bound is <null>."), false);
        }

        @Override
        protected String toUml(TypeDisplay display, Namespace namespace) {
            return String.format("%s %s %s", variable, isExtends ? "extends" : "super", super.toUml(display, namespace));
        }

        @Override
        public int compareTo(TypeName other) {
            int delta = other instanceof Variable ? 0 : (isExtends ? 1 : -1);
            if (delta == 0) delta = super.compareTo(other);
            if (delta == 0) delta = Boolean.compare(this.isExtends, ((Variable) other).isExtends);
            return delta;
        }
    }
}
