/*
 * Copyright 2016-2025 Talsma ICT
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

import nl.talsmasoftware.umldoclet.configuration.ImageConfig;

import java.io.File;
import java.nio.file.Path;

final class PackageDependenciesInserter extends DiagramFile {
    private final Path index;
    private final Path overviewSummary;
    private final Path moduleSummary;

    PackageDependenciesInserter(File basedir, File diagramFile, ImageConfig.Format format) {
        super(basedir, diagramFile, format);
        this.index = new File(basedir, "index.html").toPath();
        this.overviewSummary = new File(basedir, "overview-summary.html").toPath();
        this.moduleSummary = "package-dependencies.svg".equals(diagramFile.getName())
                ? new File(diagramFile.getParent(), "module-summary.html").toPath()
                : null;
    }

    @Override
    boolean matches(HtmlFile htmlFile) {
        final Path path = htmlFile.path;
        return index.equals(path) || overviewSummary.equals(path) || moduleSummary.equals(path);
    }

    @Override
    public Postprocessor.Inserter newInserter(String relativePathToDiagram) {
        return new Inserter(relativePathToDiagram, ImageConfig.Format.SVG.equals(format));
    }

    private static final class Inserter extends Postprocessor.Inserter {
        private static final String CENTER_STYLE = "style=\"display:block;margin-left:auto;margin-right:auto;max-width:95%;\"";

        private final boolean insertAsObject;

        private Inserter(String relativePath, boolean insertAsObject) {
            super(relativePath);
            this.insertAsObject = insertAsObject;
        }

        @Override
        String process(String line) {
            if (!inserted) {
                int idx = line.indexOf("<table");
                if (idx >= 0) {
                    inserted = true;
                    return line.substring(0, idx) + getImageTag() + System.lineSeparator() + line.substring(idx);
                }
                idx = line.indexOf("<div id=\"all-packages-table\">");
                if (idx >= 0) {
                    inserted = true;
                    idx = line.indexOf('>', idx) + 1;
                    return line.substring(0, idx) + System.lineSeparator() + getImageTag() + System.lineSeparator() + line.substring(idx);
                }
                idx = line.indexOf("<div class=\"module-signature\">");
                if (idx >= 0) {
                    inserted = true;
                    idx = line.indexOf('>', idx) + 1;
                    return line.substring(0, idx) + System.lineSeparator() + getImageTag() + System.lineSeparator() + line.substring(idx);
                }
            }
            return line;
        }

        private String getImageTag() {
            if (insertAsObject) { // Render SVG images as objects to make their links work
                return "<object type=\"image/svg+xml\" data=\"" + relativePath + "\" " + CENTER_STYLE + "></object>";
            }
            return "<img src=\"" + relativePath + "\" alt=\"Package dependencies\" " + CENTER_STYLE + "/>";
        }

    }
}
