/*
 * Copyright 2016-2026 Talsma ICT
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
import net.sourceforge.plantuml.version.Version;
import nl.talsmasoftware.indentation.io.IndentingWriter;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.rendering.writers.StringBufferingWriter;
import nl.talsmasoftware.umldoclet.uml.plantuml.PlantumlGenerator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.util.FileUtils.ensureParentDir;
import static nl.talsmasoftware.umldoclet.util.FileUtils.relativePath;
import static nl.talsmasoftware.umldoclet.util.FileUtils.withoutExtension;

/// Base class for all diagrams.
///
/// A diagram is a [UMLNode] that can be rendered to a PlantUML source file and then to a diagram image.
///
/// @author Sjoerd Talsma
public abstract class Diagram extends UMLNode {

    private final Configuration config;
    private final PlantumlGenerator plantumlGenerator;
    private final FileFormat[] formats;
    private File diagramBaseFile;

    /// Creates a new diagram.
    ///
    /// @param config The configuration to use.
    protected Diagram(Configuration config) {
        super(null);
        this.config = requireNonNull(config, "Configuration is <null>");
        this.plantumlGenerator = PlantumlGenerator.getPlantumlGenerator(config);
        this.formats = config.images().formats().stream()
                .map(this::toFileFormat).filter(Objects::nonNull)
                .toArray(FileFormat[]::new);
    }

    @Override
    public IndentingWriter writeTo(IndentingWriter output) {
        try {
            IndentingWriter indented = output.writeln("@startuml").indent();
            writeCopyrightStatement(indented);
            writeCustomDirectives(config.customPlantumlDirectives(), indented);
            writeChildrenTo(indented);
            writeFooterTo(indented);
            return indented.unindent().writeln("@enduml");
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    /// Writes custom directives to the diagram.
    ///
    /// @param customDirectives The custom directives to write.
    /// @param output           The output to write to.
    /// @return The same output instance for method chaining.
    protected IndentingWriter writeCustomDirectives(List<String> customDirectives, IndentingWriter output) throws IOException {
        for (String customDirective : customDirectives) {
            output.writeln(customDirective);
        }
        if (!customDirectives.isEmpty()) {
            output.writeln("");
        }
        return output;
    }

    /// Writes the footer to the diagram.
    ///
    /// @param output The output to write to.
    /// @return The same output instance for method chaining.
    private IndentingWriter writeFooterTo(IndentingWriter output) throws IOException {
        String footerText = config.logger().localize(Message.DOCLET_UML_FOOTER, Message.DOCLET_VERSION);
        String footerLink = "https://github.com/talsma-ict/umldoclet";
        output.writeln("", "<style>").indent()
                .writeln("footer {").indent()
                .writeln("HyperLinkColor #8", // default footer FontColor
                        "HyperLinkUnderlineThickness 0")
                .unindent().writeln("}")
                .unindent().writeln("</style>")
                .writeln(String.format("footer \\n[[%s %s]]", footerLink, footerText),
                        "' Generated " + ZonedDateTime.now());
        return output;
    }

    /// @return The configuration used by this diagram.
    @Override
    public Configuration getConfiguration() {
        return config;
    }

    /// Determine the physical file location for the plantuml output.
    ///
    /// This will even be called if `-createPumlFiles` is not enabled,
    /// to determine the `diagram base file`.
    ///
    /// @return The physical file for the plantuml output.
    protected abstract File getPlantUmlFile();

    /// @return The diagram file without extension.
    /// @see #getDiagramFile(FileFormat)
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

    /// The diagram file in the specified format.
    ///
    /// @param format The diagram file format.
    /// @return The diagram file.
    private File getDiagramFile(FileFormat format) {
        File base = getDiagramBaseFile();
        return new File(base.getParent(), base.getName() + format.getFileSuffix());
    }

    /// Renders this diagram to the configured output formats.
    public void render() {
        // Skips rendering empty diagrams per configuration
        if (this.isEmpty() && !config.renderEmptyDiagrams()) {
            if (formats.length > 0) {
                config.logger().debug("Skipping empty diagram: {0}", getDiagramFile(formats[0]));
            }
            return;
        }

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

    /// Renders the PlantUML source for this diagram.
    ///
    /// @return The PlantUML source code.
    /// @throws IOException If an I/O error occurs.
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
            writeTo(new IndentingWriter(writer, config.indentation()));
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

    /// Static utility method to convert an image format to PlantUML {@linkplain FileFormat} with the same name.
    ///
    /// @param format The image format to convert into PlantUML fileformat.
    /// @return The PlantUML file format.
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

    private void writeCopyrightStatement(IndentingWriter output) throws IOException {
        String version = Message.DOCLET_VERSION.toString(Locale.ENGLISH);
        final int year = LocalDate.now().getYear();
        output.writeln("' Copyright to this UML and generated images belongs to the author of the corresponding Java sources.",
                "",
                "' This UML was generated by UMLDoclet (C) Copyright 2016-" + year + " Talsma ICT.",
                "' UMLDoclet " + version + " is licensed under the Apache License, version 2.0",
                "' and contains parts of PlantUML " + Version.versionString() + " (ASL) Copyright 2009-" + year + ", Arnaud Roques.",
                "");
    }
}
