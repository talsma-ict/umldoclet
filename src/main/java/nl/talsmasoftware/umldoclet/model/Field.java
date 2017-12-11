package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.Modifier.*;

/**
 * @author Sjoerd Talsma
 */
public class Field extends Renderer {

    protected final VariableElement fld;
    protected final Set<Modifier> modifiers;

    protected Field(Type type, VariableElement variableElement) {
        super(type.diagram);
        this.fld = requireNonNull(variableElement, "Variable element is <null>.");
        this.modifiers = fld.getModifiers();
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        if (modifiers.contains(STATIC)) output.append("{static}").whitespace();
        return output.append(umlAccessibility(modifiers)).append(fld.getSimpleName()).newline();
    }

    protected static char umlAccessibility(Set<Modifier> modifiers) {
        return modifiers.contains(PRIVATE) ? '-'
                : modifiers.contains(PROTECTED) ? '#'
                : modifiers.contains(PUBLIC) ? '+'
                : '~';
    }

    @Override
    public int hashCode() {
        return Objects.hash(fld.getEnclosingElement(), fld.getSimpleName());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Field
                && Objects.equals(fld.getSimpleName(), ((Field) other).fld.getSimpleName())
                && Objects.equals(fld.getEnclosingElement(), ((Field) other).fld.getEnclosingElement())
        );
    }

}
