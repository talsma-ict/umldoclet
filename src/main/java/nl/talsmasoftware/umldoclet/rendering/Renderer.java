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

import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingDelegateWriter;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public abstract class Renderer {

    protected final UMLDocletConfig config;
    protected final UMLDiagram currentDiagram;
    protected final List<Renderer> children = new ArrayList<>();

    protected Renderer(UMLDocletConfig config, UMLDiagram currentDiagram) {
        this.config = requireNonNull(config, "No UML doclet configuration provided.");
        this.currentDiagram = validateDiagram(currentDiagram);
    }

    public abstract IndentingPrintWriter writeTo(IndentingPrintWriter output);

    public final Writer writeTo(Writer output) {
        return this.writeTo(output instanceof IndentingPrintWriter ? (IndentingPrintWriter) output :
                IndentingPrintWriter.wrap(
                        IndentingDelegateWriter.wrap(output).withIndentationWidth(config.indentation())));
    }

    protected IndentingPrintWriter writeChildrenTo(IndentingPrintWriter output) {
        final IndentingPrintWriter indented = output.indent();
        for (Renderer child : children) {
            child.writeTo(indented);
        }
        return output;
    }

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
