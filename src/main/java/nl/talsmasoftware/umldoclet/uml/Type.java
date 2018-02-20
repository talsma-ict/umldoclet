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

import nl.talsmasoftware.umldoclet.uml.configuration.TypeDisplay;
import nl.talsmasoftware.umldoclet.rendering.UMLPart;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Type extends nl.talsmasoftware.umldoclet.uml.UMLPart implements Namespace.NameSpaceAware, Comparable<Type> {

    public final Namespace containingPackage;
    public final TypeClassification classfication;
    public final TypeName name;
    protected final Set<nl.talsmasoftware.umldoclet.rendering.UMLPart> children = new LinkedHashSet<>();

    public Type(Namespace namespace, TypeClassification classification, TypeName name) {
        super(requireNonNull(namespace, "Containing package is <null>.").config);
        this.containingPackage = namespace;
        this.classfication = requireNonNull(classification, "Type classification is <null>.");
        this.name = requireNonNull(name, "Type name is <null>.");
    }

    @Override
    public Collection<? extends UMLPart> getChildren() {
        return children;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output, Namespace namespace) {
        output.append(classfication.toUml()).whitespace();
        output.append(name.toUml(TypeDisplay.QUALIFIED, namespace)).whitespace();
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

}
