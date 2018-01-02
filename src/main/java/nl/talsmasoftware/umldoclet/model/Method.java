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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.model.Field.visibilityOf;

/**
 * @author Sjoerd Talsma
 */
public class Method extends UMLRenderer {

    private final Type containingType;
    private final Visibility visibility;
    private final boolean isAbstract, isStatic;
    private final String name;
    private final TypeName returnType;

    static Method createMethod(Type containingType, ExecutableElement executableElement) {
        Set<Modifier> modifiers = requireNonNull(executableElement, "Executable element is <null>.").getModifiers();
        return new Method(
                containingType,
                visibilityOf(modifiers),
                modifiers.contains(Modifier.ABSTRACT),
                modifiers.contains(Modifier.STATIC),
                executableElement.getSimpleName().toString(),
                TypeNameVisitor.INSTANCE.visit(executableElement.asType())
        );
    }

    protected Method(Type containingType, Visibility visibility, boolean isAbstract, boolean isStatic, String name, TypeName returnType) {
        super(requireNonNull(containingType, "Containing type is <null>.").diagram);
        this.containingType = containingType;
        this.visibility = requireNonNull(visibility, "Method visibility is <null>.");
        this.isAbstract = isAbstract;
        this.isStatic = isStatic;
        this.name = requireNonNull(name, "Method name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Method name is empty.");
//        this.returnType = requireNonNull(returnType, "Method return type is <null>.");
        this.returnType = returnType;

//
//        // TODO javadoc aware code.
//        this.name = requireNonNull(executableElement.getSimpleName().toString(), "Method name is <null>.");
//        this.containingType = requireNonNull(TypeNameVisitor.INSTANCE.visit(executableElement.getEnclosingElement().asType()),
//                () -> "Enclosing type of method " + name + " is <null>.");
//        Set<Modifier> modifiers = executableElement.getModifiers();
//        this.visibility = visibilityOf(modifiers);
//        this.isAbstract = modifiers.contains(Modifier.ABSTRACT);
//        this.isStatic = modifiers.contains(Modifier.STATIC);
////        this.type = requireNonNull(TypeNameVisitor.INSTANCE.visit(executableElement.getReturnType()),
////                () -> "Return type is <null> for " + containingType.qualified + "." + name + ".");
//        this.type = TypeNameVisitor.INSTANCE.visit(executableElement.getReturnType());
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (isAbstract) output.append("{abstract}").whitespace();
        if (isStatic) output.append("{static}").whitespace();
        visibility.writeTo(output).append(name).append("()"); // TODO render parameters.
        if (returnType != null) returnType.writeTo(output.append(":").whitespace());
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
