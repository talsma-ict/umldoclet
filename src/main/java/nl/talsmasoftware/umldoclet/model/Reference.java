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
package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingRenderer;

import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.v1.Concatenation.append;

/**
 * Reference between two types.
 * <p>
 * The following reference types are currently supported:
 * <ul>
 * <li>The 'extends' reference: {@code "--|>"}</li>
 * <li>The 'implements' reference: {@code "..|>"}</li>
 * </ul>
 *
 * @author Sjoerd Talsma
 */
public class Reference implements Namespace.NameSpaceAware {

    public final Side from, to;
    public final String type;
    public final Collection<String> notes;

    public Reference(Side from, String type, Side to, String... notes) {
        this(from, type, to, notes != null && notes.length > 0 ? asList(notes) : null);
    }

    private Reference(Side from, String type, Side to, Iterable<String> notes) {
        this.from = requireNonNull(from, "Reference \"from\" side is <null>.");
        this.type = requireNonNull(type, "Reference type is <null>.").trim();
        if (this.type.isEmpty()) throw new IllegalArgumentException("Reference type is empty.");
        this.to = requireNonNull(to, "Reference \"to\" side is <null>.");

        if (notes == null) this.notes = emptySet();
        else { // Copy notes via an accumulator collection.
            final Set<String> notesAcc = new LinkedHashSet<>();
            for (String note : notes) {
                final String trimmed = note != null ? note.trim() : "";
                if (!trimmed.isEmpty()) notesAcc.add(trimmed);
            }
            if (notesAcc.isEmpty()) this.notes = emptySet();
            else if (notesAcc.size() == 1) this.notes = singleton(notesAcc.iterator().next());
            else this.notes = unmodifiableSet(notesAcc);
        }
    }

    public boolean isSelfReference() {
        return from.qualifiedName.equals(to.qualifiedName);
    }

    public Reference addNote(final String note) {
        final String trimmed = note != null ? note.trim() : "";
        return trimmed.isEmpty() || notes.contains(trimmed) ? this
                : new Reference(from, type, to, append(notes, trimmed));
    }

    private Reference inverse() {
        return new Reference(to, reverseType(), from, this.notes);
    }

    /**
     * @return The canonical type that can be used for equality matching.
     */
    public Reference canonical() {
        return type.startsWith("<--") || type.startsWith("<..")
                || type.endsWith("--|>") || type.endsWith("..|>")
                || type.endsWith("--*") || type.endsWith("--o")
                ? inverse() : this;
    }

    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output, Namespace namespace) {
        output.append(from.toString(namespace)).whitespace()
                .append(type).whitespace()
                .append(to.toString(namespace));
        if (!notes.isEmpty()) output.append(": ").append(notes.stream().collect(joining("\\n")));
        output.newline();
        return output;
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

    @Override
    public String toString() {
        return writeTo(new StringWriter()).toString();
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
        return ch == '<' ? '>' : ch == '>' ? '<' : ch;
    }

    public static final class Side {
        private final boolean nameFirst;
        public final String qualifiedName, cardinality;

        public static Side from(String fromQualifiedName) {
            return from(fromQualifiedName, null);
        }

        public static Side from(String fromQualifiedName, String fromCardinality) {
            return new Side(fromQualifiedName, fromCardinality, true);
        }

        public static Side to(String toQualifiedName) {
            return to(toQualifiedName, null);
        }

        public static Side to(String toQualifiedName, String toCardinality) {
            return new Side(toQualifiedName, toCardinality, false);
        }

        protected Side(String qualifiedName, String cardinality, boolean nameFirst) {
            requireNonNull(qualifiedName, "Name of referred object is <null>.");
            int genericIdx = qualifiedName.indexOf('<');
            if (genericIdx > 0) qualifiedName = qualifiedName.substring(0, genericIdx);
            this.qualifiedName = qualifiedName.trim();
            if (this.qualifiedName.isEmpty()) throw new IllegalArgumentException("Name of referred object is empty.");
            this.cardinality = cardinality != null ? cardinality.trim() : "";
            this.nameFirst = nameFirst;
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

        public String toString(Namespace namespace) {
            String name = qualifiedName;
            if (namespace != null && name.startsWith(namespace.name + ".")) {
                name = name.substring(namespace.name.length() + 1);
                if (name.indexOf('.') > 0) name = qualifiedName;
            }
            return cardinality.isEmpty() ? name
                    : nameFirst ? name + ' ' + cardinality
                    : cardinality + ' ' + name;
        }

        @Override
        public String toString() {
            return toString(null);
        }
    }

}
