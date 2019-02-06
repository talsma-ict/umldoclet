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
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.logging.Message.INFO_GENERATING_FILE;
import static nl.talsmasoftware.umldoclet.util.FileUtils.ensureParentDir;
import static nl.talsmasoftware.umldoclet.util.FileUtils.withoutExtension;

public class Diagram {

    private final Configuration config;
    private final UMLNode umlRoot;
    private final FileFormat[] formats;
    private File pumlFile, diagramFile;

    public Diagram(UMLNode umlRoot, Collection<FileFormat> formats) {
        this.umlRoot = requireNonNull(umlRoot, "UML root is <null>.");
        this.config = requireNonNull(umlRoot.getConfiguration(), "Configuration is <null>");
        this.formats = requireNonNull(formats, "Diagram file formats are <null>.").stream()
                .filter(Objects::nonNull).toArray(FileFormat[]::new);
    }

    /**
     * @return The physical file for the plantuml output.
     */
    private File getPlantUmlFile() {
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
            String relativePumlFile = FileUtils.relativePath(destinationDir, getPlantUmlFile());
            diagramFile = config.images().directory()
                    .map(imgDir -> new File(destinationDir, imgDir))
                    .map(imgDir -> new File(imgDir, relativePumlFile.replace('/', '.')))
                    .orElseGet(() -> new File(destinationDir, relativePumlFile));
        }
        return diagramFile;
    }

    public void render() {
        // 1. Render UML sources
        final String plantumlSource = renderPlantumlSource();

        // 2. Render each diagram.
        File diagramFile = null; // TODO: Iterate diagram files, not formats
        for (FileFormat format : formats) {
            if (diagramFile == null) diagramFile = getDiagramFile();
            diagramFile = new File(diagramFile.getParent(), withoutExtension(diagramFile.getName()) + format.getFileSuffix());

            try (OutputStream out = new FileOutputStream(ensureParentDir(diagramFile))) {
                Link.linkFrom(diagramFile.getParent());
                umlRoot.getConfiguration().logger().info(INFO_GENERATING_FILE, diagramFile);

                new SourceStringReader(plantumlSource).outputImage(out, new FileFormatOption(format));

            } catch (IOException ioe) {
                throw new IllegalStateException("I/O error rendering " + this + ": " + ioe.getMessage(), ioe);
            } finally {
                Link.linkFrom(null);
            }
        }
    }

    private String renderPlantumlSource() {
        if (config.renderPumlFile()) {
            File pumlFile = getPlantUmlFile();
            try (IndentingPrintWriter writer = createPlantumlWriter(requireNonNull(pumlFile, "Plantuml File is <null>."))) {
                config.logger().info(INFO_GENERATING_FILE, pumlFile);
                umlRoot.writeTo(IndentingPrintWriter.wrap(writer, config.indentation()));
            }
        }
        return umlRoot.toString(); // TODO: re-use rendered source.
    }

    private IndentingPrintWriter createPlantumlWriter(File pumlFile) {
        try {

            Writer pumlWriter = new OutputStreamWriter(new FileOutputStream(ensureParentDir(pumlFile)), config.umlCharset());
            return IndentingPrintWriter.wrap(pumlWriter, config.indentation());

        } catch (IOException ioe) {
            throw new IllegalStateException("Could not create writer to PlantUML file: " + pumlFile, ioe);
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
