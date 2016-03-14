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
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingWriter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Base implementation for any 'renderer' subclass.
 * <p/>
 * Renderers are capable of rendering themselves to {@link IndentingPrintWriter} instances and have
 * chaining methods returning these writers for easier appending.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public abstract class Renderer {

    protected final UMLDiagram diagram;
    protected final Collection<Renderer> children = new LinkedHashSet<>();

    protected Renderer(UMLDiagram diagram) {
        this.diagram = validateDiagram(diagram);
    }

    public abstract IndentingPrintWriter writeTo(IndentingPrintWriter output);

    public final Writer writeTo(Writer output) {
        if (output instanceof IndentingPrintWriter) {
            return writeTo((IndentingPrintWriter) output);
        } else {
            return writeTo(IndentingPrintWriter.wrap(
                    IndentingWriter.wrap(output)
                            .withIndentationWidth(
                                    diagram.config.indentation())));
        }
    }

    protected IndentingPrintWriter writeChildrenTo(IndentingPrintWriter output) {
        final IndentingPrintWriter indented = output.indent();
        for (Renderer child : children) {
            child.writeTo(indented);
        }
        return output;
    }

    protected static IndentingPrintWriter writeTypeTo(IndentingPrintWriter out, Type type) {
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

    /**
     * This method puts the given {@code value} within double-quotes
     * and escapes any double-quotes that may be already in the string.
     * <p/>
     * The method returns an empty String ({@code ""}) when the given {@code value}
     * is either {@code null} or empty. This is intentional as it allows for unconditional appending.
     *
     * @param value The value to be quoted.
     * @return The value within double quotes or an empty string if the value was null or empty.
     */
    protected static String quoted(String value) {
        return value == null || value.trim().isEmpty() ? "" : '"' + value.replaceAll("\"", "\\\"") + '"';
    }

    /**
     * Returns whether the the given element is deprecated;
     * it has the {@literal @}{@link Deprecated} annotation
     * or the {@literal @}deprecated JavaDoc tag.
     * <p/>
     * If the element itself is not deprecated, the method checks whether the superclass or containing class
     * is deprecated.
     *
     * @param element The element being inspected for deprecation.
     * @return {@code true} if the specified {@code element} is deprecated, {@code false} if it is not.
     */
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
     * @return {@code true} if the other object is an instance of this renderers class and its children are equal,{@code false} otherwise.
     */
    public boolean equals(Object other) {
        return this == other || (getClass().isInstance(other)
                && Objects.equals(children, ((Renderer) other).children)
        );
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
