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
import java.nio.file.Path;
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

    private final File basedir, imagesDirectory, diagramFile;
    private final String extension, pathToCompare;

    UmlClassDiagram(Path basedir, Optional<Path> imagesDirectory, Path path) {
        basedir = basedir.normalize();
        path = path.normalize();
        this.basedir = basedir.toFile();
        this.imagesDirectory = imagesDirectory.map(Path::toFile).orElse(null);
        this.diagramFile = path.toFile();
        String fileName = diagramFile.getName();
        int dotIdx = fileName.lastIndexOf('.');
        this.extension = fileName.substring(dotIdx);
        if (imagesDirectory.isPresent()) {
            this.pathToCompare = fileName.substring(0, dotIdx).replace('.', '/') + extension;
        } else {
            this.pathToCompare = FileUtils.relativePath(this.basedir, diagramFile);
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

}
