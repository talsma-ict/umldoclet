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
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingRenderer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Type extends UMLRenderer implements IndentingRenderer.WithChildren, Comparable<Type> {

    public final Package containingPackage;
    public final TypeClassification classfication;
    public final TypeName name;
    protected final Set<Renderer> children = new LinkedHashSet<>();

    public Type(Package containingPackage, TypeClassification classification, TypeName name) {
        super(requireNonNull(containingPackage, "Containing package is <null>.").config);
        this.containingPackage = containingPackage;
        this.classfication = requireNonNull(classification, "Type classification is <null>.");
        this.name = requireNonNull(name, "Type name is <null>.");
    }

    @Override
    public Collection<? extends Renderer> getChildren() {
        return children;
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        classfication.writeTo(output).whitespace();
        name.writeTo(output).whitespace();
        if (!children.isEmpty()) writeChildrenTo(output.append('{').newline()).append('}');
        return output.newline();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int compareTo(Type other) {
        return name.compareTo(requireNonNull(other, "Cannot compare Type to <null>.").name);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Type && this.compareTo((Type) other) == 0);
    }

}
