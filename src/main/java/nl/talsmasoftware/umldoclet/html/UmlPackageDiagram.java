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
 * @author Sjoerd Talsma
 */
final class UmlPackageDiagram extends UmlDiagram {

    private final File basedir, diagramFile;
    private final String extension, pathToCompare;

    UmlPackageDiagram(File basedir, File diagramFile, boolean hasImagesDirectory) {
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

    private String changeHtmlFileNameToDiagram(String htmlFileName) {
        return htmlFileName.replaceFirst("package-summary.html$", "package" + extension);
    }

    @Override
    Optional<Postprocessor> createPostprocessor(HtmlFile html) {
        File htmlFile = html.path.toFile();
        if (pathToCompare.equals(changeHtmlFileNameToDiagram(FileUtils.relativePath(basedir, htmlFile)))) {
            return Optional.of(new Postprocessor(html, this, FileUtils.relativePath(htmlFile, diagramFile)));
        }
        return Optional.empty();
    }

    @Override
    public Postprocessor.Inserter newInserter(String relativePathToDiagram) {
        return new Inserter(relativePathToDiagram);
    }

    private static final class Inserter extends Postprocessor.Inserter {
        boolean tableClosed = false;

        private Inserter(String relativePath) {
            super(relativePath);
        }

        @Override
        String process(String line) {
            if (!inserted && line.contains("<table")) {
                inserted = true;
                return line.replaceFirst("<table", getImageTag() + System.lineSeparator() + "<table");
            }
            return line;
        }

        private String getImageTag() {
            String center = " style=\"display:block;margin-left:auto;margin-right:auto;max-width:100%;\"";
            return "<img src=\"" + relativePath + "\" alt=\"Package summary UML Diagram\"" + center + "/>";
        }
    }

}
