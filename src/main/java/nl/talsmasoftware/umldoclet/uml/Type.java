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

import nl.talsmasoftware.umldoclet.configuration.TypeDisplay;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Collection;
import java.util.Optional;

import static java.util.Locale.ENGLISH;
import static java.util.Objects.requireNonNull;

/// Model object for a Type in an UML diagram.
///
/// A type represents a class, interface, enum, or annotation.
///
/// @author Sjoerd Talsma
public class Type extends UMLNode {
    /// Classification of a UML Type.
    ///
    /// @author Sjoerd Talsma
    public enum Classification {
        /// An enumeration type.
        ENUM,
        /// An interface type.
        INTERFACE,
        /// An annotation type.
        ANNOTATION,
        /// An abstract class.
        ABSTRACT_CLASS,
        /// A regular class.
        CLASS;

        /// Returns the UML representation of this classification.
        ///
        /// @return The UML representation.
        public String toUml() {
            return name().toLowerCase(ENGLISH).replace('_', ' ');
        }
    }

    private final Namespace packageNamespace;
    private final Classification classfication;
    private TypeName name;
    private boolean isDeprecated;
    private boolean includePackagename;
    private Link link;

    /// Creates a new type.
    ///
    /// @param namespace      The package namespace this type belongs to.
    /// @param classification The classification of this type.
    /// @param name           The name of this type.
    public Type(Namespace namespace, Classification classification, TypeName name) {
        this(namespace, classification, name, false, false, null);
    }

    private Type(Namespace namespace, Classification classification, TypeName name, boolean isDeprecated,
                 boolean addPackageToName, Collection<? extends UMLNode> children) {
        super(namespace);
        this.packageNamespace = requireNonNull(namespace, "Containing package is <null>.");
        this.classfication = requireNonNull(classification, "Type classification is <null>.");
        this.name = requireNonNull(name, "Type name is <null>.");
        this.isDeprecated = isDeprecated;
        this.includePackagename = addPackageToName;
        if (children != null) children.forEach(this::addChild);
    }

    /// @return The name of this type.
    public TypeName getName() {
        return name;
    }

    /// Updates the generic type variables of this type.
    ///
    /// @param name The new name including potentially updated generic type variables.
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

    /// Marks this type as deprecated.
    ///
    /// @return This type instance.
    public Type deprecated() {
        this.isDeprecated = true;
        return this;
    }

    /// Sets whether the package name should be included in the type name.
    ///
    /// @param include `true` if the package name should be included.
    public void setIncludePackagename(boolean include) {
        this.includePackagename = include;
    }

    /// @return The module name of this type, if available.
    public Optional<String> getModulename() {
        return packageNamespace.getModuleName();
    }

    /// @return The package name of this type.
    public String getPackagename() {
        return packageNamespace.name;
    }

    /// @return The classification of this type.
    public Classification getClassfication() {
        return classfication;
    }

    private <IPW extends IndentingPrintWriter> IPW writeNameTo(IPW output) {
        if (includePackagename && name.qualified.startsWith(this.packageNamespace.name + '.')) {
            String nameInPackage = name.qualified.substring(this.packageNamespace.name.length() + 1);
            output.append("\"<size:14>").append(nameInPackage)
                    .append("\\n<size:10>").append(this.packageNamespace.name)
                    .append("\" as ");
        }

        // Namespace aware compensation // TODO Simplify this package logic and make sure all is still needed!
        Namespace namespace = findParent(Namespace.class).orElse(null);
        output.append(name.toUml(TypeDisplay.QUALIFIED, namespace));
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append(classfication.toUml()).whitespace();
        writeNameTo(output).whitespace();
        if (isDeprecated) output.append("<<deprecated>>").whitespace();
        link().writeTo(output).whitespace();
        writeChildrenTo(output);
        output.newline();
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        if (!getChildren().isEmpty() && !Classification.ANNOTATION.equals(classfication)) {
            output.append('{').newline();
            super.writeChildrenTo(output.indent());
            output.append('}');
        }
        return output;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Type && this.name.equals(((Type) other).name));
    }

}
