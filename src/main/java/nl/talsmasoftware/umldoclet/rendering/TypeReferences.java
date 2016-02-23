/*
 * Copyright (C) 2016 Talsma ICT
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
package nl.talsmasoftware.umldoclet.rendering;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Created on 23-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class TypeReferences extends Renderer {

    private final ClassDoc classDoc;

    public TypeReferences(UMLDocletConfig config, UMLDiagram diagram, ClassDoc classDoc) {
        super(config, diagram);
        this.classDoc = requireNonNull(classDoc, "No class documentation provided.");
    }

    private IndentingPrintWriter declareSuperclass(IndentingPrintWriter out, Type superclassType) {
        final String superclassName = superclassType.qualifiedTypeName();
        if (currentDiagram().encounteredTypes.add(superclassName)) {
            out.append("class ").append(superclassName).newline();
        }
        return out;
    }

    private IndentingPrintWriter declareInterface(IndentingPrintWriter out, Type interfaceType) {
        final String interfaceName = interfaceType.qualifiedTypeName();
        if (currentDiagram().encounteredTypes.add(interfaceName)) {
            out.append("interface ").append(interfaceName).newline();
        }
        return out;
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        // Add superclass.
        declareSuperclass(out, classDoc.superclassType())
                .append(classDoc.superclassType().qualifiedTypeName()).append(" <|-- ").append(classDoc.qualifiedTypeName()).newline();
        for (Type interfaceType : classDoc.interfaceTypes()) {
            declareInterface(out, interfaceType)
                    .append(interfaceType.qualifiedTypeName()).append(" <|-- ").append(classDoc.qualifiedTypeName()).newline();
        }
        return out;
    }

}
