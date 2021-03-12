/*
 * Copyright 2016-2021 Talsma ICT
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

import java.io.File;
import java.nio.file.Path;

final class PackageDependenciesInserter extends DiagramFile {
    private final Path index;
    private final Path overviewSummary;

    PackageDependenciesInserter(File basedir, File diagramFile) {
        super(basedir, diagramFile);
        this.index = new File(basedir, "index.html").toPath();
        this.overviewSummary = new File(basedir, "overview-summary.html").toPath();
    }

    @Override
    boolean matches(HtmlFile htmlFile) {
        final Path path = htmlFile.path;
        return index.equals(path) || overviewSummary.equals(path);
    }

    @Override
    public Postprocessor.Inserter newInserter(String relativePathToDiagram) {
        return new Inserter(relativePathToDiagram);
    }

    private static final class Inserter extends Postprocessor.Inserter {
        private static final String CENTER_STYLE = "style=\"display:block;margin-left:auto;margin-right:auto;max-width:95%;\"";

        private Inserter(String relativePath) {
            super(relativePath);
        }

        @Override
        String process(String line) {
            if (!inserted) {
                int idx = line.indexOf("<table");
                if (idx >= 0) {
                    inserted = true;
                    return line.substring(0, idx) + getImageTag() + System.lineSeparator() + line.substring(idx);
                }
            }
            return line;
        }

        private String getImageTag() {
            if (relativePath.endsWith(".svg")) {
                // Render SVG images as objects to make their links work
                return "<object type=\"image/svg+xml\" data=\"" + relativePath + "\" " + CENTER_STYLE + "></object>";
            }
            return "<img src=\"" + relativePath + "\" alt=\"Package dependencies\" " + CENTER_STYLE + "/>";
        }

    }
}
