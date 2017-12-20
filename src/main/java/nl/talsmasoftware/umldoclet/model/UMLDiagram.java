/*
 * Copyright 2016-2017 Talsma ICT
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

import jdk.javadoc.doclet.DocletEnvironment;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.rendering.plantuml.PlantumlImageWriter;

import java.io.*;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.logging.Message.ERROR_COULDNT_RENDER_UML;

/**
 * Renders a new UML diagram.
 * <p>
 * Responsible for rendering the UML diagram itself:
 * The <code>{@literal @}startuml</code> and <code>{@literal @}enduml</code> lines with the children within.
 * Subclasses of {@code UMLDiagram} are responsible for adding appropriate child renderers.
 *
 * @author Sjoerd Talsma
 */
public abstract class UMLDiagram extends Renderer {

    protected final Configuration config;
    protected final DocletEnvironment env;

    public UMLDiagram(Configuration config, DocletEnvironment env) {
        super(null);
        this.config = requireNonNull(config, "No UML Doclet configuration provided.");
        this.env = requireNonNull(env, "Doclet environment is <null>.");
    }

    /**
     * This method determines the physical file where the plantuml diagram should be rendered.
     *
     * @return The physical file where this plantuml diagram in question should be rendered.
     */
    protected abstract File pumlFile();

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        out.append("@startuml").newline().newline();
        writeChildrenTo(out);
        return out.append("@enduml").newline();
    }

    /**
     * Renders this diagram to a designated {@link #pumlFile() .puml file}.
     *
     * @return Whether the rendering succeeded.
     */
    public boolean render() {
        final File pumlFile = pumlFile();
        final String baseName = baseName(pumlFile);
        // TODO Make these configurable:
        final File imgdir = ensureParentDir(pumlFile).getParentFile();
        final String[] imgFormats = new String[]{"svg", "png"};

        try (Writer writer = new PlantumlImageWriter(
                new OutputStreamWriter(new FileOutputStream(pumlFile)), config, imgdir, baseName, imgFormats)) {
            config.info(Message.INFO_GENERATING_FILE, pumlFile);
            this.writeTo(IndentingPrintWriter.wrap(writer, config.indentation));
            return true;
        } catch (IOException | RuntimeException e) {
            config.error(ERROR_COULDNT_RENDER_UML, pumlFile, e);
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

}
