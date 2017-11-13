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

import jdk.javadoc.doclet.DocletEnvironment;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Renders a new UML diagram.
 * <p>
 * Responsible for rendering the <code>{@literal @}startuml</code> and <code>{@literal @}enduml</code> lines.
 *
 * @author Sjoerd Talsma
 */
public abstract class UMLDiagram extends Renderer {

    protected final Configuration config;
    protected final DocletEnvironment env;

    public UMLDiagram(Configuration config, DocletEnvironment env) {
        super(null);
        this.config = requireNonNull(config, "No UML Doclet configuration provided.");
        this.env = requireNonNull(env, "Doclet environment is <null>.");
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        out.append("@startuml").newline().newline();
        writeChildrenTo(out);
        return out.append("@enduml").newline();
    }

}
