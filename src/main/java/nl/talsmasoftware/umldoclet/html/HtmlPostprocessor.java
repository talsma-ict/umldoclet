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
package nl.talsmasoftware.umldoclet.html;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.uml.Diagram;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * Postprocesses the HTML output from the standard doclet to add UML diagrams.
 *
 * @author Sjoerd Talsma
 */
public class HtmlPostprocessor {
    private final Configuration config;
    private final Collection<Diagram> diagrams;

    public HtmlPostprocessor(Configuration config, Collection<Diagram> diagrams) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.diagrams = requireNonNull(diagrams, "Generated diagram collection is <null>.");
    }

    public boolean postProcessHtml() {
        try {
            final File destinationDir = new File(config.destinationDirectory());
            if (!destinationDir.isDirectory() || !destinationDir.canRead()) {
                throw new IllegalStateException("Cannot read from configured destination directory \"" + destinationDir + "\"!");
            }
            final Collection<UmlDiagram> diagrams = new DiagramCollector(config).collectDiagrams();

            long count = Files.walk(destinationDir.toPath())
                    .filter(HtmlFile::isHtmlFile)
                    .map(path -> new HtmlFile(config, path))
                    .map(htmlFile -> htmlFile.process(diagrams))
                    .filter(Boolean::booleanValue).count();
            return true;
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O exception postprocessing HTML files in "
                    + config.destinationDirectory() + ": " + ioe.getMessage(), ioe);
        }
    }

}
