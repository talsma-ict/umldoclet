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

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/// UML namespace
///
/// This corresponds to a 'package' in the Java world.
///
/// @author Sjoerd Talsma
public class Namespace extends UMLNode {

    /// The module containing the package, if known. Otherwise `null`.
    private final String moduleName;
    /// The name of this [Namespace] (or java 'package').
    public final String name;

    /// Constructor for java package.
    ///
    /// @param parent     The parent, this should be a package, module or diagram.
    /// @param name       The name of this namespace.
    /// @param moduleName The name of the module this package belongs to (if known, otherwise `null`).
    public Namespace(UMLNode parent, String name, String moduleName) {
        super(parent);
        this.name = requireNonNull(name, "Package name is <null>.").trim();
        this.moduleName = moduleName;
    }

    /// Return the module name this package belongs to if known, otherwise [Optional#empty()].
    ///
    /// @return The module name this package belongs to.
    public Optional<String> getModuleName() {
        return Optional.ofNullable(moduleName);
    }

    /// Adds the package name to the diagram.
    ///
    /// Re: bug 107: If the package name is empty (i.e. the 'default' package),
    /// render `"unnamed"` because an empty name is not valid in PlantUML.
    ///
    /// @param output The output to append the package name to.
    /// @param <IPW>  The type of the output object.
    /// @return The same output instance for method chaining.
    private <IPW extends IndentingPrintWriter> IPW writeNameTo(IPW output) {
        output.append(name.isEmpty() ? "unnamed" : name).whitespace();
        return output;
    }

    /// Write the java package to the diagram output.
    ///
    /// @param output The output to write to.
    /// @return The output for chaining purposes.
    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        writeNameTo(output.append("package").whitespace()).append('{').newline();
        writeChildrenTo(output.indent());
        output.append('}').newline();
        return output;
    }

    /// Check whether this package contains the specified type.
    ///
    /// @param typeName The name of the type to check.
    /// @return `true` if the specified type is contained in this package, otherwise `false`.
    public boolean contains(TypeName typeName) {
        return typeName != null && typeName.qualified.startsWith(this.name + ".");
    }

    /// Hashcode for the package. This is based on the package name.
    ///
    /// @return hashcode for the package.
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /// Return whether this package equals another object.
    ///
    /// @param other The object to compare this namespace to.
    /// @return `true` if and only if the other object is also a [Namespace] instance and has the same name.
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Namespace && name.equals(((Namespace) other).name));
    }
}
