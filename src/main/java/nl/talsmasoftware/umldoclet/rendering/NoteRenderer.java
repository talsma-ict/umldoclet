package nl.talsmasoftware.umldoclet.rendering;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Created by sjoerd on 05-03-16.
 */
public class NoteRenderer extends ParentAwareRenderer {

    protected final String note;
    private final String position = "bottom";

    protected NoteRenderer(Renderer parent, String note) {
        super(parent);
        this.note = requireNonNull(note, "Note to render may not be <null>!").trim();
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        output.append("note");
        if (parent instanceof ClassRenderer) {
            output.whitespace().append(position).whitespace().append("of").whitespace()
                    .append(((ClassRenderer) parent).name());
        }
        output.newline().indent().append(note).newline();
        return output.append("end note").newline().newline();
    }

}
