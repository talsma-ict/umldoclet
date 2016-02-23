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
import com.sun.javadoc.PackageDoc;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class UMLDiagram extends Renderer {

    final Set<String> encounteredTypes = new LinkedHashSet<>();

    public UMLDiagram(UMLDocletConfig config) {
        super(config, null);
    }

    @Override
    protected UMLDiagram currentDiagram() {
        return this;
    }

    public UMLDiagram singleClassDiagram(ClassDoc classDoc) {
        UMLDiagram classDiagram = new UMLDiagram(config);
        classDiagram.children.add(new ClassRenderer(config, this, classDoc));
        return classDiagram;
    }

    public UMLDiagram singlePackageDiagram(PackageDoc packageDoc) {
        UMLDiagram packageDiagram = new UMLDiagram(config);
        packageDiagram.children.add(new PackageRenderer(config, this, packageDoc));
        return packageDiagram;
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        out.append("@startuml").newline();
        writeChildrenTo(out);
        return out.append("@enduml").newline();
    }

}
