package nl.talsmasoftware.umldoclet.rendering;

import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Created by sjoerd on 05-03-16.
 */
public class NoteRenderer extends Renderer {

    private final String note;
    private final String position = null;
    private final String targetName;

    protected NoteRenderer(UMLDocletConfig config, UMLDiagram currentDiagram, String note, String targetName) {
        super(config, currentDiagram);
        this.note = requireNonNull(note, "Note to render may not be <null>!").trim();
        this.targetName = targetName;
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        output.append("note");
        if (targetName != null) {
            final String pos = position == null ? "bottom" : position;
            output.append(' ').append(pos).append(" of ").append(targetName);
        }
        output.newline().indent().append(note).newline();
        return output.append("end note").newline().newline();
    }

}
