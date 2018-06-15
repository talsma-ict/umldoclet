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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Collects all generated diagram files from the output directory.
 *
 * @author Sjoerd Talsma
 */
final class DiagramCollector implements Predicate<Path> {
    private final Path basedir;
    private final Collection<String> diagramExtensions;
    private final Collection<Diagram> collected = new ArrayList<>();

    DiagramCollector(Configuration config) {
        this.basedir = new File(config.destinationDirectory()).toPath();
        this.diagramExtensions = unmodifiableList(config.images().formats().stream()
                .map(String::toLowerCase)
                .map(format -> format.startsWith(".") ? format : "." + format)
                .collect(toList()));
    }

    /**
     * Collects all generated diagram files by walking the specified path.
     *
     * @param path The path to collect generated diagrams from
     * @return The collected diagrams
     * @throws IOException In case there were I/O errors walking the path
     */
    Collection<Diagram> collect(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile() && test(file)) collected.add(new Diagram(basedir, file));
                return super.visitFile(file, attrs);
            }
        });
        return unmodifiableCollection(collected);
    }

    @Override
    public boolean test(Path path) {
        return diagramExtensions.stream().anyMatch(extension -> path.toString().toLowerCase().endsWith(extension));
    }
}
