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

import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation for any 'renderer' subclass.
 * <p/>
 * Renderers are capable of rendering themselves to {@link IndentingPrintWriter} instances and have
 * chaining methods returning these writers for easier appending.
 *
 * @author Sjoerd Talsma
 */
public abstract class Renderer {

    protected final DiagramRenderer diagram;
    protected final Collection<Renderer> children = new LinkedHashSet<>();

    protected Renderer(DiagramRenderer diagram) {
        // Validates the given diagram.
        // There is only one situation where a <null> diagram is accepted; for the DiagramRenderer class itself.
        this.diagram = requireNonNull(this instanceof DiagramRenderer ? (DiagramRenderer) this
                : diagram, "Diagram renderer is <null>.");
    }

    protected abstract IndentingPrintWriter writeTo(IndentingPrintWriter output);

    public final Writer writeTo(Writer output) {
        final IndentingPrintWriter indentingPrintWriter =
                output instanceof IndentingPrintWriter ? (IndentingPrintWriter) output
                        : IndentingPrintWriter.wrap(output, diagram.config.indentation());
        return writeTo(indentingPrintWriter);
    }

    protected IndentingPrintWriter writeChildrenTo(IndentingPrintWriter output) {
        final IndentingPrintWriter indented = output.indent();
        for (Renderer child : children) {
            child.writeTo(indented);
        }
        return output;
    }

    protected Renderer lastChild() {
        // TODO: Check what the impact would be to change children into a List (is that even desirable?).
        Renderer last = null;
        for (Renderer child : children) last = child;
        return last;
    }

    protected static IndentingPrintWriter writeTypeTo(IndentingPrintWriter out, Type type) {
        if (type != null) {
            out.append(type.typeName());
            final ParameterizedType parameterizedType = type.asParameterizedType();
            if (parameterizedType != null) {
                final Type[] generics = parameterizedType.typeArguments();
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
            out.append(type.dimension());
        }
        return out;
    }

    /**
     * This method puts the given {@code value} within double-quotes
     * and escapes any double-quotes that may be already in the string.
     * <p>
     * The method returns an empty String ({@code ""}) when the given {@code value}
     * is either {@code null} or empty. This is intentional as it allows for unconditional appending.
     *
     * @param value The value to be quoted.
     * @return The value within double quotes or an empty string if the value was null or empty.
     */
    public static String quoted(String value) {
        return value == null || value.trim().isEmpty() ? "" : '"' + value.replaceAll("\"", "\\\"") + '"';
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

}
