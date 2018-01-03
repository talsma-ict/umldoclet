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
package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.Renderer;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingChildRenderer;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.plantuml.PlantumlImageWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static nl.talsmasoftware.umldoclet.logging.Message.ERROR_COULDNT_RENDER_UML;
import static nl.talsmasoftware.umldoclet.logging.Message.INFO_GENERATING_FILE;

/**
 * Renders a new UML diagram.
 * <p>
 * Responsible for rendering the UML diagram itself:
 * The <code>{@literal @}startuml</code> and <code>{@literal @}enduml</code> lines with the children within.
 * Subclasses of {@code UMLDiagram} are responsible for adding appropriate child renderers.
 * <p>
 * The diagram is rendered to a {@code .puml} output file.
 * Writing happens to the {@link PlantumlImageWriter} which caches the written plantuml file and
 * will generate one or more corresponding images from the diagram when the writer is closed.
 *
 * @author Sjoerd Talsma
 */
public abstract class UMLDiagram extends UMLRenderer implements IndentingChildRenderer {

    //    protected final Configuration config;
    //    protected final DocletEnvironment env;
    protected final List<Renderer> children = new ArrayList<>();

    protected UMLDiagram(Configuration config) {
        super(config);
    }

    /**
     * This method determines the physical file where the plantuml diagram should be rendered.
     *
     * @return The physical file where this plantuml diagram in question should be rendered.
     */
    protected abstract File pumlFile();

    @Override
    public Collection<Renderer> getChildren() {
        return children;
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
     *
     * @return Whether the rendering succeeded.
     */
    public boolean render() {
        final File pumlFile = pumlFile();

        try (Writer writer = createPlantumlWriter(pumlFile)) {
            config.getLogger().info(INFO_GENERATING_FILE, pumlFile);
            this.writeTo(IndentingPrintWriter.wrap(writer, config.getIndentation()));
            return true;
        } catch (IOException | RuntimeException e) {
            config.getLogger().error(ERROR_COULDNT_RENDER_UML, pumlFile, e);
            return false;
        }
    }

    /**
     * Ensure the parent directory exists by attempting to create it if it doensn't yet exist.
     *
     * @param file The file verify directory existence for.
     * @return The specified file.
     */
    protected static File ensureParentDir(File file) {
        if (file != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IllegalStateException("Can't create directory \"" + file.getParent() + "\".");
        }
        return file;
    }

    private static String baseName(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(0, lastDot) : name;
    }

    private Writer createPlantumlWriter(File pumlFile) throws IOException {
        // TODO Make these configurable:
        final File imgdir = ensureParentDir(pumlFile).getParentFile();
        final String baseName = baseName(pumlFile);
        final String[] imgFormats = new String[]{"svg", "png"};

        return new PlantumlImageWriter(
                new OutputStreamWriter(new FileOutputStream(pumlFile)),
                config.getLogger(), imgdir, baseName, imgFormats);
    }

}
