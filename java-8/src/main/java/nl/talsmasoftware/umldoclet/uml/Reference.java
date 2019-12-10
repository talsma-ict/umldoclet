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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;

/**
 * Reference between two types.
 * <p>
 * The following reference types are currently supported:
 * <ul>
 * <li>The 'extends' reference: {@code "--|>"}</li>
 * <li>The 'implements' reference: {@code "..|>"}</li>
 * <li>The 'inner class' reference: {@code "+--"}</li>
 * </ul>
 *
 * @author Sjoerd Talsma
 */
public class Reference extends UMLNode {
    public final Side from;
    public final Side to;
    public final String type;
    public final Collection<String> notes;

    public Reference(Side from, String type, Side to, String... notes) {
        this(from, type, to, notes == null ? emptySet() : asList(notes));
    }

    private Reference(Side from, String type, Side to, Collection<String> notes) {
        super(null);
        this.from = requireNonNull(from, "Reference \"from\" side is <null>.");
        this.type = requireNonNull(type, "Reference type is <null>.").trim();
        if (this.type.isEmpty()) throw new IllegalArgumentException("Reference type is empty.");
        this.to = requireNonNull(to, "Reference \"to\" side is <null>.");
        this.notes = notes.stream()
                .filter(Objects::nonNull)
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(toCollection(LinkedHashSet::new));
    }

    public boolean isSelfReference() {
        return from.qualifiedName.equals(to.qualifiedName);
    }

    public Reference addNote(final String note) {
        final String trimmed = note != null ? note.trim() : "";
        if (trimmed.isEmpty() || notes.contains(trimmed)) return this;
        final Collection<String> newNotes = new ArrayList<>(notes.size() + 1);
        newNotes.addAll(notes);
        newNotes.add(trimmed);
        return new Reference(from, type, to, newNotes);
    }

    private Reference inverse() {
        return new Reference(Side.from(to.qualifiedName, to.cardinality),
                reverseType(),
                Side.to(from.qualifiedName, from.cardinality),
                this.notes);
    }

    /**
     * @return The canonical type that can be used for equality matching.
     */
    public Reference canonical() {
        return type.startsWith("<-") || type.startsWith("<..")
                || type.endsWith("-|>") || type.endsWith("..|>")
                || type.endsWith("-*") || type.endsWith("-o")
                || type.endsWith("-+")
                ? inverse() : this;
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        // Namespace aware compensation
        final Namespace namespace = findParent(Namespace.class).orElse(null);

        output.append(from.toString(namespace)).whitespace()
                .append(type).whitespace()
                .append(to.toString(namespace));
        if (!notes.isEmpty()) output.append(": ").append(String.join("\\n", notes));
        output.newline();
        return output;
    }

    /**
     * Returns whether or not this reference contains the requested type.
     *
     * @param typeName The name of a type to check.
     * @return Whether either {@code from} or {@code to} matches {@code typeName}.
     */
    public boolean contains(TypeName typeName) {
        return from.matches(typeName) || to.matches(typeName);
    }

    @Override
    public int hashCode() {
        final Reference ref = canonical();
        return Objects.hash(ref.from, ref.type, ref.to);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        else if (!(other instanceof Reference)) return false;
        final Reference t_c = this.canonical();
        final Reference o_c = ((Reference) other).canonical();
        return t_c.from.equals(o_c.from) && t_c.type.equals(o_c.type) && t_c.to.equals(o_c.to);
    }

    private String reverseType() {
        char[] chars = type.toCharArray();
        char swap;
        for (int i = 0, j = chars.length - 1; i < j; i++) {
            swap = chars[i];
            chars[i] = reverseChar(chars[j]);
            chars[j--] = reverseChar(swap);
        }
        return String.valueOf(chars);
    }

    private static char reverseChar(char ch) {
        return ch == '<' ? '>' : ch == '>' ? '<'
                : ch == '{' ? '}' : ch == '}' ? '{'
                : ch;
    }

    public static final class Side {
        private final boolean nameFirst;
        private final String qualifiedName;
        private final String cardinality;

        public static Side from(String qualifiedName, String cardinality) {
            return new Side(qualifiedName, cardinality, true);
        }

        public static Side to(String qualifiedName, String cardinality) {
            return new Side(qualifiedName, cardinality, false);
        }

        private Side(String qualifiedName, String cardinality, boolean nameFirst) {
            requireNonNull(qualifiedName, "Name of referred object is <null>.");
            this.qualifiedName = qualifiedName.substring(0, indexOrLengthOf(qualifiedName, '<')).trim();
            if (this.qualifiedName.isEmpty()) throw new IllegalArgumentException("Name of referred object is empty.");
            this.cardinality = cardinality == null ? "" : cardinality.trim();
            this.nameFirst = nameFirst;
        }

        private boolean matches(TypeName typeName) {
            return typeName != null && this.qualifiedName.equals(typeName.qualified);
        }

        @Override
        public int hashCode() {
            return Objects.hash(qualifiedName, cardinality);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || (other instanceof Side
                    && this.qualifiedName.equals(((Side) other).qualifiedName)
                    && this.cardinality.equals(((Side) other).cardinality));
        }

        private String toString(Namespace namespace) {
            String name = qualifiedName;
            if (namespace != null && name.startsWith(namespace.name + ".")) {
                name = name.substring(namespace.name.length() + 1);
                if (name.indexOf('.') > 0) name = qualifiedName;
            }
            return cardinality.isEmpty() ? name
                    : nameFirst ? name + " \"" + cardinality + '"'
                    : '"' + cardinality + "\" " + name;
        }

        @Override
        public String toString() {
            return toString(null);
        }

        /**
         * The index of the searched character or the length of the string if not found.
         *
         * @param value The string to search in
         * @param ch    The character to search for
         * @return The index of the character in the string or the length of the string if not found.
         */
        private static int indexOrLengthOf(String value, char ch) {
            int idx = value.indexOf(ch);
            return idx >= 0 ? idx : value.length();
        }
    }

}
