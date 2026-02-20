/*
 * Copyright 2016-2026 Talsma ICT
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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Collections.newSetFromMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/// Part of an UML diagram that can render itself to the diagram by
/// [writing to][#writeTo(IndentingPrintWriter)] an indenting writer.
/// It serves as a reusable base-class for all specific UML nodes.
///
///
/// UML nodes are capable of rendering themselves to [IndentingPrintWriter].
///
/// @author Sjoerd Talsma
public abstract class UMLNode {

    private UMLNode parent;
    private final List<UMLNode> children = new ArrayList<>();

    /// Constructor for a new UML node with reference to its `parent`.
    ///
    /// @param parent The parent for this node, or `null` for the root node for a diagram.
    protected UMLNode(UMLNode parent) {
        this.parent = parent;
    }

    ///  Returns the parent node of this UML node, or `null` if there is none.
    ///
    /// @return The parent node of this UML node.
    public UMLNode getParent() {
        return parent;
    }

    /// Sets the parent node of this UML node.
    ///
    /// @param parent The parent node to set.
    public void setParent(UMLNode parent) {
        this.parent = parent;
    }

    /// Finds a parent node of a particular type.
    ///
    /// @param nodeType The type of parent node to find.
    /// @param <U>      The type of parent node to find.
    /// @return The parent node of the specified type, if found.
    protected <U extends UMLNode> Optional<U> findParent(Class<U> nodeType) {
        final Set<UMLNode> traversed = newSetFromMap(new IdentityHashMap<>());
        for (UMLNode parent = getParent();
             parent != null && traversed.add(parent);
             parent = parent.getParent()) {
            if (nodeType.isInstance(parent)) return Optional.of(nodeType.cast(parent));
        }
        return Optional.empty();
    }

    /// Returns the children of this UML node.
    ///
    /// @return The children of this UML node as unmodifiable list.
    public List<UMLNode> getChildren() {
        return unmodifiableList(children);
    }

    /// Returns all children that are an instance of a particular type.
    ///
    /// @param type The type of `UMLNode` to return (required, non-null).
    /// @param <T>  The type of children to obtain.
    /// @return The filtered list of children of this uml node (unmodifiable).
    public <T extends UMLNode> List<T> getChildren(Class<T> type) {
        return unmodifiableList(getChildren().stream().filter(type::isInstance).map(type::cast).collect(toList()));
    }

    /// Add a child to this node.
    ///
    /// @param child The child node to add to this node.
    public void addChild(UMLNode child) {
        children.add(child);
        child.setParent(this);
    }

    /// Remove children if they meet the specified condition.
    ///
    /// @param condition The condition to check children with. They get removed if the condition returns `true`.
    /// @return `true` if at least 1 child was removed, otherwise `false`.
    public boolean removeChildren(Predicate<? super UMLNode> condition) {
        return children.removeIf(condition);
    }

    /// Whether this UML node is empty.
    ///
    /// By default, this is considered `true` if all [children][#getChildren()] in the node are empty.
    ///
    /// @return `true` if the node is empty, otherwise `false`.
    public boolean isEmpty() {
        return getChildren().stream().allMatch(UMLNode::isEmpty);
    }

    /// Obtain the doclet configuration from the diagram this node is part of
    ///
    /// @return The doclet configuration.
    protected Configuration getConfiguration() {
        return findParent(Diagram.class)
                .map(Diagram::getConfiguration)
                .orElseThrow(() -> new IllegalStateException("Cannot obtain configuration!"));
    }

    /// Renders this object to the given indenting `output`.
    ///
    /// @param <IPW>  The subclass of indenting print writer being written to.
    /// @param output The output to render this object to.
    /// @return A reference to the output for method chaining purposes.
    protected abstract <IPW extends IndentingPrintWriter> IPW writeTo(IPW output);

    /// Helper method to write all children to the specified output.
    ///
    /// @param <IPW>  The subclass of indenting print writer being written to.
    /// @param output The output to write the children to.
    /// @return A reference to the output for method chaining purposes.
    protected <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        getChildren().forEach(child -> child.writeTo(output));
        return output;
    }

    /// Renders the entire content of this renderer and returns it as a String value.
    ///
    /// @return The rendered content of this renderer.
    public String toString() {
        return writeTo(IndentingPrintWriter.wrap(new StringWriter(), indentation())).toString();
    }

    /// @return never-null indentation for use in toString
    private Indentation indentation() {
        try {
            return requireNonNull(getConfiguration().indentation());
        } catch (RuntimeException noConfig) {
            return Indentation.DEFAULT;
        }
    }
}
