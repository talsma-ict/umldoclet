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

import javax.lang.model.element.*;
import java.util.List;

import static java.lang.Integer.signum;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class Type extends Renderer implements Comparable<Type> {

    protected final TypeElement tp;

    protected Type(UMLDiagram diagram, TypeElement typeElement) {
        super(diagram);
        this.tp = requireNonNull(typeElement, "Type element is <null>.");

        // Add the various parts of the class UML, order matters here, obviously!

        // addEnumConstants();

        tp.getEnclosedElements().stream() // Add fields
                .filter(elem -> ElementKind.FIELD.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .forEach(elem -> children.add(new Field(this, elem)));

        tp.getEnclosedElements().stream() // Add constructors
                .filter(elem -> ElementKind.CONSTRUCTOR.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .forEach(elem -> children.add(new Constructor(this, elem)));

        tp.getEnclosedElements().stream() // Add methods
                .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .forEach(elem -> children.add(new Method(this, elem)));

    }

    protected PackageElement containingPackage() {
        return diagram.env.getElementUtils().getPackageOf(tp);
    }

    protected String getSimpleName() {
        StringBuilder sb = new StringBuilder(tp.getSimpleName());
        for (Element enclosed = tp.getEnclosingElement();
             enclosed != null && (enclosed.getKind().isClass() || enclosed.getKind().isInterface());
             enclosed = enclosed.getEnclosingElement()) {
            sb.insert(0, enclosed.getSimpleName() + ".");
        }
        return sb.toString();
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        output.append(umlTypeOf(tp)).whitespace();
        writeNameTo(output).whitespace();
        writeTypeParametersTo(output).whitespace();
        if (!children.isEmpty()) writeChildrenTo(output.append('{').newline()).append('}');
        return output.newline().newline();
    }

    protected IndentingPrintWriter writeNameTo(IndentingPrintWriter output) {
        return output.append(tp.getQualifiedName());
    }

    protected IndentingPrintWriter writeTypeParametersTo(IndentingPrintWriter output) {
        List<? extends TypeParameterElement> typeParameters = tp.getTypeParameters();
        return typeParameters.isEmpty() ? output
                : output.append('<')
                .append(typeParameters.stream().map(Element::getSimpleName).collect(joining(", ")))
                .append('>');
    }

    /**
     * Determines the 'UML' type for the class to be rendered.
     * Currently, this can return one of the following: {@code "enum"}, {@code "interface"}, {@code "abstract class"}
     * or otherwise {@code "class"}.
     *
     * @param typeElement The type element to return the uml type for.
     * @return The UML type for the class to be rendered.
     */
    protected static String umlTypeOf(TypeElement typeElement) {
        ElementKind kind = requireNonNull(typeElement, "Type element is <null>.").getKind();
        return ElementKind.ENUM.equals(kind) ? "enum"
                : ElementKind.INTERFACE.equals(kind) ? "interface"
                : ElementKind.ANNOTATION_TYPE.equals(kind) ? "annotation"
                : typeElement.getModifiers().contains(Modifier.ABSTRACT) ? "abstract class"
                : "class";
    }

    @Override
    public int hashCode() {
        return tp.getQualifiedName().hashCode();
    }

    public int compareTo(Type other) {
        final String otherQName = requireNonNull(other, "Cannot compare Type to <null>.").tp.getQualifiedName().toString();
        final String myQName = tp.getQualifiedName().toString();
        final int diff = signum(myQName.compareToIgnoreCase(otherQName));
        return diff == 0 ? signum(myQName.compareTo(otherQName)) : diff;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Type && this.compareTo((Type) other) == 0);
    }
}
