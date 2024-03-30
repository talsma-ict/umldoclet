package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.uml.*;
import nl.talsmasoftware.umldoclet.uml.util.UmlPostProcessors;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Stream;

public class PackageDiagramVisitor implements IPackageDiagramVisitor {
    private static final UmlPostProcessors POST_PROCESSORS = new UmlPostProcessors();

    @Override
    public Diagram visit(PackageElement packageElement) {
        final ModuleElement module = UMLFactoryUtil.env.getElementUtils().getModuleOf(packageElement);
        PackageDiagram packageDiagram = new PackageDiagram(UMLFactoryUtil.config, packageElement.getQualifiedName().toString(),
                module == null ? null : module.getQualifiedName().toString());
        Map<String, Collection<Type>> foreignTypes = new LinkedHashMap<>();
        List<Reference> references = new ArrayList<>();

        Namespace namespace = createPackage(packageDiagram, packageElement, foreignTypes, references, "::");
        packageDiagram.addChild(namespace);

        // Filter "java.lang" or "java.util" references that occur >= 3 times
        // Maybe somehow make this UMLFactoryUtil.configurable as well?
        foreignTypes.entrySet().stream()
                .filter(entry -> "java.lang".equals(entry.getKey()) || "java.util".equals(entry.getKey()))
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
                    String foreignPackage = entry.getKey();
                    Namespace foreignNamespace = new Namespace(packageDiagram, foreignPackage, null);
                    entry.getValue().forEach(foreignNamespace::addChild);
                    return foreignNamespace;
                })
                .flatMap(foreignPackage -> Stream.of(UmlCharacters.NEWLINE, foreignPackage))
                .forEach(packageDiagram::addChild);

        namespace.addChild(UmlCharacters.NEWLINE);

        if (UMLFactoryUtil.config.methods().javaBeanPropertiesAsFields()) {
            namespace.getChildren().stream()
                    .filter(Type.class::isInstance).map(Type.class::cast)
                    .forEach(POST_PROCESSORS.javaBeanPropertiesAsFieldsPostProcessor());
        }

        if (!references.isEmpty()) packageDiagram.addChild(UmlCharacters.NEWLINE);
        references.stream().map(Reference::canonical).forEach(packageDiagram::addChild);

        return packageDiagram;
    }

    private static boolean isBooleanPrimitive(TypeMirror type) {
        return "boolean".equals(TypeNameVisitor.INSTANCE.visit(type).qualified);
    }

    private static String propertyName(ExecutableElement method) {
        char[] result = null;
        final Set<Modifier> modifiers = method.getModifiers();
        if (modifiers.contains(Modifier.PUBLIC)
                && !modifiers.contains(Modifier.ABSTRACT)
                && !modifiers.contains(Modifier.STATIC)) {
            String name = method.getSimpleName().toString();
            int params = method.getParameters().size();
            if (params == 0 && name.length() > 3 && name.startsWith("get")) {
                // TODO: check non-void return type?
                result = name.substring(3).toCharArray();
            } else if (params == 1 && name.length() > 3 && name.startsWith("set")) {
                // TODO: check void return type?
                result = name.substring(3).toCharArray();
            } else if (params == 0 && name.length() > 2 && name.startsWith("is") && isBooleanPrimitive(method.getReturnType())) {
                result = name.substring(2).toCharArray();
            }
        }
        if (result != null) {
            result[0] = Character.toLowerCase(result[0]);
            return new String(result);
        }
        return null;
    }
    private Collection<Reference> findPackageReferences(
            Namespace namespace, Map<String, Collection<Type>> foreignTypes, TypeElement typeElement, Type type, String separator) {
        Collection<Reference> references = new LinkedHashSet<>();

        // Superclass reference.
        TypeMirror superclassType = typeElement.getSuperclass();
        Element superclassElement = UMLFactoryUtil.env.getTypeUtils().asElement(superclassType);
        while (superclassElement instanceof TypeElement && !UMLFactoryUtil.includeSuperclass((TypeElement) superclassElement)) {
            superclassType = ((TypeElement) superclassElement).getSuperclass();
            superclassElement = UMLFactoryUtil.env.getTypeUtils().asElement(superclassType);
        }
        if (superclassElement instanceof TypeElement) {
            TypeName superclassName = TypeNameVisitor.INSTANCE.visit(superclassType);
            if (!UMLFactoryUtil.config.excludedTypeReferences().contains(superclassName.qualified)) {
                references.add(new Reference(
                        Reference.from(type.getName().getQualified(separator), null),
                        "--|>",
                        Reference.to(superclassName.getQualified(separator), null)));
                if (!NamespaceService.contains(namespace, superclassName)) {
                    addForeignType(foreignTypes, superclassElement);
                }
            }
        }

        // Implemented interfaces.
        typeElement.getInterfaces().forEach(interfaceType -> {
            TypeName interfaceName = TypeNameVisitor.INSTANCE.visit(interfaceType);
            if (!UMLFactoryUtil.config.excludedTypeReferences().contains(interfaceName.qualified)) {
                references.add(new Reference(
                        Reference.from(type.getName().getQualified(separator), null),
                        UMLFactoryUtil.interfaceRefTypeFrom(type),
                        Reference.to(interfaceName.getQualified(separator), null)));
                // TODO Figure out what to do IF the interface is found BUT has a different typename
                if (!NamespaceService.contains(namespace, interfaceName)) {
                    addForeignType(foreignTypes, UMLFactoryUtil.env.getTypeUtils().asElement(interfaceType));
                }
            }
        });

        // Add reference to containing class from innner classes.
        ElementKind enclosingKind = typeElement.getEnclosingElement().getKind();
        if (enclosingKind.isClass() || enclosingKind.isInterface()) {
            TypeName parentType = TypeNameVisitor.INSTANCE.visit(typeElement.getEnclosingElement().asType());
            references.add(new Reference(
                    Reference.from(parentType.getQualified(separator), null),
                    "+--",
                    Reference.to(type.getName().getQualified(separator), null)));
            // No check needed whether parent type lives in our namespace.
        }

        // Add 'uses' references by replacing visible fields
        typeElement.getEnclosedElements().stream()
                .filter(member -> ElementKind.FIELD.equals(member.getKind()))
                .filter(VariableElement.class::isInstance).map(VariableElement.class::cast)
                .filter(field -> UMLFactoryUtil.config.fields().include(UMLFactoryUtil.visibilityOf(field.getModifiers())))
                .forEach(field -> {
                    String fieldName = field.getSimpleName().toString();
                    TypeNameWithCardinality fieldType =UMLFactoryUtil.typeNameWithCardinality.apply(field.asType());
                    if (NamespaceService.contains(namespace, fieldType.typeName)) {
                        addReference(references, new Reference(
                                Reference.from(type.getName().getQualified(separator), null),
                                "-->",
                                Reference.to(fieldType.typeName.getQualified(separator), fieldType.cardinality),
                                fieldName));
                        type.removeChildren(child -> child instanceof Field && ((Field) child).name.equals(fieldName));
                    }
                });

        // Add 'uses' reference by replacing visible getters/setters
        typeElement.getEnclosedElements().stream()
                .filter(member -> ElementKind.METHOD.equals(member.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .filter(method -> UMLFactoryUtil.config.methods().include(UMLFactoryUtil.visibilityOf(method.getModifiers())))
                .forEach(method -> {
                    String propertyName = propertyName(method);
                    if (propertyName != null) {
                        TypeNameWithCardinality returnType = UMLFactoryUtil.typeNameWithCardinality.apply(propertyType(method));
                        if (NamespaceService.contains(namespace, returnType.typeName)) {
                            addReference(references, new Reference(
                                    Reference.from(type.getName().getQualified(separator), null),
                                    "-->",
                                    Reference.to(returnType.typeName.getQualified(separator), returnType.cardinality),
                                    propertyName));
                            type.removeChildren(child -> child instanceof Method
                                    && ((Method) child).name.equals(method.getSimpleName().toString()));
                        }
                    }
                });

        return references;
    }

    private void addForeignType(Map<String, Collection<Type>> foreignTypes, Element typeElement) {
        if (foreignTypes != null && typeElement instanceof TypeElement) {
            Type type = UMLFactoryUtil.createAndPopulateType(null, (TypeElement) typeElement);
            if (typeElement.getKind().isClass()) {
                type.removeChildren(child -> child instanceof Method && !((Method) child).isAbstract);
            }
            foreignTypes.computeIfAbsent(type.getPackagename(), (namespace) -> new LinkedHashSet<>()).add(type);
        }
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

    private static TypeMirror propertyType(ExecutableElement method) {
        if (method.getSimpleName().toString().startsWith("set") && !method.getParameters().isEmpty()) {
            return method.getParameters().get(0).asType();
        }
        return method.getReturnType();
    }

    Namespace createPackage(Diagram diagram,
                            PackageElement packageElement,
                            Map<String, Collection<Type>> foreignTypes,
                            List<Reference> references,
                            String referenceSeparator) {
        final ModuleElement module = UMLFactoryUtil.env.getElementUtils().getModuleOf(packageElement);
        Namespace pkg = new Namespace(diagram, packageElement.getQualifiedName().toString(),
                module == null ? null : module.getQualifiedName().toString());

        // Add all types contained in this package.
        packageElement.getEnclosedElements().stream()
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .flatMap(PackageDiagramVisitor::innerTypes)
                .filter(UMLFactoryUtil.env::isIncluded)
                .map(typeElement -> {
                    Type type = UMLFactoryUtil.createAndPopulateType(pkg, typeElement);
                    references.addAll(findPackageReferences(pkg, foreignTypes, typeElement, type, referenceSeparator));
                    return type;
                })
                .flatMap(type -> Stream.of(UmlCharacters.NEWLINE, type))
                .forEach(pkg::addChild);

        return pkg;
    }

    private static Stream<TypeElement> innerTypes(TypeElement type) {
        return Stream.concat(Stream.of(type), type.getEnclosedElements().stream()
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .flatMap(PackageDiagramVisitor::innerTypes));
    }
}
