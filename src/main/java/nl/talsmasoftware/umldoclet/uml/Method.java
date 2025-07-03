/*
 * Copyright 2016-2025 Talsma ICT
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

import java.util.Objects;

/**
 * UML representation of a method.
 *
 * @author Sjoerd Talsma
 */
public class Method extends TypeMember {

    /**
     * If this method is an abstract method.
     */
    public boolean isAbstract;

    /**
     * Create a new method in the containing type with a specific name and return type.
     *
     * @param containingType The containing type the member is part of.
     * @param name           The name of the method.
     * @param returnType     The name of the return type.
     */
    public Method(Type containingType, String name, TypeName returnType) {
        super(containingType, name, returnType);
    }

    private Parameters getOrCreateParameters() {
        return getChildren().stream()
                .filter(Parameters.class::isInstance).map(Parameters.class::cast)
                .findFirst()
                .orElseGet(this::createAndAddNewParameters);
    }

    private Parameters createAndAddNewParameters() {
        Parameters parameters = new Parameters(this);
        this.addChild(parameters);
        return parameters;
    }

    /**
     * Add a parameter to this method.
     *
     * @param name The name of the parameter.
     * @param type The type of the parameter.
     */
    public void addParameter(String name, TypeName type) {
        getOrCreateParameters().add(name, type);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (!getConfiguration().methods().include(getVisibility())) return output;
        if (isAbstract) output.append("{abstract}").whitespace();
        return super.writeTo(output);
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeParametersTo(IPW output) {
        return getOrCreateParameters().writeTo(output);
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeTypeTo(IPW output) {
        TypeDisplay returnTypeDisplay = getConfiguration().methods().returnType();
        if (type != null && !TypeDisplay.NONE.equals(returnTypeDisplay)) {
            output.append(": ").append(type.toUml(returnTypeDisplay, null));
        }
        return output;
    }

    @Override
    void replaceParameterizedType(TypeName from, TypeName to) {
        super.replaceParameterizedType(from, to);
        getOrCreateParameters().replaceParameterizedType(from, to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getOrCreateParameters());
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other)
                && getOrCreateParameters().equals(((Method) other).getOrCreateParameters());
    }

}
