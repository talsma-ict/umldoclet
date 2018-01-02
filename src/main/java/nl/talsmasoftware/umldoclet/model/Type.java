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

import nl.talsmasoftware.umldoclet.rendering.Renderer;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.ElementKind.ENUM;
import static nl.talsmasoftware.umldoclet.model.Field.createField;
import static nl.talsmasoftware.umldoclet.model.Method.createMethod;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.from;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.to;

public class Type extends UMLRenderer implements Comparable<Type> {

    public final Package containingPackage;
    public final TypeClassification classfication;
    public final TypeName name;
    protected final Set<Renderer> children = new LinkedHashSet<>();
    protected final Set<Reference> references = new LinkedHashSet<>();

    static Type createType(Package containingPackage, TypeElement typeElement) {
        ElementKind kind = requireNonNull(typeElement, "Type element is <null>.").getKind();
        Set<Modifier> modifiers = typeElement.getModifiers();
        TypeClassification classification = ENUM.equals(kind) ? TypeClassification.ENUM
                : ElementKind.INTERFACE.equals(kind) ? TypeClassification.INTERFACE
                : ElementKind.ANNOTATION_TYPE.equals(kind) ? TypeClassification.ANNOTATION
                : modifiers.contains(Modifier.ABSTRACT) ? TypeClassification.ABSTRACT_CLASS
                : TypeClassification.CLASS;

        Type type = new Type(containingPackage,
                classification,
                TypeNameVisitor.INSTANCE.visit(typeElement.asType())
        );

        // Add the various parts of the class UML, order matters here, obviously!
        if (TypeClassification.ENUM.equals(classification)) typeElement.getEnclosedElements().stream() // Enum const
                .filter(elem -> ElementKind.ENUM_CONSTANT.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .forEach(elem -> type.children.add(createField(type, elem)));
        typeElement.getEnclosedElements().stream() // Add fields
                .filter(elem -> ElementKind.FIELD.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .forEach(elem -> type.children.add(createField(type, elem)));
        typeElement.getEnclosedElements().stream() // Add constructors
                .filter(elem -> ElementKind.CONSTRUCTOR.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .forEach(elem -> type.children.add(createMethod(type, elem)));
        typeElement.getEnclosedElements().stream() // Add methods
                .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .forEach(elem -> type.children.add(createMethod(type, elem)));
        if (!TypeKind.NONE.equals(typeElement.getSuperclass().getKind())) {
            type.references.add(new Reference(
                    from(type.name.qualified), "--|>", to(TypeNameVisitor.INSTANCE.visit(typeElement.getSuperclass()).qualified)));
        }
        typeElement.getInterfaces().stream()
                .map(TypeNameVisitor.INSTANCE::visit)
                .forEach(ifName -> type.references.add(new Reference(from(type.name.qualified), "..|>", to(ifName.qualified))));

        return type;
    }

    protected Type(Package containingPackage, TypeClassification classification, TypeName name) {
        super(requireNonNull(containingPackage, "Containing package is <null>.").diagram);
        this.containingPackage = containingPackage;
        this.classfication = requireNonNull(classification, "Type classification is <null>.");
        this.name = requireNonNull(name, "Type name is <null>.");
    }

//    protected PackageElement containingPackage() {
//        return diagram.env.getElementUtils().getPackageOf(tp);
//    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        classfication.writeTo(output).whitespace();
        name.writeTo(output).whitespace();
        if (!children.isEmpty()) {
            IndentingPrintWriter indented = output.append('{').newline().indent();
            children.forEach(child -> child.writeTo(indented));
            indented.unindent().append('}');
        }
        return output.newline();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int compareTo(Type other) {
        return name.compareTo(requireNonNull(other, "Cannot compare Type to <null>.").name);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Type && this.compareTo((Type) other) == 0);
    }

}
