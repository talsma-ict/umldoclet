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
package nl.talsmasoftware.umldoclet.uml.plantuml;

import net.sourceforge.plantuml.FileFormat;
import nl.talsmasoftware.umldoclet.configuration.Configuration;

import java.io.IOException;
import java.io.OutputStream;

import static nl.talsmasoftware.umldoclet.uml.plantuml.RemotePlantumlGenerator.HTTP_URLS;

public interface PlantumlGenerator {
    static PlantumlGenerator getPlantumlGenerator(Configuration configuration) {
        return configuration.plantumlServerUrl()
                .filter(url -> HTTP_URLS.matcher(url).find())
                .map(url -> (PlantumlGenerator) new RemotePlantumlGenerator(url))
                .orElseGet(BuiltinPlantumlGenerator::new);
    }

    void generatePlantumlDiagramFromSource(String plantumlSource, FileFormat format, OutputStream out) throws IOException;

}
