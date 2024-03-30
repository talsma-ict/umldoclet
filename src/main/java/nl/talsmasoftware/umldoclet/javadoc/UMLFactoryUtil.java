package nl.talsmasoftware.umldoclet.javadoc;

import jdk.javadoc.doclet.DocletEnvironment;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.Visibility;
import nl.talsmasoftware.umldoclet.uml.*;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.ENUM;

public class UMLFactoryUtil {

    public static Configuration config;
    public static DocletEnvironment env;
    public static Function<TypeMirror, TypeNameWithCardinality> typeNameWithCardinality;

    public static String interfaceRefTypeFrom(Type type) {
        final boolean isExtendedBySubInterface = Type.Classification.INTERFACE.equals(type.getClassfication());
        return isExtendedBySubInterface ? "--|>" : "..|>";
    }

    /**
     * Determine whether a superclass is included in the documentation.
     *
     * <p>
     * Introduced to fix <a href="https://github.com/talsma-ict/umldoclet/issues/146">issue 146</a>:
     * skip superclass if not included in the documentation.
     *
     * @param superclass The superclass to test.
     * @return {@code true} if the superclass is within the documented javadoc part,
     * or if its modifiers have the 'right' accesibility. See {#148} for accessibility details.
     */
    public static boolean includeSuperclass(TypeElement superclass) {
        if (env.isIncluded(superclass)) return true;
        // TODO Make configurable:
        // See https://github.com/talsma-ict/umldoclet/issues/148
        return superclass.getModifiers().contains(Modifier.PUBLIC)
                || superclass.getModifiers().contains(Modifier.PROTECTED);
    }

    static Method createMethod(Type containingType, ExecutableElement executableElement) {
        Set<Modifier> modifiers = requireNonNull(executableElement, "Executable element is <null>.").getModifiers();
        Method method = new Method(containingType,
                executableElement.getSimpleName().toString(),
                TypeNameVisitor.INSTANCE.visit(executableElement.getReturnType())
        );
        method.setVisibility(UMLFactoryUtil.visibilityOf(modifiers));
        method.isAbstract = modifiers.contains(Modifier.ABSTRACT);
        method.isStatic = modifiers.contains(Modifier.STATIC);
        method.isDeprecated = env.getElementUtils().isDeprecated(executableElement);
        method.addChild(createParameters(executableElement.getParameters()));
        return method;
    }

    public static boolean isExcludedEnumMethod(ExecutableElement method) {
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

    public static boolean similarMethodSignatures(ExecutableElement method1, ExecutableElement method2) {
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

    private static Collection<ExecutableElement> _methodsFromExcludedSuperclasses = null;

    public static Collection<ExecutableElement> methodsFromExcludedSuperclasses() {
        if (_methodsFromExcludedSuperclasses == null) {
            _methodsFromExcludedSuperclasses = config.excludedTypeReferences().stream()
                    .map(env.getElementUtils()::getTypeElement).filter(Objects::nonNull)
                    .map(TypeElement::getEnclosedElements).flatMap(Collection::stream)
                    .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                    .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                    .filter(method -> !method.getModifiers().contains(Modifier.ABSTRACT))
                    .filter(method -> UMLFactoryUtil.visibilityOf(method.getModifiers()).compareTo(Visibility.PRIVATE) > 0)
                    .collect(toCollection(LinkedHashSet::new));
        }
        return _methodsFromExcludedSuperclasses;
    }

    public static Type.Classification typeClassificationOf(TypeElement type) {
        ElementKind kind = type.getKind();
        Set<Modifier> modifiers = type.getModifiers();
        return ENUM.equals(kind) ? Type.Classification.ENUM
                : ElementKind.INTERFACE.equals(kind) ? Type.Classification.INTERFACE
                : ElementKind.ANNOTATION_TYPE.equals(kind) ? Type.Classification.ANNOTATION
                : modifiers.contains(Modifier.ABSTRACT) ? Type.Classification.ABSTRACT_CLASS
                : Type.Classification.CLASS;
    }

    public static boolean isMethodFromExcludedSuperclass(ExecutableElement method) {
        boolean result = false;
        Element containingClass = method.getEnclosingElement();
        if (containingClass.getKind().isClass() || containingClass.getKind().isInterface()) {
            result = UMLFactoryUtil.methodsFromExcludedSuperclasses().stream().anyMatch(
                    m -> UMLFactoryUtil.similarMethodSignatures(m, method)
                            && env.getTypeUtils().isAssignable(containingClass.asType(), m.getEnclosingElement().asType()));
        }
        result = result || UMLFactoryUtil.isExcludedEnumMethod(method);
        return result;
    }

    private static boolean isVarArgsMethod(Element element) {
        return element instanceof ExecutableElement && ((ExecutableElement) element).isVarArgs();
    }

    private static Parameters createParameters(List<? extends VariableElement> params) {
        Parameters result = new Parameters(null);
        Boolean varargs = null;
        for (VariableElement param : params) {
            if (varargs == null) result = result.varargs(varargs = isVarArgsMethod(param.getEnclosingElement()));
            result = result.add(param.getSimpleName().toString(), TypeNameVisitor.INSTANCE.visit(param.asType()));
        }
        return result;
    }

    static Method createConstructor(Type containingType, ExecutableElement executableElement) {
        Set<Modifier> modifiers = requireNonNull(executableElement, "Executable element is <null>.").getModifiers();
        Method constructor = new Method(containingType, containingType.getName().simple, null);
        constructor.setVisibility(UMLFactoryUtil.visibilityOf(modifiers));
        constructor.isAbstract = modifiers.contains(Modifier.ABSTRACT);
        constructor.isStatic = modifiers.contains(Modifier.STATIC);
        constructor.isDeprecated = env.getElementUtils().isDeprecated(executableElement);
        constructor.addChild(createParameters(executableElement.getParameters()));
        return constructor;
    }

    private static Namespace packageOf(TypeElement typeElement) {
        final ModuleElement module = env.getElementUtils().getModuleOf(typeElement);
        return new Namespace(null, env.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString(),
                module == null ? null : module.getQualifiedName().toString());
    }

    /**
     * Creates an 'empty' type (i.e. without any fields, constructors or methods)
     *
     * @param containingPackage The containing package of the type (optional, will be obtained from typeElement if null).
     * @param type              The type element to create a Type object for.
     * @return The empty Type object.
     */

    public static Type createType(Namespace containingPackage, TypeElement type) {
        requireNonNull(type, "Type element is <null>.");
        if (containingPackage == null) containingPackage = packageOf(type);
        return new Type(containingPackage, UMLFactoryUtil.typeClassificationOf(type), TypeNameVisitor.INSTANCE.visit(type.asType()));
    }

    private static boolean isOnlyDefaultConstructor(Collection<ExecutableElement> constructors) {
        return constructors.size() == 1 && constructors.iterator().next().getParameters().isEmpty();
    }

    private static Field createField(Type containingType, VariableElement variable) {
        Set<Modifier> modifiers = requireNonNull(variable, "Variable element is <null>.").getModifiers();
        Field field = new Field(containingType,
                variable.getSimpleName().toString(),
                TypeNameVisitor.INSTANCE.visit(variable.asType()));
        field.setVisibility(UMLFactoryUtil.visibilityOf(modifiers));
        field.isStatic = modifiers.contains(Modifier.STATIC);
        field.isDeprecated = env.getElementUtils().isDeprecated(variable);
        return field;
    }

    private static Type populateType(Type type, TypeElement typeElement) {
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
                    .map(constructor -> UMLFactoryUtil.createConstructor(type, constructor))
                    .forEach(type::addChild);
        }

        enclosedElements.stream()
                .filter(elem -> ElementKind.METHOD.equals(elem.getKind()))
                .filter(ExecutableElement.class::isInstance).map(ExecutableElement.class::cast)
                .filter(method -> !UMLFactoryUtil.isMethodFromExcludedSuperclass(method))
                .map(method -> UMLFactoryUtil.createMethod(type, method))
                .forEach(type::addChild);

        return env.getElementUtils().isDeprecated(typeElement) ? type.deprecated() : type;
    }

    public static Type createAndPopulateType(Namespace containingPackage, TypeElement type) {
        return populateType(UMLFactoryUtil.createType(containingPackage, type), type);
    }

    public static Visibility visibilityOf(Set<Modifier> modifiers) {
        return modifiers.contains(Modifier.PRIVATE) ? Visibility.PRIVATE
                : modifiers.contains(Modifier.PROTECTED) ? Visibility.PROTECTED
                : modifiers.contains(Modifier.PUBLIC) ? Visibility.PUBLIC
                : Visibility.PACKAGE_PRIVATE;
    }
}
