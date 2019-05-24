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
package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import nl.talsmasoftware.umldoclet.uml.Namespace;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor9;

/**
 * Looks up the namespace the visited element belongs to.
 *
 * <p>
 * Returns {@code} null for unknown elements or elements not in any namespace (such as modules etc).
 *
 * @author Sjoerd Talsma
 */
final class NamespaceElementVisitor extends SimpleElementVisitor9<Namespace, Void> {
    static final NamespaceElementVisitor INSTANCE = new NamespaceElementVisitor();

    /**
     * When visiting a package element, we found the namespace we are looking for, so return the qualified name
     * as a new {@linkplain Namespace} instance.
     *
     * @param e     The package element visited.
     * @param aVoid ignored
     * @return A new Namespace object containing the qualified name of the package element.
     */
    @Override
    public Namespace visitPackage(PackageElement e, Void aVoid) {
        return new Namespace(null, e.getQualifiedName().toString());
    }

    /**
     * The default action is visiting the enclosing element until we reach the package element.
     *
     * @param e     A non-package element being visited
     * @param aVoid ignored
     * @return The result of visiting the enclosing element of this element,
     * or {@code null} if there is no enclosing element (e.g. for a module element).
     */
    @Override
    public Namespace defaultAction(Element e, Void aVoid) {
        Element enclosingElement = e.getEnclosingElement();
        return enclosingElement == null ? null : visit(enclosingElement, aVoid);
    }

    /**
     * When we reach an unknown element we just visit the enclosing element too (same as the default action).
     *
     * @param e     The unknown element being visited
     * @param aVoid ignored
     * @return the result of the {@code defaultAction}
     * @see #defaultAction(Element, Void)
     */
    @Override
    public Namespace visitUnknown(Element e, Void aVoid) {
        return defaultAction(e, aVoid);
    }

}
