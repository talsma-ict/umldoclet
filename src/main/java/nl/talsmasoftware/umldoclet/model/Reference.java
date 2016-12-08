package nl.talsmasoftware.umldoclet.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.model.Concatenation.append;

/**
 * Model class for a reference between two types.
 *
 * @author Sjoerd Talsma
 */
public class Reference {

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
        final String toCardinality = to.cardinality.isEmpty() ? "" : to.cardinality + " ";
        return from + " " + type + " " + toCardinality + to.qualifiedName;
    }

    private String reverseType() {
        char[] chars = type.toCharArray();
        char swap = chars[0];
        for (int i = 0, j = chars.length - 1; i < j; swap = chars[i++]) {
            chars[i] = reverseChar(chars[j]);
            chars[j--] = reverseChar(swap);
        }
        return String.valueOf(chars);
    }

    private static char reverseChar(char ch) {
        return ch == '<' ? '>' : ch == '>' ? '<' : ch;
    }

    public static final class Side {
        public final String qualifiedName, cardinality;

        public static Side from(String fromQualifiedName) {
            return from(fromQualifiedName, null);
        }

        public static Side from(String fromQualifiedName, String fromCardinality) {
            return new Side(fromQualifiedName, fromCardinality);
        }

        public static Side to(String toQualifiedName) {
            return to(toQualifiedName, null);
        }

        public static Side to(String toQualifiedName, String toCardinality) {
            return new Side(toQualifiedName, toCardinality);
        }

        protected Side(String qualifiedName, String cardinality) {
            this.qualifiedName = requireNonNull(qualifiedName, "Name of referred object is <null>.").trim();
            if (this.qualifiedName.isEmpty()) throw new IllegalArgumentException("Name of referred object is empty.");
            this.cardinality = cardinality != null ? cardinality.trim() : "";
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

        @Override
        public String toString() {
            return cardinality.isEmpty() ? qualifiedName : qualifiedName + ' ' + cardinality;
        }
    }

}
