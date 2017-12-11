package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.VariableElement;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class Variable extends Renderer {

    protected VariableElement var;

    protected Variable(Type type, VariableElement variableElement) {
        super(type.diagram);
        this.var = requireNonNull(variableElement, "Variable element is <null>.");
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        return output.append("+").append(var.getSimpleName()).newline();
    }

    @Override
    public int hashCode() {
        return Objects.hash(var.getEnclosingElement(), var.getSimpleName());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Variable
                && Objects.equals(var.getSimpleName(), ((Variable) other).var.getSimpleName())
                && Objects.equals(var.getEnclosingElement(), ((Variable) other).var.getEnclosingElement())
        );
    }

}
