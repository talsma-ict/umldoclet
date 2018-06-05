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
package nl.talsmasoftware.umldoclet.rendering.plantuml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.writers.StringBufferingWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.logging.Message.INFO_GENERATING_FILE;
import static nl.talsmasoftware.umldoclet.logging.Message.WARNING_UNRECOGNIZED_IMAGE_FORMAT;

/**
 * Writer that delegates to a regular writer for the UML itself, but when finished (i.e. when close is called), also
 * attempt to generate image files using the PlantUML library.
 *
 * @author Sjoerd Talsma
 */
public class PlantumlImageWriter extends StringBufferingWriter {

    private final Logger logger;
    private final EnumMap<FileFormat, File> images = new EnumMap<>(FileFormat.class);

    public PlantumlImageWriter(Logger logger, File plantumlFile, File... imageFiles) {
        super(plantumlWriter(plantumlFile));
        this.logger = requireNonNull(logger, "Logger is <null>.");
        for (File imageFile : requireNonNull(imageFiles, "Image files are <null>.")) {
            fileFormatOf(imageFile).ifPresent(format -> images.put(format, imageFile));
        }
    }

    private static Writer plantumlWriter(File plantumlFile) {
        try {
            return new FileWriter(requireNonNull(plantumlFile, "PlantUML file is <null>."));
        } catch (IOException ioe) {
            throw new IllegalStateException("Could not create writer to PlantUML file: " + plantumlFile, ioe);
        }
    }

    /**
     * Closes the delegate writer and tries to generate an image file for each configured image format.
     * The default file extension from the image format is used, together with the specified <code>directory</code>
     * and <code>baseName</code>.
     *
     * @throws IOException In case of I/O errors while closing the delegate writer or writing to an image file.
     */
    @Override
    public void close() throws IOException {
        super.close();
        for (Map.Entry<FileFormat, File> image : images.entrySet()) {
            logger.info(INFO_GENERATING_FILE, image.getValue());
            try (OutputStream imageOutput = new BufferedOutputStream(new FileOutputStream(image.getValue()))) {
                new SourceStringReader(getBuffer().toString()).outputImage(imageOutput, new FileFormatOption(image.getKey()));
            }
        }
    }

    private Optional<FileFormat> fileFormatOf(File file) {
        if (file == null) return Optional.empty();
        FileFormat result = null;
        final String name = file.getName().toLowerCase();
        for (FileFormat format : FileFormat.values()) {
            if (name.endsWith(format.getFileSuffix())) result = format;
        }
        if (result == null) logger.warn(WARNING_UNRECOGNIZED_IMAGE_FORMAT, name);
        return Optional.ofNullable(result);
    }

    /**
     * @return String representation providing information about which image file(s) will be generated.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()
                + images.values().stream().map(File::getName).collect(joining(", ", "[", "]"));
    }

}
