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

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.uml.Namespace.NameSpaceAware;
import nl.talsmasoftware.umldoclet.uml.configuration.TypeDisplay;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Type extends UMLPart implements NameSpaceAware, Comparable<Type> {

    public final Classification classfication;
    public final TypeName name;
    protected final boolean isDeprecated;
    protected final Set<UMLPart> children = new LinkedHashSet<>();

    public Type(Namespace namespace, Classification classification, TypeName name) {
        this(namespace, classification, name, false, null);
    }

    private Type(Namespace namespace, Classification classification, TypeName name, boolean isDeprecated,
                 Collection<? extends UMLPart> children) {
        super(requireNonNull(namespace, "Containing package is <null>."));
        this.classfication = requireNonNull(classification, "Type classification is <null>.");
        this.name = requireNonNull(name, "Type name is <null>.");
        this.isDeprecated = isDeprecated;
        if (children != null) this.children.addAll(children);
    }

    public Type deprecated() {
        return new Type(getNamespace(), classfication, name, true, children);
    }

    public Namespace getNamespace() {
        return (Namespace) parent;
    }

    @Override
    public Collection<? extends UMLPart> getChildren() {
        return children;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output, Namespace namespace) {
        output.append(classfication.toUml()).whitespace();
        output.append(name.toUml(TypeDisplay.QUALIFIED, namespace)).whitespace();
        if (isDeprecated) output.append("<<deprecated>>").whitespace();
        writeChildrenTo(output).newline();
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        if (!children.isEmpty()) super.writeChildrenTo(output.append('{').newline()).append('}');
        return output;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(Type other) {
        return name.compareTo(requireNonNull(other, "Cannot compare Type to <null>.").name);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Type && this.compareTo((Type) other) == 0);
    }

    /**
     * Classification of a UML Type.
     *
     * @author Sjoerd Talsma
     */
    public static enum Classification {
        ENUM, INTERFACE, ANNOTATION, ABSTRACT_CLASS, CLASS;

        public String toUml() {
            return name().toLowerCase().replace('_', ' ');
        }

    }
}
