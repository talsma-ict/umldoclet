package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.uml.*;
import nl.talsmasoftware.umldoclet.uml.util.UmlPostProcessors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ClassDiagramVisitor implements IClassDiagramVisitor{
    private static final UmlPostProcessors POST_PROCESSORS = new UmlPostProcessors();

    private static <T> Predicate<T> not(Predicate<T> predicate) {
        return t -> !predicate.test(t);
    }

    private static final Predicate<UMLNode> IS_ABSTRACT_METHOD = node ->
            node instanceof Method && ((Method) node).isAbstract;

    @Override
    public Diagram visit(TypeElement classElement) {
        Type type = UMLFactoryUtil.createAndPopulateType(null, classElement);
        ClassDiagram classDiagram = new ClassDiagram(UMLFactoryUtil.config, type);

        List<TypeName> foundTypeVariables = new ArrayList<>();
        List<Reference> references = new ArrayList<>();
        UmlCharacters sep = UmlCharacters.NEWLINE;

        // Add superclass
        TypeMirror superclassType = classElement.getSuperclass();
        Element superclassElement = UMLFactoryUtil.env.getTypeUtils().asElement(superclassType);
        while (superclassElement instanceof TypeElement && !UMLFactoryUtil.includeSuperclass((TypeElement) superclassElement)) {
            superclassType = ((TypeElement) superclassElement).getSuperclass();
            superclassElement = UMLFactoryUtil.env.getTypeUtils().asElement(superclassType);
        }
        if (superclassElement instanceof TypeElement) {
            final TypeName superclassName = TypeNameVisitor.INSTANCE.visit(superclassType);
            if (superclassName.getGenerics().length > 0) foundTypeVariables.add(superclassName);
            if (!UMLFactoryUtil.config.excludedTypeReferences().contains(superclassName.qualified)) {
                classDiagram.addChild(sep);
                Type superType = UMLFactoryUtil.createAndPopulateType(null, (TypeElement) superclassElement);
                // Only keep abstract methods of supertype.
                superType.removeChildren(not(IS_ABSTRACT_METHOD));
                classDiagram.addChild(superType);
                sep = UmlCharacters.EMPTY;
                references.add(new Reference(
                        Reference.from(type.getName().qualified, null),
                        "--|>",
                        Reference.to(superclassName.qualified, null))
                        .canonical());
            }
        }

        // Add interfaces
        for (TypeMirror interfaceType : classElement.getInterfaces()) {
            TypeName ifName = TypeNameVisitor.INSTANCE.visit(interfaceType);
            if (ifName.getGenerics().length > 0) foundTypeVariables.add(ifName);
            if (!UMLFactoryUtil.config.excludedTypeReferences().contains(ifName.qualified)) {
                Element implementedInterface = UMLFactoryUtil.env.getTypeUtils().asElement(interfaceType);
                if (implementedInterface instanceof TypeElement) {
                    classDiagram.addChild(sep);
                    Type implementedType = UMLFactoryUtil.createAndPopulateType(null, (TypeElement) implementedInterface);
                    implementedType.removeChildren(not(IS_ABSTRACT_METHOD));
                    classDiagram.addChild(implementedType);
                    sep = UmlCharacters.EMPTY;
                }
                references.add(new Reference(
                        Reference.from(type.getName().qualified, null),
                        UMLFactoryUtil.interfaceRefTypeFrom(type),
                        Reference.to(ifName.qualified, null))
                        .canonical());
            }
        }

        // Add containing class reference
        ElementKind enclosingKind = classElement.getEnclosingElement().getKind();
        if (enclosingKind.isClass() || enclosingKind.isInterface()) {
            TypeName enclosingTypeName = TypeNameVisitor.INSTANCE.visit(classElement.getEnclosingElement().asType());
            if (enclosingTypeName.getGenerics().length > 0) foundTypeVariables.add(enclosingTypeName);
            if (!UMLFactoryUtil.config.excludedTypeReferences().contains(enclosingTypeName.qualified)) {
                Element enclosingElement = classElement.getEnclosingElement();
                if (enclosingElement instanceof TypeElement) {
                    classDiagram.addChild(sep);
                    Type enclosingType = UMLFactoryUtil.createAndPopulateType(null, (TypeElement) enclosingElement);
                    enclosingType.removeChildren(not(IS_ABSTRACT_METHOD));
                    classDiagram.addChild(enclosingType);
                    sep = UmlCharacters.EMPTY;
                }
                references.add(new Reference(
                        Reference.from(type.getName().qualified, null),
                        "--+",
                        Reference.to(enclosingTypeName.qualified, null))
                        .canonical());
            }
        }

        // Add inner classes
        classElement.getEnclosedElements().stream()
                .filter(child -> child.getKind().isInterface() || child.getKind().isClass())
                .filter(TypeElement.class::isInstance).map(TypeElement.class::cast)
                .filter(UMLFactoryUtil.env::isIncluded)
                .forEach(innerclassElem -> {
                    Type innerType = UMLFactoryUtil.createType(null, innerclassElem);
                    classDiagram.addChild(innerType);
                    references.add(new Reference(
                            Reference.from(type.getName().qualified, null),
                            "+--",
                            Reference.to(innerType.getName().qualified, null))
                            .canonical());
                });

        if (!references.isEmpty()) {
            classDiagram.addChild(UmlCharacters.NEWLINE);
            references.forEach(classDiagram::addChild);
        }

        foundTypeVariables.forEach(foundTypeVariable -> classDiagram.getChildren().stream()
                .filter(Type.class::isInstance).map(Type.class::cast)
                .filter(tp -> foundTypeVariable.equals(tp.getName()))
                .forEach(tp -> tp.updateGenericTypeVariables(foundTypeVariable)));

        if (UMLFactoryUtil.config.methods().javaBeanPropertiesAsFields()) {
            POST_PROCESSORS.javaBeanPropertiesAsFieldsPostProcessor().accept(type);
        }

        return classDiagram;
    }
}
