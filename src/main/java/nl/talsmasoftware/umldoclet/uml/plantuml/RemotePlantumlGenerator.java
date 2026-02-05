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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.code.ArobaseStringCompressor;
import net.sourceforge.plantuml.code.AsciiEncoder;
import net.sourceforge.plantuml.code.CompressionZlib;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/// Generator that uses a remote PlantUML server to generate diagrams.
@SuppressFBWarnings(value = "URLCONNECTION_SSRF_FD", justification = "We only allow http(s) urls.")
public class RemotePlantumlGenerator implements PlantumlGenerator {
    /// Pattern to match http and https URLs.
    public static final Pattern HTTP_URLS = Pattern.compile("^https?://");

    private static final String DEFAULT_PLANTUML_BASE_URL = "https://www.plantuml.com/plantuml/";
    private static final Transcoder TRANSCODER =
            TranscoderImpl.utf8(new AsciiEncoder(), new ArobaseStringCompressor(), new CompressionZlib());

    private final String baseUrl;

    /// Creates a new [RemotePlantumlGenerator] with the given base URL.
    ///
    /// @param baseUrl The base URL of the remote PlantUML server.
    public RemotePlantumlGenerator(final String baseUrl) {
        String url = Objects.toString(baseUrl, DEFAULT_PLANTUML_BASE_URL);
        if (!HTTP_URLS.matcher(url).find()) {
            throw new IllegalArgumentException("Unsupported PlantUML server base url: [" + url + "].");
        }
        if (!url.endsWith("/")) url += "/";
        this.baseUrl = url;
    }

    @Override
    public void generatePlantumlDiagramFromSource(String plantumlSource, FileFormat format, OutputStream out) {
        final String encodedDiagram = encodeDiagram(plantumlSource);
        final String diagramUrl = baseUrl + format.name().toLowerCase() + '/' + encodedDiagram;
        try (InputStream in = new URL(diagramUrl).openConnection().getInputStream()) {
            final byte[] buf = new byte[4096];
            for (int read = in.read(buf); read >= 0; read = in.read(buf)) {
                out.write(buf, 0, read);
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Error generating diagram: " + e.getMessage(), e);
        }
    }

    private String encodeDiagram(final String diagramSource) {
        try {
            // TODO internalize transcoder to be able to remove PlantUML dependency altogether.
            return TRANSCODER.encode(requireNonNull(diagramSource, "UML diagram source was <null>."));
        } catch (IOException ioe) {
            throw new IllegalStateException("Error encoding diagram: " + ioe.getMessage(), ioe);
        }
    }

}
