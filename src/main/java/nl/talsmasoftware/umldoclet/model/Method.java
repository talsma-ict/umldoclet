package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.ExecutableElement;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class Method extends Renderer {

    protected final ExecutableElement method;

    protected Method(Type type, ExecutableElement executableElement) {
        super(type.diagram);
        this.method = requireNonNull(executableElement, "Method executable element is <null>.");
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        return output.append("' TODO method ").append(method.getSimpleName()).newline();
    }

}
