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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import static nl.talsmasoftware.umldoclet.util.FileUtils.relativePath;

/**
 * Class for rendering links in the generated UML
 *
 * @author Sjoerd Talsma
 */
public class Link extends UMLPart {
    private static final ThreadLocal<String> LINK_FROM = new ThreadLocal<>();

    private final URI target;

    private Link(UMLPart parent, URI target) {
        super(parent);
        this.target = target;
    }

    public static Link forType(Type type) {
        final String destinationDirectory = type.getConfiguration().destinationDirectory();
        final String packageName = type.getNamespace().name;
        final String nameInPackage = type.name.qualified.startsWith(packageName + ".")
                ? type.name.qualified.substring(packageName.length() + 1) : type.name.simple;

        Optional<URI> target = relativeHtmlFile(destinationDirectory, packageName, nameInPackage)
                .or(() -> type.getConfiguration().resolveExternalLinkToType(packageName, nameInPackage));

        return new Link(type, target.orElse(null));
    }

    private static Optional<URI> relativeHtmlFile(String destinationDirectory, String packageName, String nameInPackage) {
        final String directory = destinationDirectory + "/" + packageName.replace('.', '/');
        return Optional.of(new File(directory, nameInPackage + ".html"))
                .filter(File::isFile)
                .map(File::toURI);
    }

    /**
     * Sets the base path where relative links should be rendered from.
     * <p>
     * This setting is configured on a per-thread basis.
     *
     * @param basePath The base path to define relative links from.
     */
    public static void linkFrom(String basePath) {
        if (basePath == null) LINK_FROM.remove();
        else LINK_FROM.set(basePath);
    }

    private Optional<Namespace> diagramPackage() {
        UMLRoot diagram = getRootUMLPart();
        if (diagram instanceof PackageUml) {
            return Optional.of(new Namespace(diagram, ((PackageUml) diagram).packageName));
        } else if (diagram instanceof ClassUml) {
            return Optional.of(((ClassUml) diagram).type.getNamespace());
        }
        return Optional.empty();
    }

    private Optional<File> linkFromDir() {
        final File fromDir = new File(
                Optional.ofNullable(LINK_FROM.get())
                        .or(() -> diagramPackage()
                                .map(namespace -> namespace.name)
                                .map(packageName -> packageName.replace('.', '/'))
                                .map(packageDir -> getRootUMLPart().config.destinationDirectory() + "/" + packageDir))
                        .orElseGet(() -> getRootUMLPart().config.destinationDirectory()));
        return fromDir.isDirectory() ? Optional.of(fromDir) : Optional.empty();
    }

    private Optional<String> relativeTarget() {
        return Optional.ofNullable(target)
                .filter(uri -> "file".equals(uri.getScheme())).map(File::new)
                .flatMap(targetFile -> linkFromDir().map(dir -> relativePath(dir, targetFile)));
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (target != null) {
            output.append("[[").append(relativeTarget().orElseGet(target::toASCIIString)).append("]]");
        }
        return output;
    }

}
