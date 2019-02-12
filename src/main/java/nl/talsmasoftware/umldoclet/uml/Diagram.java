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
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.writers.StringBufferingWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.util.FileUtils.ensureParentDir;
import static nl.talsmasoftware.umldoclet.util.FileUtils.relativePath;
import static nl.talsmasoftware.umldoclet.util.FileUtils.withoutExtension;

public abstract class Diagram extends UMLNode {

    private final Configuration config;
    private final FileFormat[] formats;
    private File diagramBaseFile;

    protected Diagram(Configuration config) {
        super(null);
        this.config = requireNonNull(config, "Configuration is <null>");
        this.formats = config.images().formats().stream().filter(Objects::nonNull).toArray(FileFormat[]::new);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append("@startuml").newline().newline();
        writeChildrenTo(output);
        writeFooterTo(output);
        output.newline().append("@enduml").newline();
        return output;
    }

    private <IPW extends IndentingPrintWriter> IPW writeFooterTo(IPW output) {
        output.indent().newline()
                .append("center footer").whitespace()
                .append(config.logger().localize(
                        Message.DOCLET_UML_FOOTER,
                        Message.DOCLET_VERSION,
                        net.sourceforge.plantuml.version.Version.versionString()))
                .newline();
        return output;
    }

    public Configuration getConfiguration() {
        return config;
    }

    /**
     * Determine the physical file location for the plantuml output.
     *
     * <p>This will even be called if {@code -createPumlFiles} is not enabled,
     * to determine the {@linkplain #getDiagramBaseFile()}.
     *
     * @return The physical file for the plantuml output.
     */
    protected abstract File getPlantUmlFile();

    /**
     * @return The diagram file without extension.
     * @see #getDiagramFile(FileFormat)
     */
    private File getDiagramBaseFile() {
        if (diagramBaseFile == null) {
            File destinationDir = new File(config.destinationDirectory());
            String relativeBaseFile = withoutExtension(relativePath(destinationDir, getPlantUmlFile()));
            if (config.images().directory().isPresent()) {
                File imageDir = new File(destinationDir, config.images().directory().get());
                diagramBaseFile = new File(imageDir, relativeBaseFile.replace('/', '.'));
            } else {
                diagramBaseFile = new File(destinationDir, relativeBaseFile);
            }
        }
        return diagramBaseFile;
    }

    /**
     * The diagram file in the specified format.
     *
     * @param format The diagram file format.
     * @return The diagram file.
     */
    private File getDiagramFile(FileFormat format) {
        File base = getDiagramBaseFile();
        return new File(base.getParent(), base.getName() + format.getFileSuffix());
    }

    public void render() {
        try {
            // 1. Render UML sources
            final String plantumlSource = renderPlantumlSource();

            // 2. Render each diagram.
            for (FileFormat format : formats) {
                renderDiagramFile(plantumlSource, format);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O error rendering " + this + ": " + ioe.getMessage(), ioe);
        }
    }

    private String renderPlantumlSource() throws IOException {
        if (config.renderPumlFile()) {
            return writePlantumlSourceToFile();
        } else {
            return super.toString();
        }
    }

    private String writePlantumlSourceToFile() throws IOException {
        File pumlFile = getPlantUmlFile();
        config.logger().info(Message.INFO_GENERATING_FILE, pumlFile);

        try (StringBufferingWriter writer = createBufferingPlantumlFileWriter(pumlFile)) {
            writeTo(IndentingPrintWriter.wrap(writer, config.indentation()));
            return writer.getBuffer().toString();
        }
    }

    private StringBufferingWriter createBufferingPlantumlFileWriter(File pumlFile) throws IOException {
        requireNonNull(pumlFile, "Plantuml File is <null>.");
        ensureParentDir(pumlFile);
        return new StringBufferingWriter(
                new OutputStreamWriter(
                        new FileOutputStream(pumlFile), config.umlCharset()));
    }

    private void renderDiagramFile(String plantumlSource, FileFormat format) throws IOException {
        final File diagramFile = getDiagramFile(format);
        config.logger().info(Message.INFO_GENERATING_FILE, diagramFile);
        ensureParentDir(diagramFile);
        try (OutputStream out = new FileOutputStream(diagramFile)) {
            Link.linkFrom(diagramFile.getParent());
            new SourceStringReader(plantumlSource).outputImage(out, new FileFormatOption(format));
        } finally {
            Link.linkFrom(null);
        }
    }

    @Override
    public String toString() {
        final String name = getDiagramBaseFile().getPath();
        if (formats.length == 1) return name + formats[0].getFileSuffix();
        return name + Stream.of(formats).map(FileFormat::getFileSuffix)
                .map(s -> s.substring(1))
                .collect(joining(",", ".[", "]"));
    }

}
