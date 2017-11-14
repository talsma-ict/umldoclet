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

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.*;

import static java.util.Objects.requireNonNull;

public class Type extends Renderer {

    protected final TypeElement typeElement;

    protected Type(UMLDiagram diagram, TypeElement typeElement) {
        super(diagram);
        this.typeElement = requireNonNull(typeElement, "Type element is <null>.");
    }

    protected PackageElement containingPackage() {
        return diagram.env.getElementUtils().getPackageOf(typeElement);
    }

    protected String getSimpleName() {
        StringBuilder sb = new StringBuilder(typeElement.getSimpleName());
        for (Element enclosed = typeElement.getEnclosingElement();
             enclosed != null && (enclosed.getKind().isClass() || enclosed.getKind().isInterface());
             enclosed = enclosed.getEnclosingElement()) {
            sb.insert(0, enclosed.getSimpleName() + ".");
        }
        return sb.toString();
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        output.append(umlTypeOf(typeElement)).whitespace();
        if (!children.isEmpty()) writeChildrenTo(output.append('{').newline()).append('}');
        return output.newline().newline();
    }

    /**
     * Determines the 'UML' type for the class to be rendered.
     * Currently, this can return one of the following: {@code "enum"}, {@code "interface"}, {@code "abstract class"}
     * or otherwise {@code "class"}.
     *
     * @param typeElement The type element to return the uml type for.
     * @return The UML type for the class to be rendered.
     */
    protected static String umlTypeOf(TypeElement typeElement) {
        ElementKind kind = requireNonNull(typeElement, "Type element is <null>.").getKind();
        return ElementKind.ENUM.equals(kind) ? "enum"
                : ElementKind.INTERFACE.equals(kind) ? "interface"
                : ElementKind.ANNOTATION_TYPE.equals(kind) ? "annotation"
                : typeElement.getModifiers().contains(Modifier.ABSTRACT) ? "abstract class"
                : "class";
    }
}
