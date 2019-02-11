/*
 * Copyright 2016-2019 Talsma ICT
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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingRenderer;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Collections.newSetFromMap;
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
public abstract class UMLNode implements IndentingRenderer {
    private UMLNode parent;
    private final List<UMLNode> children = new ArrayList<>();

    protected UMLNode(UMLNode parent) {
        this.parent = parent;
    }

    public UMLNode getParent() {
        return parent;
    }

    public void setParent(UMLNode parent) {
        this.parent = parent;
    }

//    protected UMLNode getRootUMLPart() {
//        UMLNode parent = getParent();
//        return parent == null || parent == this ? this : parent.getRootUMLPart();
//    }

    public List<UMLNode> getChildren() {
        return children;
    }

    public void addChild(UMLNode child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChildren(Predicate<? super UMLNode> condition) {
        children.removeIf(condition);
    }

    protected <U extends UMLNode> Optional<U> findParent(Class<U> nodeType) {
        final Set<UMLNode> traversed = newSetFromMap(new IdentityHashMap<>());
        for (UMLNode parent = getParent();
             parent != null && traversed.add(parent);
             parent = parent.getParent()) {
            if (nodeType.isInstance(parent)) return Optional.of(nodeType.cast(parent));
        }
        return Optional.empty();
    }

    protected Configuration getConfiguration() {
        return findParent(UMLRoot.class)
                .map(UMLRoot::getConfiguration)
                .orElseThrow(() -> new IllegalStateException("Cannot obtain configuration!"));
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
        Collection<? extends UMLNode> children = getChildren();
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
