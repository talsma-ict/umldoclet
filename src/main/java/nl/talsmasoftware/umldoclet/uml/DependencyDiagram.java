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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.File;

public class DependencyDiagram extends Diagram {

    private File pumlFile = null;

    public DependencyDiagram(Configuration config) {
        super(config);
    }

    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            pumlFile = new File(getConfiguration().destinationDirectory(), "package-dependencies.puml");
        }
        return pumlFile;
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        output.indent()
                .append("set namespaceSeparator none").newline()
                .append("hide circle").newline()
                .append("hide empty fields").newline()
                .append("hide empty methods").newline().newline();
        return super.writeChildrenTo(output);
    }
}
