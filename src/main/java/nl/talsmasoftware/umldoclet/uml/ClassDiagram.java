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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.File;

/// UML diagram for a single class.
public class ClassDiagram extends Diagram {

    private File pumlFile = null;

    /// Creates a new UML diagram for a single class.
    ///
    /// @param config The configuration to use.
    /// @param type   The type to generate the diagram for.
    public ClassDiagram(Configuration config, Type type) {
        super(config);
        addChild(type);
    }

    /// Return the type information of the class for which this diagram is generated.
    ///
    /// @return The type for which this diagram is generated.
    public Type getType() {
        return getChildren().stream()
                .filter(Type.class::isInstance).map(Type.class::cast)
                .findFirst().orElseThrow(() -> new IllegalStateException("No Type defined in Class diagram!"));
    }

    /// Adds a new child to this class diagram.
    ///
    /// @param child The child to add to this diagram.
    @Override
    public void addChild(UMLNode child) {
        super.addChild(child);
        if (child instanceof Type) ((Type) child).setIncludePackagename(true);
    }

    /// Write the child UML nodes for the diagram.
    ///
    /// @param output The output to write to.
    /// @return The output for chaining purposes.
    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        output.append("set namespaceSeparator none").newline()
                .append("hide empty fields").newline()
                .append("hide empty methods").newline()
                .newline();
        return super.writeChildrenTo(output);
    }

    /// Returns the file for the PlantUML code.
    ///
    /// @return The file containing the PlantUML code.
    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            final Type type = getType();
            StringBuilder result = new StringBuilder(getConfiguration().destinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            type.getModulename().ifPresent(modulename -> result.append(modulename).append('/'));
            String containingPackage = type.getPackagename();
            result.append(containingPackage.replace('.', '/')).append('/');
            if (type.getName().qualified.startsWith(containingPackage + ".")) {
                result.append(type.getName().qualified.substring(containingPackage.length() + 1));
            } else {
                result.append(type.getName().simple);
            }
            pumlFile = new File(result.append(".puml").toString());
        }
        return pumlFile;
    }

}
