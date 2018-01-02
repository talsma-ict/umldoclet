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
package nl.talsmasoftware.umldoclet.javadoc;

import jdk.javadoc.doclet.DocletEnvironment;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.model.*;
import nl.talsmasoftware.umldoclet.rendering.Renderer;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingRenderer;

import javax.lang.model.element.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.ElementKind.ENUM;

/**
 * @author Sjoerd Talsma
 */
public class UMLFactory {

    final Configuration config;
    private final DocletEnvironment env;

    public UMLFactory(Configuration config, DocletEnvironment env) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.env = requireNonNull(env, "Doclet environment is <null>.");
    }

    Namespace packageOf(TypeElement typeElement) {
        return new Namespace(config, env.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString());
    }

    Field createField(Type containingType, VariableElement variable) {
        Set<Modifier> modifiers = requireNonNull(variable, "Variable element is <null>.").getModifiers();
        return new Field(containingType,
                visibilityOf(modifiers),
                modifiers.contains(Modifier.STATIC),
                variable.getSimpleName().toString(),
                TypeNameVisitor.INSTANCE.visit(variable.asType())
        );
    }

    private Parameters toParameters(List<? extends VariableElement> params) {
        Parameters result = new Parameters(config);
        params.forEach(param -> result.add(param.getSimpleName().toString(), TypeNameVisitor.INSTANCE.visit(param.asType())));
        return result;
    }

    Method createConstructor(Type containingType, ExecutableElement executableElement) {
        Set<Modifier> modifiers = requireNonNull(executableElement, "Executable element is <null>.").getModifiers();
        return new Method(
                containingType,
                visibilityOf(modifiers),
                modifiers.contains(Modifier.ABSTRACT),
                modifiers.contains(Modifier.STATIC),
                containingType.name.simple,
                toParameters(executableElement.getParameters()),
                null
        );
    }

    Method createMethod(Type containingType, ExecutableElement executableElement) {
        Set<Modifier> modifiers = requireNonNull(executableElement, "Executable element is <null>.").getModifiers();
        return new Method(
                containingType,
                visibilityOf(modifiers),
                modifiers.contains(Modifier.ABSTRACT),
                modifiers.contains(Modifier.STATIC),
                executableElement.getSimpleName().toString(),
                toParameters(executableElement.getParameters()),
                TypeNameVisitor.INSTANCE.visit(executableElement.getReturnType())
        );
    }

    static Visibility visibilityOf(Set<Modifier> modifiers) {
        return modifiers.contains(Modifier.PRIVATE) ? Visibility.PRIVATE
                : modifiers.contains(Modifier.PROTECTED) ? Visibility.PROTECTED
                : modifiers.contains(Modifier.PUBLIC) ? Visibility.PUBLIC
                : Visibility.PACKAGE_PRIVATE;
    }

    Type createType(TypeElement typeElement) {
        return createType(packageOf(typeElement), typeElement);
    }

    private static boolean addChild(IndentingRenderer.WithChildren parent, Renderer child) {
        Collection<Renderer> children = (Collection<Renderer>) parent.getChildren();
        return children.add(child);
    }

    Type createType(Namespace containingPackage, TypeElement typeElement) {
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
                .forEach(elem -> addChild(type, createField(type, elem)));
        typeElement.getEnclosedElements().stream() // Add fields
                .filter(elem -> ElementKind.FIELD.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .forEach(elem -> addChild(type, createField(type, elem)));
        typeElement.getEnclosedElements().stream() // Add constructors
                .filter(elem -> ElementKind.CONSTRUCTOR.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .forEach(elem -> addChild(type, createConstructor(type, elem)));
        typeElement.getEnclosedElements().stream() // Add methods
                .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .forEach(elem -> addChild(type, createMethod(type, elem)));

//        if (!TypeKind.NONE.equals(typeElement.getSuperclass().getKind())) {
//            type.references.add(new Reference(
//                    from(type.name.qualified), "--|>", to(TypeNameVisitor.INSTANCE.visit(typeElement.getSuperclass()).qualified)));
//        }
//        typeElement.getInterfaces().stream()
//                .map(TypeNameVisitor.INSTANCE::visit)
//                .forEach(ifName -> type.references.add(new Reference(from(type.name.qualified), "..|>", to(ifName.qualified))));

        return type;
    }

    Namespace createPackage(PackageElement packageElement) {
        Namespace pkg = new Namespace(config, packageElement.getQualifiedName().toString());

        // Add all types contained in this package.
        packageElement.getEnclosedElements().stream()
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .map(type -> createType(pkg, type))
                .forEach(type -> addChild(pkg, type));

        // Or find references within the factory.
        pkg.getChildren().stream()
                .filter(Type.class::isInstance).map(Type.class::cast)
                .flatMap(type -> type.getChildren().stream()
                        .filter(Reference.class::isInstance).map(Reference.class::cast)
                        .map(Reference::canonical))
                .forEach(ref -> addChild(pkg, ref));

        return pkg;
    }

}
