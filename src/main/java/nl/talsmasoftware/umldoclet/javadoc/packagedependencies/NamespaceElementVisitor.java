/*
 * Copyright 2016-2019 Talsma ICT
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
package nl.talsmasoftware.umldoclet.javadoc.packagedependencies;

import nl.talsmasoftware.umldoclet.uml.Namespace;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.SimpleElementVisitor9;

/**
 * Looks up the namespace the visited element belongs to.
 */
final class NamespaceElementVisitor extends SimpleElementVisitor9<Namespace, Void> {
    static final NamespaceElementVisitor INSTANCE = new NamespaceElementVisitor();

    @Override
    public Namespace visitPackage(PackageElement e, Void aVoid) {
        return new Namespace(null, e.getQualifiedName().toString());
    }

    @Override
    public Namespace defaultAction(Element e, Void aVoid) {
        Element enclosingElement = e.getEnclosingElement();
        return enclosingElement == null ? null : visit(enclosingElement, aVoid);
    }

    @Override
    public Namespace visitUnknown(Element e, Void aVoid) {
        return null;
    }

}
