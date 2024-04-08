/*
 * Copyright 2016-2024 Talsma ICT
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

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Function;

import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

/**
 * Simple data object containing a (possibly) derived type name with a cardinality.
 *
 * @author Sjoerd Talsma
 */
final class TypeNameWithCardinality {

    final TypeName typeName;
    final String cardinality;

    private TypeNameWithCardinality(TypeName typeName, String cardinality) {
        this.typeName = typeName;
        this.cardinality = cardinality;
    }

    /**
     * Returns a function that applies the TypeNameVisitor, but also:
     * <ol>
     * <li>Checks if a type is an {@code Array}, {@code Iterable} or {@code Stream} to return the type argument with cardinality {@code "*"}</li>
     * <li>Checks if a type is a Java 8 or Guava {@code Optional} object to return the type argument with cardinality {@code "0..1"}</li>
     * <li>Otherwise, the name of the actual type is returned with cardinality {@code null}</li>
     * </ol>
     *
     * @param typeUtils The type utils to use for supertype introspection (required).
     * @return The function to return TypeName with cardinality for use in same-package references.
     */
    static Function<TypeMirror, TypeNameWithCardinality> function(final Types typeUtils) {
        requireNonNull(typeUtils, "Type utils are <null>.");
        return type -> {
            if (type instanceof ArrayType) {
                return handleArrayType((ArrayType) type, typeUtils);
            } else if (type instanceof DeclaredType) {
                return handleDeclaredType((DeclaredType) type, typeUtils);
            } else {
                return new TypeNameWithCardinality(TypeNameVisitor.INSTANCE.visit(type), null);
            }
        };
    }

    private static TypeNameWithCardinality handleArrayType(ArrayType arrayType, Types typeUtils) {
        TypeName componentName = TypeNameVisitor.INSTANCE.visit(arrayType.getComponentType());
        return new TypeNameWithCardinality(componentName, "*");
    }

    private static TypeNameWithCardinality handleDeclaredType(DeclaredType declaredType, Types typeUtils) {
        Queue<TypeMirror> superTypes = new ArrayDeque<>(Collections.singleton(declaredType));
        Set<String> checkedTypes = new HashSet<>();
        while (!superTypes.isEmpty()) {
            TypeMirror superType = superTypes.poll();
            String qName = TypeNameVisitor.INSTANCE.visit(superType).qualified;
            if (checkedTypes.add(qName)) { // Don't reiterate
                TypeNameWithCardinality typeNameWithCardinality = checkCardinality(superType, qName);
                if (typeNameWithCardinality != null) return typeNameWithCardinality;
                superTypes.addAll(typeUtils.directSupertypes(superType));
            }
        }
        return new TypeNameWithCardinality(TypeNameVisitor.INSTANCE.visit(declaredType), null);
    }

    private static TypeNameWithCardinality checkCardinality(TypeMirror superType, String qualifiedName) {
        String cardinality;
        if ("java.util.Optional".equals(qualifiedName) || "com.google.common.base.Optional".equals(qualifiedName)) {
            cardinality = "0..1";
        } else if ("java.lang.Iterable".equals(qualifiedName) || "java.util.stream.Stream".equals(qualifiedName)) {
            cardinality = "*";
        } else {
            cardinality = null;
        }

        // Assumption: the 'iterable' and 'optional' types are DeclaredTypes with a single TypeArgument.
        Optional<TypeName> typeArgument = Optional.ofNullable(cardinality)
                .map(c -> superType instanceof DeclaredType ? (DeclaredType) superType : null)
                .map(DeclaredType::getTypeArguments)
                .map(args -> args.size() == 1 ? args.get(0) : null)
                .map(TypeNameVisitor.INSTANCE::visit);
        return typeArgument.map(ta -> new TypeNameWithCardinality(ta, cardinality)).orElse(null);
    }
}
