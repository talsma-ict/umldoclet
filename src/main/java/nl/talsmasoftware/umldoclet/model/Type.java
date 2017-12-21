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
import javax.lang.model.type.TypeKind;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.lang.Integer.signum;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.ElementKind.ENUM;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.from;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.to;

public class Type extends UMLRenderer implements Comparable<Type> {

    protected final TypeElement tp;
    protected final Set<Modifier> modifiers;
    private final TypeName name;
    protected final Set<Reference> references = new LinkedHashSet<>();

    protected Type(UMLDiagram diagram, TypeElement typeElement) {
        super(diagram);
        this.tp = requireNonNull(typeElement, "Type element is <null>.");
        this.modifiers = typeElement.getModifiers();
        this.name = TypeNameVisitor.INSTANCE.visit(typeElement.asType());

        // Add the various parts of the class UML, order matters here, obviously!

        if (ENUM.equals(tp.getKind())) tp.getEnclosedElements().stream() // Add enum constants
                .filter(elem -> ElementKind.ENUM_CONSTANT.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .forEach(elem -> children.add(new Field(this, elem)));

        tp.getEnclosedElements().stream() // Add fields
                .filter(elem -> ElementKind.FIELD.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .forEach(elem -> children.add(new Field(this, elem)));

        tp.getEnclosedElements().stream() // Add constructors
                .filter(elem -> ElementKind.CONSTRUCTOR.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .forEach(elem -> children.add(new Method(this, elem)));

        tp.getEnclosedElements().stream() // Add methods
                .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .forEach(elem -> children.add(new Method(this, elem)));

        if (!TypeKind.NONE.equals(tp.getSuperclass().getKind())) {
            references.add(new Reference(
                    from(name.qualified), "--|>", to(TypeNameVisitor.INSTANCE.visit(tp.getSuperclass()).qualified)));
        }
        tp.getInterfaces().stream().map(TypeNameVisitor.INSTANCE::visit)
                .forEach(ifName -> references.add(new Reference(from(name.qualified), "..|>", to(ifName.qualified))));
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

    protected String getQualifiedName() {
        return tp.getQualifiedName().toString();
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        output.append(umlClassificationOf(tp)).whitespace();
        name.writeTo(output).whitespace();
        if (!children.isEmpty()) writeChildrenTo(output.append('{').newline()).append('}');
        return output.newline().newline();
    }

    /**
     * The 'UML' classification of the type.
     * <p>
     * Currently, this can return one of the following:
     * {@code "enum"},
     * {@code "interface"},
     * {@code "annotation"},
     * {@code "abstract class"}
     * or otherwise {@code "class"}.
     *
     * @param typeElement The type element to return the uml type for.
     * @return The UML type for the class to be rendered.
     */
    protected static String umlClassificationOf(TypeElement typeElement) {
        ElementKind kind = requireNonNull(typeElement, "Type element is <null>.").getKind();
        return ENUM.equals(kind) ? "enum"
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
