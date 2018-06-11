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

import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * Model object for a Field or Method in an UML class.
 *
 * @author Sjoerd Talsma
 */
public abstract class TypeMember extends UMLPart implements Comparable<TypeMember> {

    protected final Type containingType;
    protected final Visibility visibility;
    protected final boolean isAbstract, isStatic, isDeprecated;
    public final String name;
    protected final TypeName type;

    public TypeMember(Type containingType, Visibility visibility, boolean isAbstract, boolean isStatic,
                      boolean isDeprecated, String name, TypeName type) {
        super(containingType);
        this.containingType = requireNonNull(containingType, "Containing type is <null>.");
        this.visibility = requireNonNull(visibility, "Member visibility is <null>.");
        this.isAbstract = isAbstract;
        this.isStatic = isStatic;
        this.isDeprecated = isDeprecated;
        this.name = requireNonNull(name, "Member name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Member name is empty.");
        this.type = type;
    }

    protected <IPW extends IndentingPrintWriter> IPW writeTypeTo(IPW output) {
        if (type != null) output.append(": ").append(type.toString());
        return output;
    }

    protected <IPW extends IndentingPrintWriter> IPW writeParametersTo(IPW output) {
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (isAbstract) output.append("{abstract}").whitespace();
        if (isStatic) output.append("{static}").whitespace();
        output.append(visibility.toUml());
        if (isDeprecated) output.append("--").append(name).append("--");
        else output.append(name);
        writeParametersTo(output);
        writeTypeTo(output);
        output.newline();
        return output;
    }

    @Override
    public int compareTo(TypeMember other) {
        return comparing((TypeMember member) -> member.getClass().getSimpleName())
                .thenComparing(member -> member.name.toLowerCase())
                .thenComparing(member -> member.name)
                .compare(this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containingType, name);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof TypeMember && this.compareTo((TypeMember) other) == 0);
    }

}
