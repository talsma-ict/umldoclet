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
package nl.talsmasoftware.umldoclet.html;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Collects all generated diagram files from the output directory.
 *
 * @author Sjoerd Talsma
 */
final class DiagramCollector extends SimpleFileVisitor<Path> {
    private final Path basedir;
    private final Optional<Path> imagesDirectory;
    private final List<String> diagramExtensions;
    private final ThreadLocal<Collection<UmlDiagram>> collected = ThreadLocal.withInitial(ArrayList::new);

    DiagramCollector(Configuration config) {
        this.basedir = new File(config.destinationDirectory()).toPath();
        this.diagramExtensions = unmodifiableList(config.images().formats().stream()
                .map(String::toLowerCase)
                .map(format -> format.startsWith(".") ? format : "." + format)
                .collect(toList()));
        this.imagesDirectory = config.images().directory()
                .map(imagesDir -> new File(config.destinationDirectory(), imagesDir))
                .map(File::toPath);
    }

    /**
     * Collects all generated diagram files by walking the specified path.
     *
     * @return The collected diagrams
     * @throws IOException In case there were I/O errors walking the path
     */
    Collection<UmlDiagram> collectDiagrams() throws IOException {
        if (diagramExtensions.isEmpty()) return Collections.emptySet();
        try {
            Files.walkFileTree(imagesDirectory.orElse(basedir), this);
            return unmodifiableCollection(collected.get());
        } finally {
            collected.remove();
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile() && FileUtils.hasExtension(file, diagramExtensions.get(0))) {
            createDiagramInstance(file).ifPresent(collected.get()::add);
        }
        return super.visitFile(file, attrs);
    }

    private Optional<UmlDiagram> createDiagramInstance(Path diagramFile) {
        if (diagramFile.getFileName().toString().startsWith("package.")) {
            return Optional.empty(); // TODO implement package diagram HTML inclusion
        }
        return Optional.of(new UmlClassDiagram(basedir, imagesDirectory, diagramFile));
    }

}
