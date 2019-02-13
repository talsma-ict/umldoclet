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

import nl.talsmasoftware.umldoclet.configuration.TypeDisplay;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class Type extends UMLNode implements Comparable<Type> {
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

    private Namespace namespace;
    private Classification classfication;
    private TypeName name;
    private boolean isDeprecated, addPackageToName;
    private Link link;

    public Type(Namespace namespace, Classification classification, TypeName name) {
        this(namespace, classification, name, false, false, null);
    }

    private Type(Namespace namespace, Classification classification, TypeName name, boolean isDeprecated,
                 boolean addPackageToName, Collection<? extends UMLNode> children) {
        super(namespace);
        this.namespace = requireNonNull(namespace, "Containing package is <null>.");
        this.classfication = requireNonNull(classification, "Type classification is <null>.");
        this.name = requireNonNull(name, "Type name is <null>.");
        this.isDeprecated = isDeprecated;
        this.addPackageToName = addPackageToName;
        if (children != null) children.forEach(this::addChild);
    }

    public TypeName getName() {
        return name;
    }

    public void updateGenericTypeVariables(TypeName name) {
        if (name != null && name.qualified.equals(this.name.qualified)) {
            final TypeName[] generics = this.name.getGenerics();
            this.name = name;
            if (generics.length == name.getGenerics().length) {
                getChildren().stream()
                        .filter(TypeMember.class::isInstance).map(TypeMember.class::cast)
                        .forEach(member -> {
                            for (int i = 0; i < generics.length; i++) {
                                member.replaceParameterizedType(generics[i], name.getGenerics()[i]);
                            }
                        });
            }
        }
    }

    private Link link() {
        if (link == null) link = Link.forType(this);
        return link;
    }

    public Type deprecated() {
        return new Type(getNamespace(), classfication, name, true, addPackageToName, getChildren());
    }

    // TODO: Refactor this obscurity into something understandable
    void addPackageToName() {
        this.addPackageToName = true;
    }

    /**
     * @return The package for this class (not necessarily any parent node)
     * @deprecated // TODO Should probably be replaced by something like name.getPackage(env) instead!
     */
    @Deprecated
    public Namespace getNamespace() {
        return namespace;
    }

    public Classification getClassfication() {
        return classfication;
    }

    @Override
    public void setParent(UMLNode parent) {
        super.setParent(parent);
        // TODO: this probably shouldn't be necessary either??
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
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        // Namespace aware compensation
        final Namespace namespace = findParent(Namespace.class)
                .orElseGet(() -> findParent(PackageDiagram.class).map(pkg -> new Namespace(pkg, pkg.packageName))
                        .orElse(null));
        output.append(classfication.toUml()).whitespace();
        writeNameTo(output, namespace).whitespace();
        if (isDeprecated) output.append("<<deprecated>>").whitespace();
        link().writeTo(output).whitespace();
        writeChildrenTo(output).newline();
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        if (!getChildren().isEmpty()) super.writeChildrenTo(output.append('{').newline()).append('}');
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
