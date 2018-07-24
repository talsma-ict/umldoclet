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

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * UML namespace
 * <p>
 * This corresponds to a 'package' in the Java world.
 *
 * @author Sjoerd Talsma
 */
public class Namespace extends UMLPart implements Comparable<Namespace> {

    public final String name;
    private final Collection<UMLPart> children = new ArrayList<>();

    public Namespace(UMLRoot diagram, String name) {
        super(diagram);
        this.name = requireNonNull(name, "Package name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Package name is empty.");
    }

    @Override
    public Collection<? extends UMLPart> getChildren() {
        return children;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append("namespace").whitespace().append(name).whitespace().append('{').newline();
        writeChildrenTo(output);
        output.append('}').newline();
        return output;
    }

    public boolean contains(TypeName typeName) {
        return typeName != null && typeName.qualified.startsWith(this.name + ".");
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(Namespace other) {
        requireNonNull(other, "Cannot compare with namespace <null>.");
        int diff = name.compareToIgnoreCase(other.name);
        return diff == 0 ? name.compareTo(other.name) : diff;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Namespace && this.compareTo((Namespace) other) == 0);
    }
}
