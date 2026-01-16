/*
 * Copyright 2016-2026 Talsma ICT
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
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;
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
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableCollection;
import static nl.talsmasoftware.umldoclet.configuration.ImageConfig.Format.SVG;

/// Collects all generated diagram files from the output directory.
///
/// @author Sjoerd Talsma
final class DiagramCollector extends SimpleFileVisitor<Path> {
    private static final Pattern PACKAGE_DIAGRAM_PATTERN = Pattern.compile("package\\.[a-z]+$");
    private static final Pattern PACKAGE_DEPENDENCY_DIAGRAM_PATTERN = Pattern.compile("package-dependencies\\.[a-z]+$");

    private final File basedir;
    private final Optional<File> imagesDirectory;
    private final Optional<ImageConfig.Format> imageFormat;
    private final ThreadLocal<Collection<DiagramFile>> collected = ThreadLocal.withInitial(ArrayList::new);

    DiagramCollector(Configuration config) {
        this.basedir = new File(config.destinationDirectory());
        this.imageFormat = config.images().formats().stream().findFirst();
        this.imagesDirectory = config.images().directory()
                .map(imagesDir -> new File(config.destinationDirectory(), imagesDir));
    }

    /// Collects all generated diagram files by walking the specified path.
    ///
    /// @return The collected diagrams
    /// @throws IOException In case there were I/O errors walking the path
    Collection<DiagramFile> collectDiagrams() throws IOException {
        if (!imageFormat.isPresent()) return Collections.emptySet();
        try {
            Files.walkFileTree(imagesDirectory.orElse(basedir).toPath(), this);
            return unmodifiableCollection(collected.get());
        } finally {
            collected.remove();
        }
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile() && FileUtils.hasExtension(path, imageFormat.orElse(SVG).fileExtension)) {
            collected.get().add(createDiagramInstance(path));
        }
        return super.visitFile(path, attrs);
    }

    private boolean isPackageDiagram(File diagramFile) {
        return PACKAGE_DIAGRAM_PATTERN.matcher(diagramFile.getName()).find();
    }

    private boolean isPackageDependencyDiagram(File diagramFile) {
        return PACKAGE_DEPENDENCY_DIAGRAM_PATTERN.matcher(diagramFile.getName()).find();
    }

    private DiagramFile createDiagramInstance(Path diagramPath) {
        final File diagramFile = diagramPath.normalize().toFile();
        final ImageConfig.Format format = imageFormat.orElse(SVG);
        if (isPackageDiagram(diagramFile)) {
            return new PackageDiagramInserter(basedir, diagramFile, format, imagesDirectory.isPresent());
        } else if (isPackageDependencyDiagram(diagramFile)) {
            return new PackageDependenciesInserter(basedir, diagramFile, format);
        }
        return new ClassDiagramInserter(basedir, diagramFile, format, imagesDirectory.isPresent());
    }

}
