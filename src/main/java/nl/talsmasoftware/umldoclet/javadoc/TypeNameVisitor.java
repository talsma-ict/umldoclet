/*
 * Copyright 2016-2026 Talsma ICT
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

import nl.talsmasoftware.umldoclet.uml.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor9;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Set;

/// The UML type name implemented as [TypeVisitor].
///
/// @author Sjoerd Talsma
final class TypeNameVisitor extends SimpleTypeVisitor9<TypeName, Void> {
    private static final EnumSet<TypeKind> NO_KNOWN_TYPES = EnumSet.of(
            TypeKind.VOID, TypeKind.NONE, TypeKind.NULL, TypeKind.ERROR, TypeKind.OTHER);

    static final TypeNameVisitor INSTANCE = new TypeNameVisitor();

    private TypeNameVisitor() {
    }

    private static final ThreadLocal<Set<TypeMirror>> VISITED = ThreadLocal.withInitial(
            () -> Collections.newSetFromMap(new IdentityHashMap<>()));

    /// Internal variant of [#visit(TypeMirror, Object)] for calls from inside this visitor itself.
    ///
    /// Main purpose of this method is to limit the endless recursion that would result for types such as
    /// `<T extends Comparable<T>>`
    ///
    /// @param type      The type to visit.
    /// @param parameter The parameter (ignored by our visitor).
    /// @return The type name
    private TypeName _visit(TypeMirror type, Void parameter) {
        if (VISITED.get().add(type)) try {
            return super.visit(type, parameter);
        } finally {
            VISITED.get().remove(type);
            if (VISITED.get().isEmpty()) VISITED.remove();
        }
        return defaultAction(type, parameter);
    }

    @Override
    public TypeName visitPrimitive(PrimitiveType primitiveType, Void parameter) {
        // "byte", "char", "short", "int", "long", "float", "double", "boolean"
        final String primitive = primitiveType.getKind().name().toLowerCase();
        return new TypeName(null, primitive, primitive);
    }

    @Override
    public TypeName visitNoType(NoType noType, Void parameter) {
        // "void", "package", "module", "none"
        final String none = noType.getKind().name().toLowerCase();
        return new TypeName(null, none, none);
    }

    @Override

    public TypeName visitDeclared(DeclaredType declaredType, Void parameter) {
        final Element el = declaredType.asElement();
        final String simpleName = el.getSimpleName().toString();
        final String qualifiedName = el instanceof QualifiedNameable
                ? ((QualifiedNameable) el).getQualifiedName().toString() : simpleName;
        final TypeName[] generics = declaredType.getTypeArguments().stream()
                .map(generic -> _visit(generic, parameter))
                .toArray(TypeName[]::new);
        final String packagename;

        Element enclosingElement = el.getEnclosingElement();
        if (enclosingElement.getKind().isInterface() || enclosingElement.getKind().isClass()) {
            packagename = visit(enclosingElement.asType()).packagename;
        } else {
            int dot = qualifiedName.lastIndexOf('.');
            packagename = dot > 0 ? qualifiedName.substring(0, dot) : null;
        }
        return new TypeName(packagename, simpleName, qualifiedName, generics);
    }

    @Override
    public TypeName visitArray(ArrayType arrayType, Void parameter) {
        return TypeName.Array.of(_visit(arrayType.getComponentType(), parameter));
    }

    @Override
    public TypeName visitTypeVariable(TypeVariable typeVariable, Void parameter) {
        TypeMirror upperBound = typeVariable.getUpperBound();
        if (upperBound != null && !NO_KNOWN_TYPES.contains(upperBound.getKind())) {
            // Fix for #64: Avoid redundant <T extends Object> (which is obviously true for all T's)
            TypeName upperBoundName = _visit(upperBound, parameter);
            if (!Object.class.getName().equals(upperBoundName.qualified)) {
                return TypeName.Variable.extendsBound(typeVariable.toString(), upperBoundName);
            }
        }
        TypeMirror lowerBound = typeVariable.getLowerBound();
        if (lowerBound != null && !NO_KNOWN_TYPES.contains(lowerBound.getKind())) {
            return TypeName.Variable.superBound(typeVariable.toString(), _visit(lowerBound, parameter));
        }

        return defaultAction(typeVariable, parameter);
    }

    @Override
    public TypeName visitWildcard(WildcardType wildcardType, Void parameter) {
        TypeMirror extendsBound = wildcardType.getExtendsBound();
        if (extendsBound != null) return TypeName.Variable.extendsBound("?", _visit(extendsBound, parameter));
        TypeMirror superBound = wildcardType.getSuperBound();
        if (superBound != null) return TypeName.Variable.superBound("?", _visit(superBound, parameter));

        return defaultAction(wildcardType, parameter);
    }

    @Override
    protected TypeName defaultAction(TypeMirror tp, Void parameter) {
        String qualified = tp.toString();
        int lt = qualified.lastIndexOf('<');
        int dot = (lt < 0 ? qualified : qualified.substring(0, lt)).lastIndexOf('.');
        String packagename = dot < 0 ? null : qualified.substring(0, dot);
        return new TypeName(packagename, qualified.substring(dot + 1), qualified);
    }

}
