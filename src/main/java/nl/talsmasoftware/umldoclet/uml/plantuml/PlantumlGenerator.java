/*
 * Copyright 2016-2026 Talsma ICT
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
package nl.talsmasoftware.umldoclet.uml.plantuml;

import net.sourceforge.plantuml.FileFormat;
import nl.talsmasoftware.umldoclet.configuration.Configuration;

import java.io.IOException;
import java.io.OutputStream;

import static nl.talsmasoftware.umldoclet.uml.plantuml.RemotePlantumlGenerator.HTTP_URLS;

/// Generator that can convert PlantUML source code into a diagram in various formats.
public interface PlantumlGenerator {
    /// Factory method to get the appropriate [PlantumlGenerator] based on the configuration.
    ///
    /// @param configuration The configuration to use.
    /// @return The appropriate [PlantumlGenerator].
    static PlantumlGenerator getPlantumlGenerator(Configuration configuration) {
        return configuration.plantumlServerUrl()
                .filter(url -> HTTP_URLS.matcher(url).find())
                .map(url -> (PlantumlGenerator) new RemotePlantumlGenerator(url))
                .orElseGet(BuiltinPlantumlGenerator::new);
    }

    /// Generates a diagram from the given PlantUML source code.
    ///
    /// @param plantumlSource The PlantUML source code to generate a diagram from.
    /// @param format         The format of the diagram to generate.
    /// @param out            The output stream to write the diagram to.
    /// @throws IOException If an I/O error occurs.
    void generatePlantumlDiagramFromSource(String plantumlSource, FileFormat format, OutputStream out) throws IOException;

}
