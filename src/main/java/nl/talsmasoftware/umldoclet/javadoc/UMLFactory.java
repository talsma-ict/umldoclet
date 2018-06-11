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
import nl.talsmasoftware.umldoclet.uml.*;
import nl.talsmasoftware.umldoclet.uml.configuration.Configuration;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.ENUM;
import static nl.talsmasoftware.umldoclet.uml.Reference.Side.from;
import static nl.talsmasoftware.umldoclet.uml.Reference.Side.to;
import static nl.talsmasoftware.umldoclet.uml.UMLPart.NEWLINE;

/**
 * @author Sjoerd Talsma
 */
public class UMLFactory {

    final Configuration config;
    final ThreadLocal<UMLDiagram> diagram = new ThreadLocal<>();
    private final DocletEnvironment env;
    private final Function<TypeMirror, TypeNameWithCardinality> typeNameWithCardinality;

    public UMLFactory(Configuration config, DocletEnvironment env) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.env = requireNonNull(env, "Doclet environment is <null>.");
        this.typeNameWithCardinality = TypeNameWithCardinality.function(env.getTypeUtils());
    }

    public UMLDiagram createClassDiagram(TypeElement classElement) {
        ClassDiagram classDiagram = new ClassDiagram(this, classElement);
        this.diagram.remove();
        return classDiagram;
    }

    public UMLDiagram createPackageDiagram(PackageElement packageElement) {
        PackageDiagram packageDiagram = new PackageDiagram(this, packageElement);
        this.diagram.remove();
        return packageDiagram;
    }

    Namespace packageOf(TypeElement typeElement) {
        return new Namespace(diagram.get(), env.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString());
    }

    Field createField(Type containingType, VariableElement variable) {
        Set<Modifier> modifiers = requireNonNull(variable, "Variable element is <null>.").getModifiers();
        Field field = new Field(containingType,
                visibilityOf(modifiers),
                modifiers.contains(Modifier.STATIC),
                variable.getSimpleName().toString(),
                TypeNameVisitor.INSTANCE.visit(variable.asType())
        );
        if (env.getElementUtils().isDeprecated(variable)) field = field.deprecated();
        return field;
    }

    private Parameters createParameters(List<? extends VariableElement> params) {
        Parameters result = new Parameters();
        Boolean varargs = null;
        for (VariableElement param : params) {
            if (varargs == null) result = result.varargs(varargs = isVarArgsMethod(param.getEnclosingElement()));
            result = result.add(param.getSimpleName().toString(), TypeNameVisitor.INSTANCE.visit(param.asType()));
        }
        return result;
    }

    private boolean isOnlyDefaultConstructor(Collection<ExecutableElement> constructors) {
        return constructors.size() == 1 && constructors.iterator().next().getParameters().isEmpty();
    }

    Method createConstructor(Type containingType, ExecutableElement executableElement) {
        Set<Modifier> modifiers = requireNonNull(executableElement, "Executable element is <null>.").getModifiers();
        Method constructor = new Method(containingType,
                visibilityOf(modifiers),
                modifiers.contains(Modifier.ABSTRACT),
                modifiers.contains(Modifier.STATIC),
                containingType.name.simple,
                createParameters(executableElement.getParameters()),
                null
        );
        if (env.getElementUtils().isDeprecated(executableElement)) constructor = constructor.deprecated();
        return constructor;
    }

    Method createMethod(Type containingType, ExecutableElement executableElement) {
        Set<Modifier> modifiers = requireNonNull(executableElement, "Executable element is <null>.").getModifiers();
        Method method = new Method(containingType,
                visibilityOf(modifiers),
                modifiers.contains(Modifier.ABSTRACT),
                modifiers.contains(Modifier.STATIC),
                executableElement.getSimpleName().toString(),
                createParameters(executableElement.getParameters()),
                TypeNameVisitor.INSTANCE.visit(executableElement.getReturnType())
        );
        if (env.getElementUtils().isDeprecated(executableElement)) method = method.deprecated();
        return method;
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

    static boolean addChild(UMLPart parent, UMLPart child) {
        Collection<UMLPart> children = (Collection<UMLPart>) parent.getChildren();
        return children.add(child);
    }

    private Type createType(Namespace containingPackage, TypeElement typeElement) {
        ElementKind kind = requireNonNull(typeElement, "Type element is <null>.").getKind();
        Set<Modifier> modifiers = typeElement.getModifiers();
        Type.Classification classification = ENUM.equals(kind) ? Type.Classification.ENUM
                : ElementKind.INTERFACE.equals(kind) ? Type.Classification.INTERFACE
                : ElementKind.ANNOTATION_TYPE.equals(kind) ? Type.Classification.ANNOTATION
                : modifiers.contains(Modifier.ABSTRACT) ? Type.Classification.ABSTRACT_CLASS
                : Type.Classification.CLASS;

        Type type = new Type(containingPackage,
                classification,
                TypeNameVisitor.INSTANCE.visit(typeElement.asType())
        );

        // Add the various parts of the class UML, order matters here, obviously!
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        if (Type.Classification.ENUM.equals(classification)) enclosedElements.stream()
                .filter(elem -> ElementKind.ENUM_CONSTANT.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .forEach(enumConst -> addChild(type, createField(type, enumConst)));

        enclosedElements.stream()
                .filter(elem -> ElementKind.FIELD.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .forEach(field -> addChild(type, createField(type, field)));

        List<ExecutableElement> constructors = enclosedElements.stream()
                .filter(elem -> ElementKind.CONSTRUCTOR.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .collect(toList());
        if (!isOnlyDefaultConstructor(constructors)) {
            constructors.forEach(constructor -> addChild(type, createConstructor(type, constructor)));
        }

        enclosedElements.stream()
                .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .filter(method -> !isMethodFromExcludedSuperclass(method))
                .forEach(method -> addChild(type, createMethod(type, method)));

        return env.getElementUtils().isDeprecated(typeElement) ? type.deprecated() : type;
    }

    private boolean isMethodFromExcludedSuperclass(ExecutableElement method) {
        boolean result = false;
        Element containingClass = method.getEnclosingElement();
        if (containingClass.getKind().isClass() || containingClass.getKind().isInterface()) {
            result = methodsFromExcludedSuperclasses().stream().anyMatch(
                    m -> similarMethodSignatures(m, method)
                            && env.getTypeUtils().isAssignable(containingClass.asType(), m.getEnclosingElement().asType()));
        }
        result = result || isExcludedEnumMethod(method);
        return result;
    }

    private Collection<ExecutableElement> _methodsFromExcludedSuperclasses = null;

    private Collection<ExecutableElement> methodsFromExcludedSuperclasses() {
        if (_methodsFromExcludedSuperclasses == null) {
            _methodsFromExcludedSuperclasses = config.excludedTypeReferences().stream()
                    .map(env.getElementUtils()::getTypeElement).filter(Objects::nonNull)
                    .map(TypeElement::getEnclosedElements).flatMap(Collection::stream)
                    .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                    .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                    .filter(method -> !method.getModifiers().contains(Modifier.ABSTRACT))
                    .filter(method -> visibilityOf(method.getModifiers()).compareTo(Visibility.PRIVATE) > 0)
                    .collect(toCollection(LinkedHashSet::new));
        }
        return _methodsFromExcludedSuperclasses;
    }

    private boolean isExcludedEnumMethod(ExecutableElement method) {
        if (config.excludedTypeReferences().contains(Enum.class.getName())
                && ElementKind.ENUM.equals(method.getEnclosingElement().getKind())
                && method.getModifiers().contains(Modifier.STATIC)) {
            if ("values".equals(method.getSimpleName().toString()) && method.getParameters().isEmpty()) {
                return true;
            } else if ("valueOf".equals(method.getSimpleName().toString()) && method.getParameters().size() == 1) {
                String paramType = TypeNameVisitor.INSTANCE.visit(method.getParameters().get(0).asType()).qualified;
                return String.class.getName().equals(paramType);
            }
        }
        return false;
    }

    private boolean similarMethodSignatures(ExecutableElement method1, ExecutableElement method2) {
        if (!method1.getSimpleName().equals(method2.getSimpleName())) return false;
        int paramCount = method1.getParameters().size();
        if (paramCount != method2.getParameters().size()) return false;

        Types typeUtils = env.getTypeUtils();
        boolean assignable1 = true, assignable2 = true;
        for (int i = 0; i < paramCount && (assignable1 || assignable2); i++) {
            TypeMirror param1 = method1.getParameters().get(i).asType();
            TypeMirror param2 = method2.getParameters().get(i).asType();
            assignable1 = assignable1 && typeUtils.isAssignable(param1, param2);
            assignable2 = assignable2 && typeUtils.isAssignable(param2, param1);
        }
        return assignable1 || assignable2;
    }

    private void addForeignType(Map<Namespace, Collection<Type>> foreignTypes, Element typeElement) {
        if (foreignTypes != null && typeElement instanceof TypeElement) {
            Type type = createType((TypeElement) typeElement);
            foreignTypes.computeIfAbsent(type.getNamespace(), (namespace) -> new LinkedHashSet<>()).add(type);
        }
    }

    private Collection<Reference> findPackageReferences(
            Namespace namespace, Map<Namespace, Collection<Type>> foreignTypes, TypeElement typeElement, Type type) {
        Collection<Reference> references = new LinkedHashSet<>();

        // Superclass reference.
        if (!TypeKind.NONE.equals(typeElement.getSuperclass().getKind())) {
            String superclass = TypeNameVisitor.INSTANCE.visit(typeElement.getSuperclass()).qualified;
            if (!config.excludedTypeReferences().contains(superclass)) {
                references.add(new Reference(
                        from(type.name.qualified), "--|>",
                        to(superclass)
                ));
            }
        }

        // Implemented interfaces.
        typeElement.getInterfaces().forEach(interfaceType -> {
            TypeName ifName = TypeNameVisitor.INSTANCE.visit(interfaceType);
            if (!config.excludedTypeReferences().contains(ifName.qualified)) {
                references.add(new Reference(
                        from(type.name.qualified), "..|>",
                        to(ifName.qualified)));
                if (!namespace.contains(ifName)) {
                    addForeignType(foreignTypes, env.getTypeUtils().asElement(interfaceType));
                }
            }
        });

        // Add reference to containing class from innner classes.
        ElementKind enclosingKind = typeElement.getEnclosingElement().getKind();
        if (enclosingKind.isClass() || enclosingKind.isInterface()) {
            references.add(new Reference(
                    from(TypeNameVisitor.INSTANCE.visit(typeElement.getEnclosingElement().asType()).qualified),
                    "+--", to(type.name.qualified)));
        }

        // Add 'uses' references by replacing visible fields
        typeElement.getEnclosedElements().stream()
                .filter(member -> ElementKind.FIELD.equals(member.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .filter(field -> config.fields().include(visibilityOf(field.getModifiers())))
                .forEach(field -> {
                    String fieldName = field.getSimpleName().toString();
                    TypeNameWithCardinality fieldType = typeNameWithCardinality.apply(field.asType());
                    if (namespace.contains(fieldType.typeName)) {
                        addReference(references, new Reference(
                                from(type.name.qualified),
                                "-->",
                                to(fieldType.typeName.qualified, fieldType.cardinality),
                                fieldName));
                        type.getChildren().removeIf(child -> child instanceof Field
                                && ((Field) child).name.equals(fieldName));
                    }
                });

        // Add 'uses' reference by replacing visible getters/setters
        typeElement.getEnclosedElements().stream()
                .filter(member -> ElementKind.METHOD.equals(member.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .filter(method -> config.methods().include(visibilityOf(method.getModifiers())))
                .forEach(method -> {
                    String propertyName = propertyName(method);
                    if (propertyName != null) {
                        TypeNameWithCardinality returnType = typeNameWithCardinality.apply(propertyType(method));
                        if (namespace.contains(returnType.typeName)) {
                            addReference(references, new Reference(
                                    from(type.name.qualified),
                                    "-->",
                                    to(returnType.typeName.qualified, returnType.cardinality),
                                    propertyName));
                            type.getChildren().removeIf(child -> child instanceof Method
                                    && ((Method) child).name.equals(method.getSimpleName().toString()));
                        }
                    }
                });

        return references;
    }

    private static String propertyName(ExecutableElement method) {
        String name = method.getSimpleName().toString();
        int params = method.getParameters().size();
        if (params == 0 && name.length() > 3 && name.startsWith("get")) {
            char[] result = name.substring(3).toCharArray();
            result[0] = Character.toLowerCase(result[0]);
            return new String(result);
        } else if (params == 1 && name.length() > 3 && name.startsWith("set")) {
            char[] result = name.substring(3).toCharArray();
            result[0] = Character.toLowerCase(result[0]);
            return new String(result);
        } else if (params == 0 && name.length() > 2 && name.startsWith("is") && isBooleanPrimitive(method.getReturnType())) {
            char[] result = name.substring(2).toCharArray();
            result[0] = Character.toLowerCase(result[0]);
            return new String(result);
        }
        return null;
    }

    private static TypeMirror propertyType(ExecutableElement method) {
        if (method.getSimpleName().toString().startsWith("set") && !method.getParameters().isEmpty()) {
            return method.getParameters().get(0).asType();
        }
        return method.getReturnType();
    }

    private static boolean isVarArgsMethod(Element element) {
        return element instanceof ExecutableElement && ((ExecutableElement) element).isVarArgs();
    }

    private static boolean isBooleanPrimitive(TypeMirror type) {
        return "boolean".equals(TypeNameVisitor.INSTANCE.visit(type).qualified);
    }

    private static void addReference(Collection<Reference> collection, Reference reference) {
        Reference result = reference;
        Optional<Reference> found = collection.stream().filter(reference::equals).findFirst();
        if (found.isPresent()) {
            result = found.get();
            collection.remove(result);
            for (String note : reference.notes) result = result.addNote(note);
        }
        collection.add(result);
    }

    private static Stream<TypeElement> innerTypes(TypeElement type) {
        return Stream.concat(Stream.of(type), type.getEnclosedElements().stream()
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .flatMap(UMLFactory::innerTypes));
    }

    Namespace createPackage(UMLDiagram diagram,
                            PackageElement packageElement,
                            Map<Namespace, Collection<Type>> foreignTypes,
                            List<Reference> references) {
        Namespace pkg = new Namespace(diagram, packageElement.getQualifiedName().toString());

        // Add all types contained in this package.
        packageElement.getEnclosedElements().stream()
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .flatMap(UMLFactory::innerTypes)
                .map(typeElement -> {
                    Type type = createType(pkg, typeElement);
                    references.addAll(findPackageReferences(pkg, foreignTypes, typeElement, type));
                    return type;
                })
                .flatMap(type -> Stream.of(NEWLINE, type))
                .forEach(child -> addChild(pkg, child));

        return pkg;
    }

}
