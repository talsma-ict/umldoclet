/*
 * Copyright 2016-2017 Talsma ICT
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

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.type.*;
import javax.lang.model.util.SimpleTypeVisitor9;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * The UML type name implemented as {@link TypeVisitor}.
 *
 * @author Sjoerd Talsma
 */
final class TypeName extends SimpleTypeVisitor9<String, Void> {
    private final boolean qualifiedNames, qualifiedGenerics;

    TypeName(boolean qualifiedNames, boolean qualifiedGenerics) {
        this.qualifiedNames = qualifiedNames;
        this.qualifiedGenerics = qualifiedGenerics;
    }

    private TypeVisitor<String, Void> genericsDelegate() {
        return qualifiedNames == qualifiedGenerics ? this : new TypeName(qualifiedGenerics, qualifiedGenerics);
    }

    @Override
    public String visitPrimitive(PrimitiveType primitiveType, Void parameter) {
        // "byte", "char", "short", "int", "long", "float", "double", "boolean"
        return primitiveType.getKind().name().toLowerCase();
    }

    @Override
    public String visitNoType(NoType noType, Void parameter) {
        return noType.getKind().name().toLowerCase(); // "void", "package", "module", "none"
    }

    @Override
    public String visitDeclared(DeclaredType declaredType, Void parameter) {
        Element el = declaredType.asElement();
        List<? extends TypeMirror> args = declaredType.getTypeArguments();
        Name name = qualifiedNames && el instanceof QualifiedNameable
                ? ((QualifiedNameable) el).getQualifiedName() : el.getSimpleName();
        return args.isEmpty() ? name.toString() : name + args.stream()
                .map(typeArgument -> genericsDelegate().visit(typeArgument, parameter))
                .collect(joining(", ", "<", ">"));
    }

}
