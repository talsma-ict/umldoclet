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
package nl.talsmasoftware.umldoclet.uml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.logging.Message.INFO_GENERATING_FILE;
import static nl.talsmasoftware.umldoclet.util.FileUtils.ensureParentDir;
import static nl.talsmasoftware.umldoclet.util.FileUtils.withoutExtension;

public class Diagram {

    private UMLNode umlRoot;
    private FileFormat[] formats;
    private File pumlFile, diagramFile;

    public Diagram(UMLNode umlRoot, Collection<FileFormat> formats) {
        this.umlRoot = requireNonNull(umlRoot, "UML root is <null>.");
        this.formats = requireNonNull(formats, "Diagram file formats are <null>.").stream()
                .filter(Objects::nonNull).toArray(FileFormat[]::new);
    }

    /**
     * @return The physical file for the plantuml output.
     */
    private File getPumlFile() {
        if (pumlFile == null) {
            if (umlRoot instanceof ClassUml) pumlFile = ((ClassUml) umlRoot).pumlFile();
            else if (umlRoot instanceof PackageUml) pumlFile = ((PackageUml) umlRoot).pumlFile();
        }
        return requireNonNull(pumlFile, "No physical .puml file location!");
    }

    private File getDiagramFile() {
        if (diagramFile == null) {
            Configuration config = umlRoot.getConfiguration();
            File destinationDir = new File(config.destinationDirectory());
            String relativePumlFile = FileUtils.relativePath(destinationDir, getPumlFile());
            diagramFile = config.images().directory()
                    .map(imgDir -> new File(destinationDir, imgDir))
                    .map(imgDir -> new File(imgDir, relativePumlFile.replace('/', '.')))
                    .orElseGet(() -> new File(destinationDir, relativePumlFile));
        }
        return diagramFile;
    }

    public void render() {
        // 1. Render UML sources
        ((UMLRoot) umlRoot).renderPlantuml(getPumlFile());

        // 2. Render each diagram.
        File diagramFile = null;
        for (FileFormat format : formats) {
            if (diagramFile == null) diagramFile = getDiagramFile();
            diagramFile = new File(diagramFile.getParent(), withoutExtension(diagramFile.getName()) + format.getFileSuffix());

            try (OutputStream out = new FileOutputStream(ensureParentDir(diagramFile))) {
                Link.linkFrom(diagramFile.getParent());
                umlRoot.getConfiguration().logger().info(INFO_GENERATING_FILE, diagramFile);

                new SourceStringReader(umlRoot.toString()).outputImage(out, new FileFormatOption(format));

            } catch (IOException ioe) {
                throw new IllegalStateException("I/O error rendering " + this + ": " + ioe.getMessage(), ioe);
            } finally {
                Link.linkFrom(null);
            }
        }
    }

    @Override
    public String toString() {
        final String name = withoutExtension(getDiagramFile().getPath());
        if (formats.length == 1) return name + formats[0].getFileSuffix();
        return name + Stream.of(formats).map(FileFormat::getFileSuffix)
                .map(s -> s.substring(1))
                .collect(joining(",", ".[", "]"));
    }

}
