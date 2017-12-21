/*
 * Copyright 2016-2017 Talsma ICT
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

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Objects;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class Field extends UMLRenderer implements Comparable<Field> {

    private final TypeName enclosingType;
    private final Visibility visibility;
    private final boolean isStatic;
    private final String name;
    private final TypeName type;

    protected Field(Type type, VariableElement variableElement) {
        super(type.diagram);
        // TODO javadoc aware code.
        this.enclosingType = TypeNameVisitor.INSTANCE.visit(variableElement.getEnclosingElement().asType());
        Set<Modifier> modifiers = variableElement.getModifiers();
        this.visibility = visibilityOf(modifiers);
        this.isStatic = modifiers.contains(Modifier.STATIC);
        this.name = variableElement.getSimpleName().toString();
        this.type = TypeNameVisitor.INSTANCE.visit(variableElement.asType());
    }

    // TODO javadoc aware code.
    static Visibility visibilityOf(Set<Modifier> modifiers) {
        return modifiers.contains(Modifier.PRIVATE) ? Visibility.PRIVATE
                : modifiers.contains(Modifier.PROTECTED) ? Visibility.PROTECTED
                : modifiers.contains(Modifier.PUBLIC) ? Visibility.PUBLIC
                : Visibility.PACKAGE_PRIVATE;
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
        return comparing((Field field) -> field.enclosingType)
                .thenComparing(field -> name.toLowerCase())
                .thenComparing(field -> name)
                .compare(this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enclosingType, name);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Field && this.compareTo((Field) other) == 0);
    }

}
