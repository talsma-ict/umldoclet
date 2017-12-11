package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.model.Field.umlAccessibility;

/**
 * @author Sjoerd Talsma
 */
public class Method extends Renderer {

    protected final ExecutableElement method;
    protected final Set<Modifier> modifiers;

    protected Method(Type type, ExecutableElement executableElement) {
        super(type.diagram);
        this.method = requireNonNull(executableElement, "Method executable element is <null>.");
        this.modifiers = executableElement.getModifiers();
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        return output.append(umlAccessibility(modifiers)).append(method.getSimpleName()).append("()").newline();
    }

}
