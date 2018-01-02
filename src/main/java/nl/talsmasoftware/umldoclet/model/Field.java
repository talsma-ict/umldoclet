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

import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * Model object for a Field in an UML class.
 *
 * @author Sjoerd Talsma
 */
public class Field extends UMLRenderer implements Comparable<Field> {

    private final Type containingType;
    private final Visibility visibility;
    private final boolean isStatic;
    private final String name;
    private final TypeName type;

    public Field(Type containingType, Visibility visibility, boolean isStatic, String name, TypeName type) {
        super(requireNonNull(containingType, "Containing type is <null>.").config);
        this.containingType = containingType;
        this.visibility = requireNonNull(visibility, "Field visibility is <null>.");
        this.isStatic = isStatic;
        this.name = requireNonNull(name, "Field name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Field name is empty.");
        this.type = requireNonNull(type, "Field type is <null>.");
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (isStatic) output.append("{static}").whitespace();
        visibility.writeTo(output).append(name);
        type.writeTo(output.append(":").whitespace()).newline();
        return output;
    }

    @Override
    public int compareTo(Field other) {
        requireNonNull(other, "Cannot compare with field <null>.");
        return comparing((Field field) -> field.containingType)
                .thenComparing(field -> name.toLowerCase())
                .thenComparing(field -> name)
                .compare(this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containingType, name);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Field && this.compareTo((Field) other) == 0);
    }

}
