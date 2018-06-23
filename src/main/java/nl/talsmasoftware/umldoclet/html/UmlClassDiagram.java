/*
 * Copyright 2016-2018 Talsma ICT
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

import nl.talsmasoftware.umldoclet.util.FileUtils;

import java.io.File;
import java.util.Optional;

/**
 * Abstraction for a generated diagram file.
 * <p>
 * This class determines the relative path to the diagram from a corresponding HTML file.
 * <p>
 * TODO: Does not yet work correctly with inner classes!
 *
 * @author Sjoerd Talsma
 */
final class UmlClassDiagram extends UmlDiagram {

    private final File basedir, diagramFile;
    private final String extension, pathToCompare;

    UmlClassDiagram(File basedir, File diagramFile, boolean hasImagesDirectory) {
        this.basedir = basedir;
        this.diagramFile = diagramFile;
        final String fileName = diagramFile.getName();
        int dotIdx = fileName.lastIndexOf('.');
        this.extension = fileName.substring(dotIdx);
        if (hasImagesDirectory) {
            this.pathToCompare = fileName.substring(0, dotIdx).replace('.', '/') + extension;
        } else {
            this.pathToCompare = FileUtils.relativePath(this.basedir, this.diagramFile);
        }
    }

    private String changeHtmlFileNameExtension(Object htmlFileName) {
        return htmlFileName.toString().replaceFirst("\\.html$", extension);
    }

    @Override
    Optional<Postprocessor> createPostprocessor(HtmlFile html) {
        File htmlFile = html.path.toFile();
        if (pathToCompare.equals(changeHtmlFileNameExtension(FileUtils.relativePath(basedir, htmlFile)))) {
            return Optional.of(new Postprocessor(html, this, FileUtils.relativePath(htmlFile, diagramFile)));
        }
        return Optional.empty();
    }

    @Override
    public Postprocessor.Inserter newInserter(String relativePathToDiagram) {
        return new Inserter(relativePathToDiagram);
    }

    private final class Inserter extends Postprocessor.Inserter {
        private boolean summaryDivCleared = false;

        private Inserter(String relativePath) {
            super(relativePath);
        }

        @Override
        String process(String line) {
            if (!inserted) {
                int idx = line.indexOf("<hr>");
                if (idx >= 0) {
                    idx += 4;
                    inserted = true;
                    return line.substring(0, idx) + System.lineSeparator() + getImageTag() + line.substring(idx);
                }
            } else if (!summaryDivCleared) {
                String cleared = clearSummaryDiv(line);
                if (cleared != null) {
                    summaryDivCleared = true;
                    return cleared;
                }
            }
            return line;
        }

        private String getImageTag() {
            String style = " style=\"max-width:60%;float:right;\"";
            if (relativePath.endsWith(".svg")) {
                // Render SVG images as objects to make their links work
                return "<object type=\"image/svg+xml\" data=\"" + relativePath + "\" " + style + "></object>";
            }

            String name = relativePath.substring(relativePath.lastIndexOf('/') + 1);
            int dotIdx = name.lastIndexOf('.');
            if (dotIdx > 0) name = name.substring(0, dotIdx);
            return "<img src=\"" + relativePath + "\" alt=\"" + name + " UML Diagram\"" + style + "/>";
        }

        private String clearSummaryDiv(String line) {
            final String summaryDiv = "<div class=\"summary\"";
            int idx = line.indexOf(summaryDiv);
            if (idx < 0) return null;
            int ins = idx + summaryDiv.length();
            line = line.substring(0, ins) + " style=\"clear: right;\"" + line.substring(ins);
            return line;
        }
    }
}
