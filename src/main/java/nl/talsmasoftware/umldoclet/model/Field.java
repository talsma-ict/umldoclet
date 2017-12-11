package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.*;
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
        output.append(umlAccessibility(modifiers)).append(fld.getSimpleName());
        output.append(":").whitespace().append(umlTypeOf(diagram.env.getTypeUtils().asElement(fld.asType())));
        return output.newline();
    }

    protected static char umlAccessibility(Set<Modifier> modifiers) {
        return modifiers.contains(PRIVATE) ? '-'
                : modifiers.contains(PROTECTED) ? '#'
                : modifiers.contains(PUBLIC) ? '+'
                : '~';
    }

    protected static String umlTypeOf(Element element) {
        if (element == null) return "null";
        final StringBuilder result = new StringBuilder();
        if (element instanceof QualifiedNameable) {
            result.append(((QualifiedNameable) element).getQualifiedName());
        } else {
            result.append(element.getSimpleName());
        }
        if (element instanceof Parameterizable) {
            result.append(Type.umlGenericsOf((Parameterizable) element));
        }
        return result.toString();
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
