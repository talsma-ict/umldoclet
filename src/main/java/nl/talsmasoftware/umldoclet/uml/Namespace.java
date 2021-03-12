/*
 * Copyright 2016-2021 Talsma ICT
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

import static java.util.Objects.requireNonNull;

/**
 * UML namespace
 * <p>
 * This corresponds to a 'package' in the Java world.
 *
 * @author Sjoerd Talsma
 */
public class Namespace extends UMLNode {

    public final String name;

    public Namespace(UMLNode parent, String name) {
        super(parent);
        this.name = requireNonNull(name, "Package name is <null>.").trim();
    }

    /**
     * Adds the package name to the diagram.
     * Re: bug 107: If the package name is empty (i.e. the 'default' package),
     * render {@code "unnamed"} because an empty name is not valid in PlantUML.
     *
     * @param output The output to append the package name to.
     * @param <IPW>  The type of the output object.
     * @return The same output instance for method chaining.
     */
    private <IPW extends IndentingPrintWriter> IPW writeNameTo(IPW output) {
        output.append(name.isEmpty() ? "unnamed" : name).whitespace();
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        writeNameTo(output.append("namespace").whitespace()).append('{').newline();
        writeChildrenTo(output.indent());
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
    public boolean equals(Object other) {
        return this == other || (other instanceof Namespace && name.equals(((Namespace) other).name));
    }
}
