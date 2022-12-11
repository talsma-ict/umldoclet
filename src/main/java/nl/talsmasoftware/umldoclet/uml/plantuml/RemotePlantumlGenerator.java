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
import net.sourceforge.plantuml.code.ArobaseStringCompressor;
import net.sourceforge.plantuml.code.AsciiEncoder;
import net.sourceforge.plantuml.code.CompressionZlib;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderImpl;

import java.io.IOException;
import java.io.OutputStream;

import static java.util.Objects.requireNonNull;

public class RemotePlantumlGenerator implements PlantumlGenerator {
    private final String baseUrl;
    private final Transcoder transcoder =
            TranscoderImpl.utf8(new AsciiEncoder(), new ArobaseStringCompressor(), new CompressionZlib());

    public RemotePlantumlGenerator(final String baseUrl) {
        this.baseUrl = requireNonNull(baseUrl, "Base URL for remote PlantUML server url is <null>.");
    }

    @Override
    public void generatePlantumlDiagramFromSource(String plantumlSource, FileFormat format, OutputStream out) {
        final String encodedDiagram = encodeDiagram(plantumlSource);
        System.out.println(encodedDiagram);
    }

    private String encodeDiagram(final String diagramSource) {
        try {
            return transcoder.encode(requireNonNull(diagramSource, "UML diagram source was <null>."));
        } catch (IOException ioe) {
            throw new IllegalStateException("Error encoding diagram: " + ioe.getMessage(), ioe);
        }
    }

}
