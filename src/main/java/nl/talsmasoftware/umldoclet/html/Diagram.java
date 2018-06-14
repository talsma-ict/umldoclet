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

import java.io.File;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
final class Diagram {

    private final Path basedir, path;
    private final String extension, pathString, fileAsPathString;

    Diagram(Path basedir, Path path) {
        this.basedir = requireNonNull(basedir, "Base directory is <null>.");
        requireNonNull(path, "Diagram file is <null>.");
        this.path = this.basedir.relativize(path);
        String filename = this.path.getFileName().toString();
        int dotIdx = filename.lastIndexOf('.');
        this.extension = filename.substring(dotIdx);
        this.pathString = path.toString();
        this.fileAsPathString = filename.substring(0, dotIdx)
                .replace('.', File.separatorChar) + extension;
    }

    private String html2extension(Object htmlFileName) {
        return htmlFileName.toString().replaceFirst("\\.html$", extension);
    }

    boolean correspondsWith(Path htmlPath) {
        htmlPath = basedir.relativize(htmlPath);
        String html2extension = html2extension(htmlPath);
        return pathString.equals(html2extension) || fileAsPathString.equals(html2extension);
    }
}
