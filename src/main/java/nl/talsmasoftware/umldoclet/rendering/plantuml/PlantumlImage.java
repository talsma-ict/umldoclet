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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class PlantumlImage {

    private final String name;
    private final FileFormat fileFormat;
    private final Supplier<OutputStream> outputStreamSupplier;

    protected PlantumlImage(String name, FileFormat fileFormat, Supplier<OutputStream> outputStreamSupplier) {
        this.name = requireNonNull(name, "Image name is <null>.");
        this.fileFormat = requireNonNull(fileFormat, "File format is <null>.");
        this.outputStreamSupplier = requireNonNull(outputStreamSupplier, "Output stream supplier is <null>.");
    }

    public static Optional<PlantumlImage> fromFile(File file) {
        return fileFormatOf(file).map(format -> new PlantumlImage(file.getPath(), format, () -> createFileOutputStream(file)));
    }

    private BufferedOutputStream getBufferedOutputStream() {
        final OutputStream outputStream = outputStreamSupplier.get();
        return outputStream instanceof BufferedOutputStream
                ? (BufferedOutputStream) outputStream
                : new BufferedOutputStream(outputStream);
    }

    public String getName() {
        return name;
    }

    final void renderPlantuml(SourceStringReader plantumlSource) throws IOException {
        try (OutputStream imageOutput = getBufferedOutputStream()) {
            plantumlSource.outputImage(imageOutput, new FileFormatOption(fileFormat));
        }
    }

    @Override
    public String toString() {
        final int sep = name.lastIndexOf(File.separatorChar);
        return sep >= 0 ? name.substring(sep + 1) : name;
    }

    private static Optional<FileFormat> fileFormatOf(File file) {
        if (file == null) return Optional.empty();
        FileFormat result = null;
        final String name = file.getName().toLowerCase();
        for (FileFormat format : FileFormat.values()) {
            if (name.endsWith(format.getFileSuffix())) result = format;
        }
        return Optional.ofNullable(result);
    }

    private static OutputStream createFileOutputStream(File file) {
        try {
            return new FileOutputStream(file);
        } catch (IOException ioe) {
            throw new IllegalStateException("Could not create writer to PlantUML image: " + file, ioe);
        }
    }
}
