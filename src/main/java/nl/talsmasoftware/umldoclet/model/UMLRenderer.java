/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingRenderer;

import java.io.StringWriter;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation for renderers.
 * <p>
 * Renderers are capable of rendering themselves to {@link IndentingPrintWriter} instances and have
 * chaining methods returning these writers for easier appending.
 *
 * @author Sjoerd Talsma
 */
abstract class UMLRenderer implements IndentingRenderer {

    protected final UMLDiagram diagram;
//    protected final Collection<Renderer> children = new LinkedHashSet<>();

    UMLRenderer(UMLDiagram diagram) {
        // Only situation where a <null> diagram is accepted: the diagram itself.
        this.diagram = diagram == null && this instanceof UMLDiagram ? (UMLDiagram) this : diagram;
        requireNonNull(this.diagram, "UML diagram is <null>.");
    }

//    /**
//     * Helper method to write all children to the specified output.
//     * <p>
//     * By default children will be {@link #writeTo(IndentingPrintWriter) written}
//     * with increased indentation for legibility.
//     *
//     * @param output The output to write the children to.
//     * @return A reference to the output for method chaining purposes.
//     */
//    protected IndentingPrintWriter writeChildrenTo(IndentingPrintWriter output) {
//        final IndentingPrintWriter indented = output.indent();
//        children.forEach(child -> child.writeTo(indented));
//        return output;
//    }

//    /**
//     * This method puts the given {@code value} within double-quotes
//     * and escapes any double-quotes that may be already in the string.
//     * <p>
//     * The method returns an empty String ({@code ""}) when the given {@code value}
//     * is either {@code null} or empty. This is intentional as it allows for unconditional appending.
//     *
//     * @param value The value to be quoted.
//     * @return The value within double quotes or an empty string if the value was null or empty.
//     */
//    public static String quoted(String value) {
//        return value == null || value.trim().isEmpty() ? "" : '"' + value.replaceAll("\"", "\\\"") + '"';
//    }

    /**
     * Renders the entire content of this renderer and returns it as a String value.
     *
     * @return The rendered content of this renderer.
     */
    public String toString() {
        return writeTo(IndentingPrintWriter.wrap(new StringWriter(), diagram.config.getIndentation())).toString();
    }

}
