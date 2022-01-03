/*
 * Copyright 2016-2022 Talsma ICT
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

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.SimpleElementVisitor9;

/**
 * Looks up the String the visited element belongs to.
 *
 * <p>
 * Returns {@code} null for unknown elements or elements not in any String (such as modules etc).
 *
 * @author Sjoerd Talsma
 */
final class PackageElementVisitor extends SimpleElementVisitor9<String, Void> {
    static final PackageElementVisitor INSTANCE = new PackageElementVisitor();

    /**
     * When visiting a package element, we found the String we are looking for, so return the qualified name
     * as a new {@linkplain String} instance.
     *
     * @param e     The package element visited.
     * @param aVoid ignored
     * @return A new String object containing the qualified name of the package element.
     */
    @Override
    public String visitPackage(PackageElement e, Void aVoid) {
        return e.getQualifiedName().toString();
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
    public String defaultAction(Element e, Void aVoid) {
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
    public String visitUnknown(Element e, Void aVoid) {
        return defaultAction(e, aVoid);
    }

}
