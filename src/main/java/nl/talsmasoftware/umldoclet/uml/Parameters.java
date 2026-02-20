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

import nl.talsmasoftware.umldoclet.configuration.MethodConfig;
import nl.talsmasoftware.umldoclet.configuration.TypeDisplay;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

/// Model object for the parameters of a method in a UML diagram.
///
/// @author Sjoerd Talsma
public class Parameters extends UMLNode {

    private boolean varargs = false;

    /// Creates a new parameters node.
    ///
    /// @param parent The parent method node.
    public Parameters(UMLNode parent) {
        super(parent);
    }

    @Override
    public void addChild(UMLNode child) {
        if (child instanceof Parameter) super.addChild(child);
    }

    /// Adds a parameter to this collection.
    ///
    /// @param name The name of the parameter.
    /// @param type The type of the parameter.
    /// @return Reference to this parameters node for method chaining.
    public Parameters add(String name, TypeName type) {
        addChild(new Parameter(name, type));
        return this;
    }

    /// Sets whether the last parameter is a varargs parameter.
    ///
    /// @param varargs `true` if the last parameter is a varargs parameter.
    /// @return Reference to this parameters node for method chaining.
    public Parameters varargs(boolean varargs) {
        this.varargs = varargs;
        return this;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        return writeChildrenTo(output);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        output.append('(');
        String sep = "";
        for (UMLNode param : getChildren()) {
            param.writeTo(output.append(sep));
            sep = ", ";
        }
        output.append(')');
        return output;
    }

    /// Replaces a parameterized type with another type in all parameters.
    ///
    /// @param from The type to replace.
    /// @param to   The new type.
    void replaceParameterizedType(TypeName from, TypeName to) {
        if (from != null) {
            getChildren().stream()
                    .filter(Parameter.class::isInstance).map(Parameter.class::cast)
                    .filter(p -> from.equals(p.type))
                    .forEach(p -> p.type = to);
        }
    }

    /// A single parameter in a method.
    public class Parameter extends UMLNode {
        private final String name;
        private TypeName type;

        private Parameter(String name, TypeName type) {
            super(Parameters.this);
            this.name = name;
            this.type = type;
        }

        @Override
        public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
            String sep = "";
            MethodConfig methodConfig = getConfiguration().methods();
            if (name != null && MethodConfig.ParamNames.BEFORE_TYPE.equals(methodConfig.paramNames())) {
                output.append(name);
                sep = ": ";
            }
            if (type != null && !TypeDisplay.NONE.equals(methodConfig.paramTypes())) {
                String typeUml = type.toUml(methodConfig.paramTypes(), null);
                if (varargs && typeUml.endsWith("[]")) typeUml = typeUml.substring(0, typeUml.length() - 2) + "...";
                output.append(sep).append(typeUml);
                sep = " ";
            }
            if (name != null && MethodConfig.ParamNames.AFTER_TYPE.equals(methodConfig.paramNames())) {
                output.append(sep).append(name);
            }
            return output;
        }
    }
}
