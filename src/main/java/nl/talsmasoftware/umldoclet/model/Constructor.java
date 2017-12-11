package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.ExecutableElement;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class Constructor extends Renderer {

    protected final ExecutableElement ctr;

    protected Constructor(Type type, ExecutableElement executableElement) {
        super(type.diagram);
        this.ctr = requireNonNull(executableElement, "Constructor executable element is <null>.");
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        return output.append("' TODO constructor").newline();
    }

}
