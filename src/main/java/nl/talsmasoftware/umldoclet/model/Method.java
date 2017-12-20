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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeVisitor;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.model.Field.umlAccessibility;

/**
 * @author Sjoerd Talsma
 */
public class Method extends AbstractRenderer {

    protected final ExecutableElement method;
    protected final Set<Modifier> modifiers;
    private final TypeVisitor<String, ?> parameterType, returnType;
    private final boolean showParameterType, showParameterName, parameterTypeBeforeName;

    protected Method(Type type, ExecutableElement executableElement) {
        super(type.diagram);
        this.method = requireNonNull(executableElement, "Method executable element is <null>.");
        this.modifiers = executableElement.getModifiers();
        // The following booleans should be obtained from the configuration:
        this.parameterType = new TypeName(true, true);
        this.returnType = new TypeName(true, true);
        this.showParameterType = true;
        this.showParameterName = true;
        this.parameterTypeBeforeName = true;
    }

    protected IndentingPrintWriter appendAccessibilityTo(IndentingPrintWriter output) {
        return output.append(umlAccessibility(modifiers));
    }

    protected IndentingPrintWriter appendNameTo(IndentingPrintWriter output) {
        return output.append(method.getSimpleName());
    }

    private String parameter(VariableElement parameter) {
        if (showParameterType && showParameterName) return parameterTypeBeforeName
                ? parameterType.visit(parameter.asType()) + " " + parameter.getSimpleName()
                : parameter.getSimpleName() + " " + parameterType.visit(parameter.asType());
        else if (showParameterType) return parameterType.visit(parameter.asType());
        else if (showParameterName) return parameter.getSimpleName().toString();
        return null;
    }

    protected IndentingPrintWriter appendParametersTo(IndentingPrintWriter output) {
        return output.append(method.getParameters().stream()
                .map(this::parameter).filter(Objects::nonNull)
                .collect(joining(", ", "(", ")")));
    }

    protected IndentingPrintWriter appendReturnTypeTo(IndentingPrintWriter output) {
        return output.append(": ").append(returnType.visit(method.getReturnType()));
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        return appendReturnTypeTo(
                appendParametersTo(
                        appendNameTo(
                                appendAccessibilityTo(output))))
                .newline();
    }

}
