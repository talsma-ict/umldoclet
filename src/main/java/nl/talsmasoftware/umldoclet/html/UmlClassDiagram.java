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
 * Abstraction for a generated class diagram file.
 * <p>
 * The {@link #createPostprocessor(HtmlFile)} method determines whether
 * a found {@code HTML} file corresponds to this class diagram
 * and if so, returns a postprocessor for it.
 * <p>
 * Furthermore, this class 'knows' where (in the HTML) to insert the
 * UML diagram and how to do it. The {@link #newInserter(String)} method
 * provides such an {@link Postprocessor.Inserter inserter} to the postprocessor.
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
        private boolean wrappingAddedToPre = false;
        private boolean clearRightAdded = false;

        private Inserter(String relativePath) {
            super(relativePath);
        }

        /**
         * Processes a single line from the {@code HTML} and inserts the image in the correct line and clears the
         * 'float' style at the correct place.
         *
         * @param line A single line from the {@code HTML} to be processed.
         * @return The processed line
         */
        @Override
        String process(String line) {
            if (!inserted) {
                int idx = line.indexOf("<hr>");
                if (idx >= 0) {
                    idx += 4;
                    inserted = true;
                    return line.substring(0, idx) + System.lineSeparator() + getImageTag() + line.substring(idx);
                }
            } else if (!clearRightAdded) {
                if (!wrappingAddedToPre) {
                    int idx = line.indexOf("<pre>");
                    if (idx >= 0) {
                        wrappingAddedToPre = true;
                        return line.substring(0, idx) + "<pre style=\"white-space:pre-wrap;\">" + line.substring(idx + 5);
                    }
                }
                String cleared = addClearRightStyle(line);
                if (cleared != null) {
                    clearRightAdded = true;
                    return cleared;
                }
            }
            return line;
        }

        /**
         * @return The {@code <img>} tag for this diagram including styling,
         * or an {@code <object>} tag for {@code SVG} diagrams to enable their links.
         */
        private String getImageTag() {
            String style = " style=\"max-width:60%;float:right;\"";
            if (relativePath.endsWith(".svg")) {
                // Render SVG images as objects to make their links work
                return "<object type=\"image/svg+xml\" data=\"" + relativePath + "\" " + style + "></object>";
            }
            return "<img src=\"" + relativePath + "\" alt=\"" + getDiagramName() + " UML Diagram\"" + style + "/>";
        }

        /**
         * Add {@code style="clear: right;"} to the {@code summary} div and return the new line.
         * If the line doesn't contain the summary div, returns {@code null}.
         *
         * @param line The line to check for the right place to add clear:right style to.
         * @return The modified line with the added style or {@code null} if the line was not the right place to do so.
         */
        private String addClearRightStyle(String line) {
            final String summaryDiv = "<div class=\"summary\"";
            int idx = line.indexOf(summaryDiv);
            if (idx < 0) return null;
            int ins = idx + summaryDiv.length();
            line = line.substring(0, ins) + " style=\"clear:right;\"" + line.substring(ins);
            return line;
        }

        /**
         * @return The diagram name to use for the IMG alt tag
         */
        private String getDiagramName() {
            String name = relativePath.substring(relativePath.lastIndexOf('/') + 1);
            int dotIdx = name.lastIndexOf('.');
            if (dotIdx > 0) name = name.substring(0, dotIdx);
            return name;
        }
    }
}
