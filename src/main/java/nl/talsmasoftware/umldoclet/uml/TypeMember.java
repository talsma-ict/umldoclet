/*
 * Copyright 2016-2019 Talsma ICT
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

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Model object for a Field or Method in an UML class.
 *
 * @author Sjoerd Talsma
 */
public abstract class TypeMember extends UMLNode {

    public final String name;
    protected TypeName type;
    private Visibility visibility;
    public boolean isAbstract, isStatic, isDeprecated;

    protected TypeMember(Type containingType, String name, TypeName type) {
        super(containingType);
        this.name = requireNonNull(name, "Member name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Member name is empty.");
        this.type = type;
    }

    public Visibility getVisibility() {
        return visibility == null ? Visibility.PUBLIC : visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    protected <IPW extends IndentingPrintWriter> IPW writeTypeTo(IPW output) {
        if (type != null) {
            output.append(": ").append(type.toString());
        }
        return output;
    }

    void replaceParameterizedType(TypeName from, TypeName to) {
        if (from != null && from.equals(this.type)) {
            this.type = to;
        }
    }

    protected <IPW extends IndentingPrintWriter> IPW writeParametersTo(IPW output) {
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (isAbstract) output.append("{abstract}").whitespace();
        if (isStatic) output.append("{static}").whitespace();
        output.append(getVisibility().toUml());
        if (isDeprecated) output.append("--").append(name).append("--");
        else output.append(name);
        writeParametersTo(output);
        writeTypeTo(output);
        output.newline();
        return output;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), name);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other != null && getClass().equals(other.getClass())
                && Objects.equals(getParent(), ((TypeMember) other).getParent())
                && name.equals(((TypeMember) other).name)
        );
    }

}
