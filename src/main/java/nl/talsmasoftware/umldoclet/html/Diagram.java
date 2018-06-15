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

import nl.talsmasoftware.umldoclet.util.Files;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Abstraction for a generated diagram file.
 * <p>
 * This class determines the relative path to the diagram from a corresponding HTML file.
 *
 * @author Sjoerd Talsma
 */
final class Diagram {

    private final File basedir, diagramFile;
    private final String extension, pathString, fileAsPathString;

    Diagram(Path basedir, Path path) {
        basedir = basedir.normalize();
        path = path.normalize();
        this.basedir = basedir.toFile();
        this.diagramFile = path.toFile();
        String fileName = diagramFile.getName();
        int dotIdx = fileName.lastIndexOf('.');
        this.extension = fileName.substring(dotIdx);
        this.pathString = Files.relativePath(this.basedir, diagramFile);
        if (fileName.indexOf('.') < dotIdx) {
            this.fileAsPathString = fileName.substring(0, dotIdx).replace('.', File.separatorChar) + extension;
        } else {
            this.fileAsPathString = "";
        }
    }

    private String html2extension(Object htmlFileName) {
        return htmlFileName.toString().replaceFirst("\\.html$", extension);
    }

    Optional<String> relativePathFrom(Path htmlPath) {
        File htmlFile = htmlPath.normalize().toFile();
        String html2extension = html2extension(Files.relativePath(basedir, htmlFile));
        if (pathString.equals(html2extension) || fileAsPathString.equals(html2extension)) {
            return Optional.of(Files.relativePath(htmlFile, diagramFile));
        }
        return Optional.empty();
    }

}
