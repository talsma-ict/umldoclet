/*
 * Copyright 2016-2024 Talsma ICT
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
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.writers.StringBufferingWriter;
import nl.talsmasoftware.umldoclet.uml.plantuml.PlantumlGenerator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.util.FileUtils.ensureParentDir;
import static nl.talsmasoftware.umldoclet.util.FileUtils.relativePath;
import static nl.talsmasoftware.umldoclet.util.FileUtils.withoutExtension;

/**
 * Abstract UML Diagram class.
 */
public abstract class Diagram extends UMLNode {

    private final Configuration config;
    private final PlantumlGenerator plantumlGenerator;
    private final FileFormat[] formats;
    private File diagramBaseFile;

    protected Diagram(Configuration config) {
        super(null);
        this.config = requireNonNull(config, "Configuration is <null>");
        this.plantumlGenerator = PlantumlGenerator.getPlantumlGenerator(config);
        this.formats = config.images().formats().stream()
                .map(this::toFileFormat).filter(Objects::nonNull)
                .toArray(FileFormat[]::new);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append("@startuml").newline();
        IndentingPrintWriter indented = output.indent();
        writeCustomDirectives(config.customPlantumlDirectives(), indented);
        writeChildrenTo(indented);
        writeFooterTo(indented);
        output.append("@enduml").newline();
        return output;
    }

    protected <IPW extends IndentingPrintWriter> IPW writeCustomDirectives(List<String> customDirectives, IPW output) {
        customDirectives.forEach(output::println);
        if (!customDirectives.isEmpty()) {
            output.newline();
        }
        return output;
    }

    private <IPW extends IndentingPrintWriter> IPW writeFooterTo(IPW output) {
        output.append("center footer").whitespace().append("\\n")
                .append(config.logger().localize(Message.DOCLET_UML_FOOTER, Message.DOCLET_VERSION))
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
     * to determine the {@code diagram base file}.
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
            String plantumlSource = renderPlantumlSource();
            if (Link.linkFrom(getDiagramBaseFile().getParent()) || plantumlSource == null) {
                plantumlSource = super.toString(); // Must re-render in case of different link base paths.
            }

            // 2. Render each diagram.
            for (FileFormat format : formats) {
                renderDiagramFile(plantumlSource, format);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O error rendering " + this + ": " + ioe.getMessage(), ioe);
        } finally {
            Link.linkFrom(null);
        }
    }

    private String renderPlantumlSource() throws IOException {
        if (config.renderPumlFile()) {
            return writePlantumlSourceToFile();
        } else {
            return null;
        }
    }

    private String writePlantumlSourceToFile() throws IOException {
        File pumlFile = getPlantUmlFile();
        config.logger().info(Message.INFO_GENERATING_FILE, pumlFile);

        ensureParentDir(pumlFile);
        Link.linkFrom(pumlFile.getParent());
        try (StringBufferingWriter writer = createBufferingPlantumlFileWriter(pumlFile)) {
            writeTo(IndentingPrintWriter.wrap(writer, config.indentation()));
            return writer.getBuffer().toString();
        }
    }

    private StringBufferingWriter createBufferingPlantumlFileWriter(File pumlFile) throws IOException {
        return new StringBufferingWriter(
                new OutputStreamWriter(
                        Files.newOutputStream(pumlFile.toPath()), config.umlCharset()));
    }

    private void renderDiagramFile(String plantumlSource, FileFormat format) throws IOException {
        final File diagramFile = getDiagramFile(format);
        config.logger().info(Message.INFO_GENERATING_FILE, diagramFile);
        ensureParentDir(diagramFile);
        try (OutputStream out = Files.newOutputStream(diagramFile.toPath())) {
            plantumlGenerator.generatePlantumlDiagramFromSource(plantumlSource, format, out);
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

    /**
     * Static utility method to convert an image format to PlantUML {@linkplain FileFormat} with the same name.
     *
     * @param format The image format to convert into PlantUML fileformat.
     * @return The PlantUML file format.
     */
    private FileFormat toFileFormat(ImageConfig.Format format) {
        try {
            switch (format) {
                case SVG:
                case SVG_IMG:
                    return FileFormat.SVG;
                default:
                    return FileFormat.valueOf(format.name());
            }
        } catch (RuntimeException incompatibleFormatOrNull) {
            config.logger().debug(Message.WARNING_UNRECOGNIZED_IMAGE_FORMAT, format);
        }
        return null;
    }

}
