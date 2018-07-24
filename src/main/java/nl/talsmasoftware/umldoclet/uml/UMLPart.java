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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingRenderer;
import nl.talsmasoftware.umldoclet.configuration.Configuration;

import java.io.StringWriter;
import java.util.Collection;

import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;

/**
 * Smallest 'independent' part of an UML diagram that can be rendered,
 * serves as a reusable base-class for all other UML parts.
 * <p>
 * UML parts are capable of rendering themselves to {@link IndentingPrintWriter} instances and have
 * chaining methods returning these writers for easier appending.
 *
 * @author Sjoerd Talsma
 */
public abstract class UMLPart implements IndentingRenderer {
    private UMLPart parent;

    protected UMLPart(UMLPart parent) {
        this.parent = parent;
    }

    public UMLPart getParent() {
        return parent;
    }

    void setParent(UMLPart parent) {
        this.parent = parent;
    }

    protected UMLPart requireParent() {
        return requireNonNull(parent, () -> getClass().getSimpleName() + " seems to be an orphan, it has no parent.");
    }

    protected UMLFile getRootUMLPart() {
        return requireParent().getRootUMLPart();
    }

    /**
     * To be overridden by parts that actually have children.
     *
     * @return The children for this renderer.
     */
    public Collection<? extends UMLPart> getChildren() {
        return emptySet();
    }

    public void addChild(UMLPart child) {
        Collection<UMLPart> children = (Collection<UMLPart>) getChildren();
        children.add(child);
        child.setParent(this);
    }

    protected Configuration getConfiguration() {
        return getRootUMLPart().config;
    }

    protected Indentation getIndentation() {
        return parent == null ? Indentation.DEFAULT : parent.getIndentation();
    }

    /**
     * Helper method to write all children to the specified output.
     * <p>
     * By default children will be {@link #writeTo(IndentingPrintWriter) written}
     * with increased indentation for legibility.
     *
     * @param <IPW>  The subclass of indenting print writer being written to.
     * @param output The output to write the children to.
     * @return A reference to the output for method chaining purposes.
     */
    protected <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        Collection<? extends UMLPart> children = getChildren();
        if (children != null && !children.isEmpty()) {
            IndentingPrintWriter indented = output.indent();
            children.forEach(child -> child.writeTo(indented));
        }
        return output;
    }

    /**
     * Renders the entire content of this renderer and returns it as a String value.
     *
     * @return The rendered content of this renderer.
     */
    public String toString() {
        return writeTo(IndentingPrintWriter.wrap(new StringWriter(), getIndentation())).toString();
    }

}
