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
import nl.talsmasoftware.umldoclet.uml.ClassUml;
import nl.talsmasoftware.umldoclet.uml.Field;
import nl.talsmasoftware.umldoclet.uml.Literal;
import nl.talsmasoftware.umldoclet.uml.Method;
import nl.talsmasoftware.umldoclet.uml.Namespace;
import nl.talsmasoftware.umldoclet.uml.PackageUml;
import nl.talsmasoftware.umldoclet.uml.Parameters;
import nl.talsmasoftware.umldoclet.uml.Reference;
import nl.talsmasoftware.umldoclet.uml.Type;
import nl.talsmasoftware.umldoclet.uml.TypeMember;
import nl.talsmasoftware.umldoclet.uml.TypeName;
import nl.talsmasoftware.umldoclet.uml.UMLRoot;
import nl.talsmasoftware.umldoclet.uml.Visibility;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

/**
 * One big factory to produce UML from analyzed Javadoc elements.
 * <p>
 * TODO: This should be refactored into ClassUml and PackageUml visitor implementations.
 * This increases flexibility in supporting future language features however may introduce additional risk
 * with regard to unbounded recursion (see <a href="https://github.com/talsma-ict/umldoclet/issues/75">Issue 75</a>
 * for example).
 *
 * @author Sjoerd Talsma
 */
public class UMLFactory {

    final Configuration config;
    final ThreadLocal<UMLRoot> diagram = new ThreadLocal<>();
    private final DocletEnvironment env;
    private final Function<TypeMirror, TypeNameWithCardinality> typeNameWithCardinality;

    public UMLFactory(Configuration config, DocletEnvironment env) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.env = requireNonNull(env, "Doclet environment is <null>.");
        this.typeNameWithCardinality = TypeNameWithCardinality.function(env.getTypeUtils());
    }

    public UMLRoot createClassDiagram(TypeElement classElement) {
        Type type = createAndPopulateType(null, classElement);
        ClassUml classUml = new ClassUml(config, type);

        List<TypeName> foundTypeVariables = new ArrayList<>();
        List<Reference> references = new ArrayList<>();
        Literal sep = Literal.NEWLINE;

        // Add superclass
        if (!TypeKind.NONE.equals(classElement.getSuperclass().getKind())) {
            TypeName superclassName = TypeNameVisitor.INSTANCE.visit(classElement.getSuperclass());
            if (superclassName.getGenerics().length > 0) foundTypeVariables.add(superclassName);
            if (!config.excludedTypeReferences().contains(superclassName.qualified)) {
                Element superclass = env.getTypeUtils().asElement(classElement.getSuperclass());
                if (superclass instanceof TypeElement) {
                    classUml.addChild(sep);
                    Type superType = createAndPopulateType(null, (TypeElement) superclass);
                    superType.removeChildren(child -> !(child instanceof TypeMember) || !((TypeMember) child).isAbstract);
                    classUml.addChild(superType);
                    sep = Literal.EMPTY;
                }
                references.add(new Reference(from(type.getName().qualified), "--|>", to(superclassName.qualified)).canonical());
            }
        }

        // Add interfaces
        for (TypeMirror interfaceType : classElement.getInterfaces()) {
            TypeName ifName = TypeNameVisitor.INSTANCE.visit(interfaceType);
            if (ifName.getGenerics().length > 0) foundTypeVariables.add(ifName);
            if (!config.excludedTypeReferences().contains(ifName.qualified)) {
                Element implementedInterface = env.getTypeUtils().asElement(interfaceType);
                if (implementedInterface instanceof TypeElement) {
                    classUml.addChild(sep);
                    Type implementedType = createAndPopulateType(null, (TypeElement) implementedInterface);
                    implementedType.removeChildren(child -> !(child instanceof TypeMember) || !((TypeMember) child).isAbstract);
                    classUml.addChild(implementedType);
                    sep = Literal.EMPTY;
                }
                references.add(new Reference(from(type.getName().qualified), "..|>", to(ifName.qualified)).canonical());
            }
        }

        // Add containing class reference
        ElementKind enclosingKind = classElement.getEnclosingElement().getKind();
        if (enclosingKind.isClass() || enclosingKind.isInterface()) {
            TypeName enclosingTypeName = TypeNameVisitor.INSTANCE.visit(classElement.getEnclosingElement().asType());
            if (enclosingTypeName.getGenerics().length > 0) foundTypeVariables.add(enclosingTypeName);
            if (!config.excludedTypeReferences().contains(enclosingTypeName.qualified)) {
                Element enclosingElement = classElement.getEnclosingElement();
                if (enclosingElement instanceof TypeElement) {
                    classUml.addChild(sep);
                    Type enclosingType = createAndPopulateType(null, (TypeElement) enclosingElement);
                    enclosingType.removeChildren(child -> !(child instanceof TypeMember) || !((TypeMember) child).isAbstract);
                    classUml.addChild(enclosingType);
                    sep = Literal.EMPTY;
                }
                references.add(new Reference(from(type.getName().qualified), "--+", to(enclosingTypeName.qualified)).canonical());
            }
        }

        // Add inner classes
        classElement.getEnclosedElements().stream()
                .filter(child -> child.getKind().isInterface() || child.getKind().isClass())
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .forEach(innerclassElem -> {
                    Type innerType = createType(null, innerclassElem);
                    classUml.addChild(innerType);
                    references.add(new Reference(from(type.getName().qualified), "+--", to(innerType.getName().qualified)).canonical());
                });

        if (!references.isEmpty()) {
            classUml.addChild(Literal.NEWLINE);
            references.forEach(classUml::addChild);
        }

        if (!foundTypeVariables.isEmpty()) {
            Collections.sort(foundTypeVariables);
            for (int i = foundTypeVariables.size() - 1; i >= 0; i--) {
                TypeName foundTypeVariable = foundTypeVariables.get(i);
                if (i > 0 && foundTypeVariable.equals(foundTypeVariables.get(i - 1))) {
                    i--; // duplicate, skip these.
                } else {
                    classUml.getChildren().stream()
                            .filter(Type.class::isInstance).map(Type.class::cast)
                            .filter(tp -> foundTypeVariable.equals(tp.getName()))
                            .forEach(tp -> tp.updateGenericTypeVariables(foundTypeVariable));
                }
            }
        }

        return classUml;
    }

    public UMLRoot createPackageDiagram(PackageElement packageElement) {
        PackageUml packageUml = new PackageUml(config, packageElement.getQualifiedName().toString());
        Map<Namespace, Collection<Type>> foreignTypes = new LinkedHashMap<>();
        List<Reference> references = new ArrayList<>();

        Namespace namespace = createPackage(packageUml, packageElement, foreignTypes, references);
        packageUml.addChild(namespace);

        // Filter "java.lang" or "java.util" references that occur >= 3 times
        // Maybe somehow make this configurable as well?
        foreignTypes.entrySet().stream()
                .filter(entry -> "java.lang".equals(entry.getKey().name) || "java.util".equals(entry.getKey().name))
                .map(Map.Entry::getValue)
                .forEach(types -> {
                    for (Iterator<Type> it = types.iterator(); it.hasNext(); ) {
                        Type type = it.next();
                        if (references.stream().filter(ref -> ref.contains(type.getName())).limit(3).count() > 2) {
                            references.removeIf(ref -> ref.contains(type.getName()));
                            it.remove();
                        }
                    }
                });

        // Add all remaining foreign types to the diagram.
        foreignTypes.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> {
                    Namespace foreignPackage = entry.getKey();
                    entry.getValue().forEach(foreignPackage::addChild);
                    return foreignPackage;
                })
                .flatMap(foreignPackage -> Stream.of(Literal.NEWLINE, foreignPackage))
                .forEach(packageUml::addChild);

        namespace.addChild(Literal.NEWLINE);
        references.stream().map(Reference::canonical).forEach(namespace::addChild);

        return packageUml;
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
                containingType.getName().simple,
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

    /**
     * Creates an 'empty' type (i.e. without any fields, constructors or methods)
     *
     * @param containingPackage The containing package of the type (optional, will be obtained from typeElement if null).
     * @param type              The type element to create a Type object for.
     * @return The empty Type object.
     */
    private Type createType(Namespace containingPackage, TypeElement type) {
        requireNonNull(type, "Type element is <null>.");
        if (containingPackage == null) containingPackage = packageOf(type);
        return new Type(containingPackage, typeClassificationOf(type), TypeNameVisitor.INSTANCE.visit(type.asType()));
    }

    private Type createAndPopulateType(Namespace containingPackage, TypeElement type) {
        return populateType(createType(containingPackage, type), type);
    }

    private static Type.Classification typeClassificationOf(TypeElement type) {
        ElementKind kind = type.getKind();
        Set<Modifier> modifiers = type.getModifiers();
        return ENUM.equals(kind) ? Type.Classification.ENUM
                : ElementKind.INTERFACE.equals(kind) ? Type.Classification.INTERFACE
                : ElementKind.ANNOTATION_TYPE.equals(kind) ? Type.Classification.ANNOTATION
                : modifiers.contains(Modifier.ABSTRACT) ? Type.Classification.ABSTRACT_CLASS
                : Type.Classification.CLASS;
    }

    private Type populateType(Type type, TypeElement typeElement) {
        // Add the various parts of the class UML, order matters here, obviously!
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        if (Type.Classification.ENUM.equals(type.getClassfication())) enclosedElements.stream()
                .filter(elem -> ElementKind.ENUM_CONSTANT.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .map(enumConst -> createField(type, enumConst))
                .forEach(type::addChild);

        enclosedElements.stream()
                .filter(elem -> ElementKind.FIELD.equals(elem.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .map(field -> createField(type, field))
                .forEach(type::addChild);

        List<ExecutableElement> constructors = enclosedElements.stream()
                .filter(elem -> ElementKind.CONSTRUCTOR.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .collect(toList());
        if (!isOnlyDefaultConstructor(constructors)) {
            constructors.stream()
                    .map(constructor -> createConstructor(type, constructor))
                    .forEach(type::addChild);
        }

        enclosedElements.stream()
                .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .filter(method -> !isMethodFromExcludedSuperclass(method))
                .map(method -> createMethod(type, method))
                .forEach(type::addChild);

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
            Type type = createAndPopulateType(null, (TypeElement) typeElement);
            if (typeElement.getKind().isClass()) {
                type.removeChildren(child -> child instanceof Method && !((Method) child).isAbstract);
            }
            foreignTypes.computeIfAbsent(type.getNamespace(), (namespace) -> new LinkedHashSet<>()).add(type);
        }
    }

    private Collection<Reference> findPackageReferences(
            Namespace namespace, Map<Namespace, Collection<Type>> foreignTypes, TypeElement typeElement, Type type) {
        Collection<Reference> references = new LinkedHashSet<>();

        // Superclass reference.
        if (!TypeKind.NONE.equals(typeElement.getSuperclass().getKind())) {
            TypeName superclass = TypeNameVisitor.INSTANCE.visit(typeElement.getSuperclass());
            if (!config.excludedTypeReferences().contains(superclass.qualified)) {
                references.add(new Reference(from(type.getName().qualified), "--|>", to(superclass.qualified)));
                if (!namespace.contains(superclass)) {
                    addForeignType(foreignTypes, env.getTypeUtils().asElement(typeElement.getSuperclass()));
                }
            }
        }

        // Implemented interfaces.
        typeElement.getInterfaces().forEach(interfaceType -> {
            TypeName interfaceName = TypeNameVisitor.INSTANCE.visit(interfaceType);
            if (!config.excludedTypeReferences().contains(interfaceName.qualified)) {
                references.add(new Reference(from(type.getName().qualified), "..|>", to(interfaceName.qualified)));
                // TODO Figure out what to do IF the interface is found BUT has a different typename
                if (!namespace.contains(interfaceName)) {
                    addForeignType(foreignTypes, env.getTypeUtils().asElement(interfaceType));
                }
            }
        });

        // Add reference to containing class from innner classes.
        ElementKind enclosingKind = typeElement.getEnclosingElement().getKind();
        if (enclosingKind.isClass() || enclosingKind.isInterface()) {
            TypeName parentType = TypeNameVisitor.INSTANCE.visit(typeElement.getEnclosingElement().asType());
            references.add(new Reference(from(parentType.qualified), "+--", to(type.getName().qualified)));
            // No check needed whether parent type lives in our namespace.
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
                                from(type.getName().qualified),
                                "-->",
                                to(fieldType.typeName.qualified, fieldType.cardinality),
                                fieldName));
                        type.removeChildren(child -> child instanceof Field && ((Field) child).name.equals(fieldName));
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
                                    from(type.getName().qualified),
                                    "-->",
                                    to(returnType.typeName.qualified, returnType.cardinality),
                                    propertyName));
                            type.removeChildren(child -> child instanceof Method
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

    Namespace createPackage(UMLRoot diagram,
                            PackageElement packageElement,
                            Map<Namespace, Collection<Type>> foreignTypes,
                            List<Reference> references) {
        Namespace pkg = new Namespace(diagram, packageElement.getQualifiedName().toString());

        // Add all types contained in this package.
        packageElement.getEnclosedElements().stream()
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .flatMap(UMLFactory::innerTypes)
                .map(typeElement -> {
                    Type type = createAndPopulateType(pkg, typeElement);
                    references.addAll(findPackageReferences(pkg, foreignTypes, typeElement, type));
                    return type;
                })
                .flatMap(type -> Stream.of(Literal.NEWLINE, type))
                .forEach(pkg::addChild);

        return pkg;
    }

}
