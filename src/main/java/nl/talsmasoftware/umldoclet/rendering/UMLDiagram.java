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
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class UMLDiagram extends Renderer {

    public UMLDiagram(UMLDocletConfig config) {
        super(config);
    }

    public UMLDiagram singleClassDiagram(ClassDoc classDoc) {
        UMLDiagram classDiagram = new UMLDiagram(config);
        classDiagram.children.add(new ClassRenderer(config, classDoc));
        return classDiagram;
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        out.append("@startuml").newline();
        writeChildrenTo(out);
        return out.append("@enduml").newline();
    }

}
