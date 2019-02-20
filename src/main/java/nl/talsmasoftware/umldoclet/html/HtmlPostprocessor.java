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

import nl.talsmasoftware.umldoclet.config.UMLDocletConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Postprocesses the HTML output from the standard doclet to add UML diagrams.
 *
 * @author Sjoerd Talsma
 */
public class HtmlPostprocessor {
    private final UMLDocletConfig config;

    public HtmlPostprocessor(UMLDocletConfig config) {
        this.config = requireNonNull(config, "Configuration is <null>.");
    }

    public boolean postProcessHtml() throws IOException {
        final File destinationDir = new File(config.htmlDestinationDirectory());
        if (!destinationDir.isDirectory() || !destinationDir.canRead()) {
            throw new IllegalStateException("Cannot read from configured destination directory \"" + destinationDir + "\"!");
        }

        final Collection<UmlDiagram> diagrams = new DiagramCollector(config).collectDiagrams();
        for (HtmlFile htmlFile : allHtmlFiles(destinationDir)) htmlFile.process(diagrams);
        return true;
    }

    private Collection<HtmlFile> allHtmlFiles(File file) {
        Set<HtmlFile> htmlFiles = new LinkedHashSet<>();
        if (file.canRead()) {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) htmlFiles.addAll(allHtmlFiles(child));
            } else {
                Path path = file.toPath();
                if (HtmlFile.isHtmlFile(path)) htmlFiles.add(new HtmlFile(config, path));
            }
        }
        return htmlFiles;
    }
}
