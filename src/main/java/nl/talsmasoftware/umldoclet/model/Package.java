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

import nl.talsmasoftware.umldoclet.rendering.Renderer;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.model.Type.createType;

/**
 * @author Sjoerd Talsma
 */
public class Package extends UMLRenderer {

    protected final String name;
    protected final Set<Renderer> children = new LinkedHashSet<>();
    private final Set<Reference> references = new LinkedHashSet<>();

    static Package createPackage(UMLDiagram diagram, PackageElement packageElement) {
        Package pkg = new Package(diagram, packageElement.getQualifiedName().toString());

        // Add all types contained in this package.
        packageElement.getEnclosedElements().stream()
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .map(type -> createType(pkg, type))
                .forEach(pkg.children::add);

        pkg.children.stream()
                .filter(Type.class::isInstance).map(Type.class::cast)
                .flatMap(type -> type.references.stream().map(Reference::canonical))
                .forEach(pkg.references::add);

        return pkg;
    }

    Package(UMLDiagram diagram, String name) {
        super(diagram);
        this.name = requireNonNull(name, "Package name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Package name is empty.");
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        IndentingPrintWriter indented = output.append("namespace").whitespace().append(name).whitespace()
                .append('{').newline().indent();
        children.forEach(child -> child.writeTo(indented).newline());
        references.stream().map(Object::toString).forEach(ref -> indented.append(ref).newline());
        return indented.newline().unindent().append('}').newline();
    }

}
