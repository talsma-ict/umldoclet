/*
 * Copyright (C) 2016 Talsma ICT
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
package nl.talsmasoftware.umldoclet.rendering.plantuml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import nl.talsmasoftware.umldoclet.config.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.rendering.writers.StringBufferingWriter;

import java.io.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class PlantumlImageWriter extends StringBufferingWriter {

    private final Collection<FileFormat> imageFormats;
    private final File directory;
    private final String baseName;

    public PlantumlImageWriter(UMLDocletConfig config, Writer delegate, File directory, String baseName) {
        super(delegate);
        this.imageFormats = parseFileFormats(requireNonNull(config, "Configuration is required!").imageFormats());
        this.directory = directory;
        this.baseName = baseName;
    }

    @Override
    public void close() throws IOException {
        super.close();
        for (FileFormat imageFormat : imageFormats) {
            File imageFile = new File(directory, baseName + imageFormat.getFileSuffix());
            LogSupport.info("Generating {0}...", imageFile);
            try (OutputStream pngOutput = new BufferedOutputStream(new FileOutputStream(imageFile))) {
                new SourceStringReader(getBuffer().toString()).generateImage(pngOutput, new FileFormatOption(imageFormat));
                LogSupport.debug("Finished {0}...", imageFile);
            }
        }
    }

    /**
     * Converts image format names to a set of {@link FileFormat} instances.
     * Names are used to avoid any runtime dependency from the {@link UMLDocletConfig} implementation to any
     * <code>plantuml</code> packages.
     *
     * @param imageFormatNames The names of the image formats to be generated
     *                         (e.g. <code>"PNG"</code>, <code>"SVG"</code>, etc).
     * @return The parsed <code>FileFormat</code> instances.
     */
    private static Set<FileFormat> parseFileFormats(Iterable<String> imageFormatNames) {
        Set<FileFormat> result = EnumSet.noneOf(FileFormat.class);
        if (imageFormatNames != null) {
            for (String fileFormatName : imageFormatNames) {
                fileFormatName = requireNonNull(fileFormatName, "Configured image format was null!").trim();
                if (fileFormatName.startsWith(".")) { // In case somebody mistakenly provided file extension instead.
                    fileFormatName = fileFormatName.substring(1);
                }
                for (FileFormat fileFormat : FileFormat.values()) {
                    if (fileFormatName.equalsIgnoreCase(fileFormat.name())) {
                        result.add(fileFormat);
                    }
                }
            }
        }
        return result;
    }

}
