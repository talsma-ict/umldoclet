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

import net.sourceforge.plantuml.SourceStringReader;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.writers.StringBufferingWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
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
    private final Collection<PlantumlImage> images;

    private PlantumlImageWriter(Logger logger, Writer plantumlWriter, Iterable<PlantumlImage> images) {
        super(plantumlWriter);
        this.logger = logger;
        this.images = unmodifiableCopyOf(images);
    }

    public static PlantumlImageWriter create(Logger logger, File plantumlFile, File... imageFiles) {
        requireNonNull(logger, "Logger is <null>.");
        requireNonNull(plantumlFile, "PlantUML file is <null>.");
        try {
            // TODO Use umlEncoding
            return new PlantumlImageWriter(logger, new FileWriter(plantumlFile), Stream.of(imageFiles)
                    .map(file -> fileToImage(logger, file))
                    .filter(Optional::isPresent).map(Optional::get)
                    .collect(Collectors.toList()));
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
        if (!images.isEmpty()) {
            SourceStringReader sourceStringReader = new SourceStringReader(getBuffer().toString());
            for (PlantumlImage image : images) {
                logger.info(INFO_GENERATING_FILE, image.getName());
                image.renderPlantuml(sourceStringReader);
            }
        }
    }

    /**
     * @return String representation providing information about which image file(s) will be generated.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + images;
    }

    private static Optional<PlantumlImage> fileToImage(Logger logger, File file) {
        Optional<PlantumlImage> plantumlImage = PlantumlImage.fromFile(file);
        if (!plantumlImage.isPresent() && !file.getName().endsWith(".none")) {
            logger.warn(WARNING_UNRECOGNIZED_IMAGE_FORMAT, file.getName());
        }
        return plantumlImage;
    }

    private static <T> Collection<T> unmodifiableCopyOf(Iterable<? extends T> iterable) {
        final ArrayList<T> copy = new ArrayList<>();
        if (iterable != null) iterable.forEach(item -> {
            if (item != null) copy.add(item);
        });
        switch (copy.size()) {
            case 0:
                return emptyList();
            case 1:
                return singletonList(copy.get(0));
            default:
                copy.trimToSize();
                return unmodifiableList(copy);
        }
    }
}
