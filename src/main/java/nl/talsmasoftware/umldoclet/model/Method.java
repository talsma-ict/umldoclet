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
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.model.Field.visibilityOf;

/**
 * @author Sjoerd Talsma
 */
public class Method extends UMLRenderer {

//    protected final ExecutableElement method;
//    protected final Set<Modifier> modifiers;
//    private final TypeVisitor<String, ?> parameterType, returnType;
//    private final boolean showParameterType, showParameterName, parameterTypeBeforeName;

    private final TypeName enclosingType;
    private final Visibility visibility;
    private final boolean isAbstract, isStatic;
    private final String name;
    private final TypeName type;

    protected Method(Type type, ExecutableElement executableElement) {
        super(type.diagram);
//        this.method = requireNonNull(executableElement, "Method executable element is <null>.");
//        this.modifiers = executableElement.getModifiers();
//        // The following booleans should be obtained from the configuration:
//        this.parameterType = new TypeNameVisitor(true, true);
//        this.returnType = new TypeNameVisitor(true, true);
//        this.showParameterType = true;
//        this.showParameterName = true;
//        this.parameterTypeBeforeName = true;

        // TODO javadoc aware code.
        requireNonNull(executableElement, "Executable element is <null>.");
        this.name = requireNonNull(executableElement.getSimpleName().toString(), "Method name is <null>.");
        this.enclosingType = requireNonNull(TypeNameVisitor.INSTANCE.visit(executableElement.getEnclosingElement().asType()),
                () -> "Enclosing type of method " + name + " is <null>.");
        Set<Modifier> modifiers = executableElement.getModifiers();
        this.visibility = visibilityOf(modifiers);
        this.isAbstract = modifiers.contains(Modifier.ABSTRACT);
        this.isStatic = modifiers.contains(Modifier.STATIC);
//        this.type = requireNonNull(TypeNameVisitor.INSTANCE.visit(executableElement.getReturnType()),
//                () -> "Return type is <null> for " + enclosingType.qualified + "." + name + ".");
        this.type = TypeNameVisitor.INSTANCE.visit(executableElement.getReturnType());
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (isAbstract) output.append("{abstract}").whitespace();
        if (isStatic) output.append("{static}").whitespace();
        visibility.writeTo(output).append(name).append("()"); // TODO render parameters.
        if (type != null) type.writeTo(output.append(":").whitespace());
//        type.writeTo(output.append(":").whitespace()).newline();
        output.newline();
        return output;
    }

//    private String parameter(VariableElement parameter) {
//        if (showParameterType && showParameterName) return parameterTypeBeforeName
//                ? parameterType.visit(parameter.asType()) + " " + parameter.getSimpleName()
//                : parameter.getSimpleName() + " " + parameterType.visit(parameter.asType());
//        else if (showParameterType) return parameterType.visit(parameter.asType());
//        else if (showParameterName) return parameter.getSimpleName().toString();
//        return null;
//    }

//    protected IndentingPrintWriter appendParametersTo(IndentingPrintWriter output) {
//        return output.append(method.getParameters().stream()
//                .map(this::parameter).filter(Objects::nonNull)
//                .collect(joining(", ", "(", ")")));
//    }

//    protected IndentingPrintWriter appendReturnTypeTo(IndentingPrintWriter output) {
//        return output.append(": ").append(returnType.visit(method.getReturnType()));
//    }


}
