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

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class Method extends UMLRenderer {

    private final Type containingType;
    private final Visibility visibility;
    private final boolean isAbstract, isStatic;
    private final String name;
    private final Parameters parameters;
    private final TypeName returnType;

    public Method(Type containingType, Visibility visibility, boolean isAbstract, boolean isStatic,
                  String name, Parameters parameters, TypeName returnType) {
        super(requireNonNull(containingType, "Containing type is <null>.").config);
        this.containingType = containingType;
        this.visibility = requireNonNull(visibility, "Method visibility is <null>.");
        this.isAbstract = isAbstract;
        this.isStatic = isStatic;
        this.name = requireNonNull(name, "Method name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Method name is empty.");
        this.parameters = requireNonNull(parameters, "Method parameters are <null>.");
        this.returnType = returnType;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (isAbstract) output.append("{abstract}").whitespace();
        if (isStatic) output.append("{static}").whitespace();
        visibility.writeTo(output).append(name);
        parameters.writeTo(output);
        if (returnType != null) returnType.writeTo(output.append(": ").whitespace());
        output.newline();
        return output;
    }

}
