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

import nl.talsmasoftware.umldoclet.configuration.TypeDisplay;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.uml.Namespace.NameSpaceAware;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Type extends UMLPart implements NameSpaceAware, Comparable<Type> {
    /**
     * Classification of a UML Type.
     *
     * @author Sjoerd Talsma
     */
    public enum Classification {
        ENUM, INTERFACE, ANNOTATION, ABSTRACT_CLASS, CLASS;

        public String toUml() {
            return name().toLowerCase().replace('_', ' ');
        }
    }

    private final Namespace namespace;
    private final Classification classfication;
    public final TypeName name;
    private final boolean isDeprecated, addPackageToName;
    private final Set<UMLPart> children = new LinkedHashSet<>();

    public Type(Namespace namespace, Classification classification, TypeName name) {
        this(namespace, classification, name, false, false, null);
    }

    private Type(Namespace namespace, Classification classification, TypeName name, boolean isDeprecated,
                 boolean addPackageToName, Collection<? extends UMLPart> children) {
        super(namespace);
        this.namespace = requireNonNull(namespace, "Containing package is <null>.");
        this.classfication = requireNonNull(classification, "Type classification is <null>.");
        this.name = requireNonNull(name, "Type name is <null>.");
        this.isDeprecated = isDeprecated;
        this.addPackageToName = addPackageToName;
        if (children != null) this.children.addAll(children);
    }

    public Type deprecated() {
        return new Type(getNamespace(), classfication, name, true, addPackageToName, children);
    }

    public Type addPackageToName() {
        return new Type(getNamespace(), classfication, name, isDeprecated, true, children);
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public Classification getClassfication() {
        return classfication;
    }

    @Override
    public Collection<? extends UMLPart> getChildren() {
        return children;
    }

    @Override
    void setParent(UMLPart parent) {
        super.setParent(parent);
        if (namespace.getParent() == null) namespace.setParent(parent);
    }

    private <IPW extends IndentingPrintWriter> IPW writeNameTo(IPW output, Namespace namespace) {
        if (addPackageToName && name.qualified.startsWith(this.namespace.name + '.')) {
            String nameInPackage = name.qualified.substring(this.namespace.name.length() + 1);
            output.append("\"<size:14>").append(nameInPackage)
                    .append("\\n<size:10>").append(this.namespace.name)
                    .append("\" as ");
        }
        output.append(name.toUml(TypeDisplay.QUALIFIED, namespace));
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output, Namespace namespace) {
        output.append(classfication.toUml()).whitespace();
        writeNameTo(output, namespace).whitespace();
        if (isDeprecated) output.append("<<deprecated>>").whitespace();
        writeLinkTo(output).whitespace();
        writeChildrenTo(output).newline();
        return output;
    }

    private Optional<Namespace> diagramPackage() {
        UMLRoot diagram = getRootUMLPart();
        if (diagram instanceof PackageUml) {
            return Optional.of(new Namespace(diagram, ((PackageUml) diagram).packageName));
        } else if (diagram instanceof ClassUml) {
            return Optional.of(((ClassUml) diagram).type.namespace);
        }
        return Optional.empty();
    }

    private <IPW extends IndentingPrintWriter> IPW writeLinkTo(IPW output) {
        Optional<String> relativeNameToDiagram = diagramPackage()
                .filter(ns -> name.qualified.startsWith(ns.name + '.'))
                .map(ns -> name.qualified.substring(ns.name.length() + 1));
        if (relativeNameToDiagram.isPresent()) {
            output.append("[[").append(relativeNameToDiagram.get()).append(".html]]");
        } else {
            output.append("[[fqn:").append(name.qualified).append(".html]]");
        }
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

}
