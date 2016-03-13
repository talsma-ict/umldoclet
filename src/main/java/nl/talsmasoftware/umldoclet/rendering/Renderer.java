/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.umldoclet.rendering;

import com.sun.javadoc.*;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingWriter;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public abstract class Renderer {

    // TODO: Refactor 'config' to 'currentDiagram.config'.
    // -> Probably easier to accomplish after we have more extensive unit tests in place.
    protected final UMLDocletConfig config;
    protected final UMLDiagram currentDiagram;
    protected final Collection<Renderer> children = new LinkedHashSet<>();

    protected Renderer(UMLDocletConfig config, UMLDiagram currentDiagram) {
        this.config = requireNonNull(config, "No UML doclet configuration provided.");
        this.currentDiagram = validateDiagram(currentDiagram);
    }

    public abstract IndentingPrintWriter writeTo(IndentingPrintWriter output);

    public final Writer writeTo(Writer output) {
        return this.writeTo(output instanceof IndentingPrintWriter ? (IndentingPrintWriter) output :
                IndentingPrintWriter.wrap(
                        IndentingWriter.wrap(output).withIndentationWidth(config.indentation())));
    }

    protected IndentingPrintWriter writeChildrenTo(IndentingPrintWriter output) {
        final IndentingPrintWriter indented = output.indent();
        for (Renderer child : children) {
            child.writeTo(indented);
        }
        return output;
    }

    protected IndentingPrintWriter writeTypeTo(IndentingPrintWriter out, Type type) {
        if (type != null) {
            out.append(type.typeName());
            ParameterizedType parameterizedType = type.asParameterizedType();
            if (parameterizedType != null) {
                Type[] generics = parameterizedType.typeArguments();
                if (generics.length > 0) {
                    out.append("<");
                    String sep = "";
                    for (Type generic : generics) {
                        writeTypeTo(out.append(sep), generic);
                        sep = ", ";
                    }
                    out.append(">");
                }
            }
        }
        return out;
    }

    public static boolean isDeprecated(ProgramElementDoc element) {
        // Is the element itself deprecated?
        if (element == null) {
            return false;
        } else if (element.tags("deprecated").length > 0) {
            return true;
        }
        for (AnnotationDesc annotation : element.annotations()) {
            if (Deprecated.class.getName().equals(annotation.annotationType().qualifiedName())) {
                return true;
            }
        }
        // Element itself is not deprecated.
        // Could it be contained in a deprecated class or extend a deprecated superclass?
        return isDeprecated(element.containingClass())
                || (element instanceof ClassDoc && isDeprecated(((ClassDoc) element).superclass()));
    }

    /**
     * @return Hashcode implementation based on the children of this renderer.
     */
    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    /**
     * Equals implementation based on 'instanceof' test and children equality.
     *
     * @param other The object to compare this renderer with.
     * @return {@code true} if the other object is an instance of this renderers class and its children are equal, {@code false} otherwise.
     */
    public boolean equals(Object other) {
        return this == other || (getClass().isInstance(other)
                && Objects.equals(children, ((Renderer) other).children)
        );
    }

    protected String quoted(String value) {
        return value == null || value.trim().isEmpty() ? "" : '"' + value.replaceAll("\"", "\\\"") + '"';
    }

    /**
     * Renders the entire content of this renderer and returns it as a String value.
     *
     * @return The rendered content of this renderer.
     */
    public String toString() {
        return writeTo(new StringWriter()).toString();
    }

    /**
     * Validates the given diagram. There is only one situation where a {@code null} diagram is accepted; for the
     * {@link UMLDiagram} class itself.
     *
     * @param currentDiagram The current diagram to be validated.
     * @return The guaranteed non-{@code null} current diagram value.
     */
    private UMLDiagram validateDiagram(UMLDiagram currentDiagram) {
        if (currentDiagram == null && !(this instanceof UMLDiagram)) {
            throw new IllegalArgumentException("No current UML diagram provided.");
        }
        return currentDiagram == null ? (UMLDiagram) this : currentDiagram;
    }

}
