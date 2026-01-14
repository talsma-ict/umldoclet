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

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

/// Simple data object containing a (possibly) derived type name with a cardinality.
///
/// @author Sjoerd Talsma
final class TypeNameWithCardinality {

    final TypeName typeName;
    final String cardinality;

    private TypeNameWithCardinality(TypeName typeName, String cardinality) {
        this.typeName = typeName;
        this.cardinality = cardinality;
    }

        /// Returns a function that applies the TypeNameVisitor, but also:
    /// <ol>
    /// <li>Checks if a type is an `Array`, `Iterable` or `Stream` to return the type argument with cardinality `"*"`</li>
    /// <li>Checks if a type is a Java 8 or Guava `Optional` object to return the type argument with cardinality `"0..1"`</li>
    /// <li>Otherwise, the name of the actual type is returned with cardinality `null`</li>
    /// </ol>
    ///
    /// @param typeUtils The type utils to use for supertype introspection (required).
    /// @return The function to return TypeName with cardinality for use in same-package references.
    static Function<TypeMirror, TypeNameWithCardinality> function(final Types typeUtils) {
        requireNonNull(typeUtils, "Type utils are <null>.");
        return type -> {
            if (type instanceof ArrayType) {
                TypeName componentName = TypeNameVisitor.INSTANCE.visit(((ArrayType) type).getComponentType());
                return new TypeNameWithCardinality(componentName, "*");
            } else if (type instanceof DeclaredType) {
                Queue<TypeMirror> superTypes = new ArrayDeque<>(singleton(type));
                Set<String> checkedTypes = new HashSet<>();
                while (!superTypes.isEmpty()) {
                    TypeMirror superType = superTypes.poll();
                    String qName = TypeNameVisitor.INSTANCE.visit(superType).qualified;
                    if (checkedTypes.add(qName)) { // Don't reiterate
                        String cardinality = null;
                        if ("java.util.Optional".equals(qName) || "com.google.common.base.Optional".equals(qName)) {
                            cardinality = "0..1";
                        } else if ("java.lang.Iterable".equals(qName) || "java.util.stream.Stream".equals(qName)) {
                            cardinality = "*";
                        }

                        // Assumption: the 'iterable' and 'optional' types are DeclaredTypes with a single TypeArgument.
                        Optional<TypeName> typeArgument = Optional.ofNullable(cardinality)
                                .map(c -> superType instanceof DeclaredType ? (DeclaredType) superType : null)
                                .map(DeclaredType::getTypeArguments)
                                .map(args -> args.size() == 1 ? args.get(0) : null)
                                .map(TypeNameVisitor.INSTANCE::visit);
                        if (typeArgument.isPresent()) {
                            return new TypeNameWithCardinality(typeArgument.get(), cardinality);
                        }

                        superTypes.addAll(typeUtils.directSupertypes(superType));
                    }
                }
            }

            return new TypeNameWithCardinality(TypeNameVisitor.INSTANCE.visit(type), null);
        };
    }
}
