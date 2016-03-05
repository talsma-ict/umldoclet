package nl.talsmasoftware.umldoclet.rendering;

import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Created by sjoerd on 05-03-16.
 */
public class NoteRenderer extends Renderer {

    private final String note;

    protected NoteRenderer(UMLDocletConfig config, UMLDiagram currentDiagram, String note) {
        super(config, currentDiagram);
        this.note = requireNonNull(note, "Note to render may not be <null>!").trim();
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        return null;
    }

}
