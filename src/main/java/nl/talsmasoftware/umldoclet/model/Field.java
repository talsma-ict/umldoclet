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
import javax.lang.model.type.TypeVisitor;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.Modifier.*;

/**
 * @author Sjoerd Talsma
 */
public class Field extends AbstractRenderer {

    protected final VariableElement fld;
    protected final Set<Modifier> modifiers;
    private final TypeVisitor<String, ?> typeName;

    protected Field(Type type, VariableElement variableElement) {
        super(type.diagram);
        this.fld = requireNonNull(variableElement, "Variable element is <null>.");
        this.modifiers = fld.getModifiers();
        this.typeName = new TypeName(true, true);
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        if (modifiers.contains(STATIC)) output.append("{static}").whitespace();
        output.append(umlAccessibility(modifiers)).append(fld.getSimpleName());
        output.append(":").whitespace().append(typeName.visit(fld.asType()));
        return output.newline();
    }

    protected static char umlAccessibility(Set<Modifier> modifiers) {
        return modifiers.contains(PRIVATE) ? '-'
                : modifiers.contains(PROTECTED) ? '#'
                : modifiers.contains(PUBLIC) ? '+'
                : '~';
    }

    @Override
    public int hashCode() {
        return Objects.hash(fld.getEnclosingElement(), fld.getSimpleName());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Field
                && Objects.equals(fld.getSimpleName(), ((Field) other).fld.getSimpleName())
                && Objects.equals(fld.getEnclosingElement(), ((Field) other).fld.getEnclosingElement())
        );
    }

}
