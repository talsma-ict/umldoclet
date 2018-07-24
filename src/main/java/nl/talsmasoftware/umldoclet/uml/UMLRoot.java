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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.plantuml.PlantumlImageWriter;
import nl.talsmasoftware.umldoclet.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.logging.Message.ERROR_COULDNT_RENDER_UML;
import static nl.talsmasoftware.umldoclet.logging.Message.INFO_GENERATING_FILE;
import static nl.talsmasoftware.umldoclet.util.FileUtils.ensureParentDir;

/**
 * Renders a new UML diagram.
 * <p>
 * Responsible for rendering the UML diagram itself:
 * The <code>{@literal @}startuml</code> and <code>{@literal @}enduml</code> lines with the children within.
 * Subclasses of {@code UMLRoot} are responsible for adding appropriate child renderers.
 * <p>
 * The diagram is rendered to a {@code .puml} output file.
 * Writing happens to the {@link PlantumlImageWriter} which caches the written plantuml file and
 * will generate one or more corresponding images from the diagram when the writer is closed.
 *
 * @author Sjoerd Talsma
 */
public abstract class UMLRoot extends UMLPart {

    final Configuration config;
    protected final List<UMLPart> children = new ArrayList<>();

    protected UMLRoot(Configuration config) {
        super(null);
        this.config = requireNonNull(config, "Configuration is <null>.");
    }

    @Override
    protected UMLRoot getRootUMLPart() {
        return this;
    }

    /**
     * This method determines the physical file where the plantuml diagram should be rendered.
     *
     * @return The physical file where the plantuml should be rendered.
     */
    public abstract File pumlFile();

    @Override
    public Collection<? extends UMLPart> getChildren() {
        return children;
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    protected Indentation getIndentation() {
        return config.indentation();
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append("@startuml").newline().newline();
        writeChildrenTo(output);
        output.newline().append("@enduml").newline();
        return output;
    }

    /**
     * Renders this diagram to a designated {@link #pumlFile() .puml file}.
     */
    public void render() {
        final File pumlFile = pumlFile();
        final Logger logger = getConfiguration().logger();
        try (IndentingPrintWriter writer = createPlantumlWriter(pumlFile)) {
            logger.info(INFO_GENERATING_FILE, pumlFile);
            this.writeTo(IndentingPrintWriter.wrap(writer, getConfiguration().indentation()));
        } catch (RuntimeException rte) {
            logger.error(ERROR_COULDNT_RENDER_UML, pumlFile, rte);
            throw rte;
        }
    }

    private Optional<File> configuredImageDirectory() {
        return config.images().directory().map(imageDir -> {
            final String baseDir = config.destinationDirectory();
            final File imgDir = new File(imageDir);
            return baseDir.isEmpty() || imgDir.isAbsolute() ? imgDir : new File(baseDir, imageDir);
        });
    }

    /**
     * Returns the name for the image, without the file extension.
     * <p>
     * This method also takes the {@link #configuredImageDirectory()} into consideration.
     *
     * @param file The file to return the base filename for.
     * @return The filename to use for images, without the file extension.
     */
    private String imageBasename(File file) {
        String baseName = file.getName();
        int dotIdx = baseName.lastIndexOf('.');
        if (dotIdx > 0) baseName = baseName.substring(0, dotIdx);
        if (configuredImageDirectory().isPresent()) {
            String relativeDir = FileUtils.relativePath(new File(config.destinationDirectory()), file.getParentFile());
            if (!relativeDir.isEmpty()) baseName = relativeDir.replace('/', '.') + '.' + baseName;
        }
        return baseName;
    }

    private IndentingPrintWriter createPlantumlWriter(File pumlFile) {
//        final File imageDir = configuredImageDirectory().orElseGet(pumlFile::getParentFile);
//        final String baseName = imageBasename(pumlFile);

//        FileUtils.ensureParentDir(new File(imageDir, baseName));

//        File[] imageFiles = config.images().formats().stream()
//                .map(String::toLowerCase)
//                .map(format -> new File(imageDir, baseName + "." + format))
//                .toArray(File[]::new);

        try {
//            return IndentingPrintWriter.wrap(PlantumlImageWriter.create(config, pumlFile, imageFiles), config.indentation());
            Writer pumlWriter = new OutputStreamWriter(new FileOutputStream(ensureParentDir(pumlFile)), config.umlCharset());
            return IndentingPrintWriter.wrap(pumlWriter, config.indentation());
        } catch (IOException ioe) {
            throw new IllegalStateException("Could not create writer to PlantUML file: " + pumlFile, ioe);
        }
    }

}
