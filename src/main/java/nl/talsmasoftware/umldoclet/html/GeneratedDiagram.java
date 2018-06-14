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

import nl.talsmasoftware.umldoclet.uml.configuration.Configuration;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;

final class GeneratedDiagram {

    private final Path path;

    GeneratedDiagram(Path path) {
        this.path = requireNonNull(path, "Diagram file is <null>.");
    }

    static class Visitor extends SimpleFileVisitor<Path> implements Predicate<Path> {
        private final Configuration config;
        private final Collection<GeneratedDiagram> _collected = new ArrayList<>();
        final Collection<GeneratedDiagram> collected = unmodifiableCollection(_collected);

        Visitor(Configuration config) {
            this.config = requireNonNull(config, "Configuration is <null>.");
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (attrs.isRegularFile() && test(file)) _collected.add(new GeneratedDiagram(file));
            return super.visitFile(file, attrs);
        }

        @Override
        public boolean test(Path path) {
            return false;
        }
    }

}
