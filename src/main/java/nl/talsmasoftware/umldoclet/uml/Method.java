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
import nl.talsmasoftware.umldoclet.uml.configuration.TypeDisplay;

import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class Method extends TypeMember {

    private final Parameters parameters;

    public Method(Type containingType, Visibility visibility, boolean isAbstract, boolean isStatic,
                  String name, Parameters parameters, TypeName returnType) {
        this(containingType, visibility, isAbstract, isStatic, false, name, parameters, returnType);
    }

    private Method(Type containingType, Visibility visibility, boolean isAbstract, boolean isStatic, boolean isDeprecated,
                   String name, Parameters parameters, TypeName returnType) {
        super(containingType, visibility, isAbstract, isStatic, isDeprecated, name, returnType);
        this.parameters = requireNonNull(parameters, () -> "No parameters for method " + containingType.name + "." + name);
        this.parameters.setMethod(this);
    }

    public Method deprecated() {
        return new Method(containingType, visibility, isAbstract, isStatic, true, name, parameters, type);
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeParametersTo(IPW output) {
        return parameters.writeTo(output);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (getConfiguration().methods().include(visibility)) super.writeTo(output);
        return output;
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
    public int compareTo(TypeMember other) {
        return comparing(super::compareTo)
                .thenComparing(method -> ((Method) method).parameters)
                .compare(this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parameters);
    }

}
