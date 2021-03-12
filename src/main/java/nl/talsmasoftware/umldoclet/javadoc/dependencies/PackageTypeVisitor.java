/*
 * Copyright 2016-2021 Talsma ICT
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
package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor9;

class PackageTypeVisitor extends SimpleTypeVisitor9<String, Void> {
    static final PackageTypeVisitor INSTANCE = new PackageTypeVisitor();

    @Override
    public String visitUnknown(TypeMirror t, Void aVoid) {
        return null;
    }

    @Override
    public String visitArray(ArrayType t, Void aVoid) {
        return this.visit(t.getComponentType(), aVoid);
    }

    @Override
    public String visitDeclared(DeclaredType t, Void aVoid) {
        return PackageElementVisitor.INSTANCE.visit(t.asElement().getEnclosingElement(), aVoid);
    }

    @Override
    public String visitError(ErrorType t, Void aVoid) {
        return PackageElementVisitor.INSTANCE.visit(t.asElement().getEnclosingElement(), aVoid);
    }

    @Override
    public String visitWildcard(WildcardType t, Void aVoid) {
        TypeMirror bound = t.getExtendsBound();
        if (bound == null) bound = t.getSuperBound();
        return bound == null ? null : this.visit(bound, aVoid);
    }

    @Override
    public String visitExecutable(ExecutableType t, Void aVoid) {
        TypeMirror receiverType = t.getReceiverType();
        return receiverType == null ? null : this.visit(receiverType);
    }

}
