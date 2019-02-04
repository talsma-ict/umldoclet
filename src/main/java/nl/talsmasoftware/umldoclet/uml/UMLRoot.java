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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static java.util.Objects.requireNonNull;
import static net.sourceforge.plantuml.version.Version.versionString;
import static nl.talsmasoftware.umldoclet.logging.Message.DOCLET_UML_FOOTER;
import static nl.talsmasoftware.umldoclet.logging.Message.DOCLET_VERSION;
import static nl.talsmasoftware.umldoclet.logging.Message.INFO_GENERATING_FILE;
import static nl.talsmasoftware.umldoclet.util.FileUtils.ensureParentDir;

/**
 * Renders a new UML diagram.
 * <p>
 * Responsible for rendering the UML diagram itself:
 * The <code>{@literal @}startuml</code> and <code>{@literal @}enduml</code> lines with the children within.
 * Subclasses of {@code UMLRoot} are responsible for adding appropriate child renderers.
 * <p>
 * The diagram is rendered to a {@code .puml} output file if the {@code -createPumlFiles} option is enabled.
 *
 * @author Sjoerd Talsma
 */
public abstract class UMLRoot extends UMLNode {

    final Configuration config;

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
        writeFooterTo(output);
        output.newline().append("@enduml").newline();
        return output;
    }

    public <IPW extends IndentingPrintWriter> IPW writeFooterTo(IPW output) {
        output.indent().newline()
                .append("center footer").whitespace()
                .append(config.logger().localize(DOCLET_UML_FOOTER, DOCLET_VERSION, versionString()))
                .newline();
        return output;
    }

    /**
     * Renders this diagram to a designated {@link #pumlFile() .puml file}.
     */
    public void render() {
        if (config.renderPumlFile()) {
            final File pumlFile = pumlFile();
            final Logger logger = getConfiguration().logger();
            try (IndentingPrintWriter writer = createPlantumlWriter(pumlFile)) {
                logger.info(INFO_GENERATING_FILE, pumlFile);
                this.writeTo(IndentingPrintWriter.wrap(writer, getConfiguration().indentation()));
            }
        }
    }

    private IndentingPrintWriter createPlantumlWriter(File pumlFile) {
        try {

            Writer pumlWriter = new OutputStreamWriter(new FileOutputStream(ensureParentDir(pumlFile)), config.umlCharset());
            return IndentingPrintWriter.wrap(pumlWriter, config.indentation());

        } catch (IOException ioe) {
            throw new IllegalStateException("Could not create writer to PlantUML file: " + pumlFile, ioe);
        }
    }

}
