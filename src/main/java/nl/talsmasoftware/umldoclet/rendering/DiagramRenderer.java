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
import nl.talsmasoftware.umldoclet.config.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.logging.GlobalPosition;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17-02-2016.
 *
 * @author Sjoerd Talsma
 */
public class DiagramRenderer extends Renderer {

    protected final UMLDocletConfig config;
    final Set<String> encounteredTypes = new LinkedHashSet<>();

    public DiagramRenderer(UMLDocletConfig config) {
        super(null);
        this.config = requireNonNull(config, "No UML doclet configuration provided.");
    }

    public DiagramRenderer addClass(ClassDoc classDoc) {
        try (GlobalPosition gp = new GlobalPosition(classDoc)) {
            DiagramRenderer classDiagram = new DiagramRenderer(config);
            classDiagram.children.addAll(children);
            classDiagram.children.add(new ClassRenderer(this, classDoc));
            addGlobalCommandsTo(classDiagram.children);
            return classDiagram;
        }
    }

    public DiagramRenderer addPackage(PackageDoc packageDoc) {
        try (GlobalPosition gp = new GlobalPosition(packageDoc)) {
            DiagramRenderer packageDiagram = new DiagramRenderer(config);
            packageDiagram.children.addAll(children);
            packageDiagram.children.add(new PackageRenderer(this, packageDoc));
            addGlobalCommandsTo(packageDiagram.children);
            return packageDiagram;
        }
    }

    public DiagramRenderer addDependencyDiagram(Object dontKnowYet) {
        return this;
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        out.append("@startuml").newline().newline();
        writeChildrenTo(out);
        return out.append("@enduml").newline();
    }

    private void addGlobalCommandsTo(Collection<Renderer> renderers) {
        for (String umlCommand : config.umlCommands()) {
            renderers.add(new CommandRenderer(this, umlCommand));
        }
    }

}
