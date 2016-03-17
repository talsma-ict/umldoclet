package nl.talsmasoftware.umldoclet.rendering;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Created by sjoerd on 05-03-16.
 */
public class NoteRenderer extends Renderer {

    protected final String note;
    private final String position = "bottom";
    private final String targetName;

    protected NoteRenderer(UMLDiagram currentDiagram, String note, String targetName) {
        super(currentDiagram);
        this.note = requireNonNull(note, "Note to render may not be <null>!").trim();
        this.targetName = targetName;
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        output.append("note");
        if (targetName != null) {
            output.whitespace().append(position).whitespace().append("of").whitespace().append(targetName);
        }
        output.newline().indent().append(note).newline();
        return output.append("end note").newline().newline();
    }

}
