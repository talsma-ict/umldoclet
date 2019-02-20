/*
 * Copyright 2016-2019 Talsma ICT
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

import nl.talsmasoftware.umldoclet.config.UMLDocletConfig;

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
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableCollection;

/**
 * Collects all generated diagram files from the output directory.
 *
 * @author Sjoerd Talsma
 */
final class DiagramCollector extends SimpleFileVisitor<Path> {
    private static final Pattern PACKAGE_DIAGRAM_PATTERN = Pattern.compile("package.[a-z]+$");

    private final File basedir;
    private final File imagesDirectory;
    private final List<String> diagramExtensions = new ArrayList<>();
    private final ThreadLocal<Collection<UmlDiagram>> collected = new ThreadLocal<Collection<UmlDiagram>>() {
        @Override
        protected Collection<UmlDiagram> initialValue() {
            return new ArrayList<>();
        }
    };

    DiagramCollector(UMLDocletConfig config) {
        this.basedir = new File(config.htmlDestinationDirectory());
        for (String imageFormat : config.imageFormats()) {
            imageFormat = imageFormat.toLowerCase(Locale.ENGLISH);
            if (!imageFormat.startsWith(".")) imageFormat = "." + imageFormat;
            diagramExtensions.add(imageFormat);
        }
        this.imagesDirectory = config.imageDirectory() == null ? null : new File(config.basePath(), config.imageDirectory());
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
            File dir = imagesDirectory == null ? basedir : imagesDirectory;
            Files.walkFileTree(dir.toPath(), this);
            return unmodifiableCollection(collected.get());
        } finally {
            collected.remove();
        }
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile() && hasExtension(path, diagramExtensions.get(0))) {
            UmlDiagram diagram = createDiagramInstance(path);
            if (diagram != null) collected.get().add(diagram);
        }
        return super.visitFile(path, attrs);
    }

    private boolean isPackageDiagram(File diagramFile) {
        return PACKAGE_DIAGRAM_PATTERN.matcher(diagramFile.getName()).find();
    }

    private UmlDiagram createDiagramInstance(Path diagramPath) {
        File diagramFile = diagramPath.normalize().toFile();
        if (isPackageDiagram(diagramFile)) {
            return new UmlPackageDiagram(basedir, diagramFile, imagesDirectory != null);
        }
        return new UmlClassDiagram(basedir, diagramFile, imagesDirectory != null);
    }

    private static boolean hasExtension(Object file, String extension) {
        if (file == null || extension == null) return false;
        if (!extension.startsWith(".")) extension = '.' + extension;
        return file.toString().toLowerCase().endsWith(extension.toLowerCase());
    }

}
