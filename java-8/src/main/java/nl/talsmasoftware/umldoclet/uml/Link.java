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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import static nl.talsmasoftware.umldoclet.util.FileUtils.relativePath;

/**
 * Class for rendering links in the generated UML
 *
 * @author Sjoerd Talsma
 */
public class Link extends UMLNode {
    private static final ThreadLocal<String> LINK_FROM = new ThreadLocal<>();

    private final URI target;

    private Link(UMLNode parent, URI target) {
        super(parent);
        this.target = target;
    }

    public static Link forType(Type type) {
        final String destinationDirectory = type.getConfiguration().destinationDirectory();
        final String packageName = type.getPackagename();
        final String nameInPackage = type.getName().qualified.startsWith(packageName + ".")
                ? type.getName().qualified.substring(packageName.length() + 1) : type.getName().simple;

        URI target = relativeHtmlFile(destinationDirectory, packageName, nameInPackage);
        if (target == null) {
            target = type.getConfiguration().resolveExternalLinkToType(packageName, nameInPackage).orElse(null);
        }
        return new Link(type, target);
    }

    public static Link forPackage(Namespace namespace) {
        final String destinationDirectory = namespace.getConfiguration().destinationDirectory();
        final String packageName = namespace.name;
        final String nameInPackage = "package-summary";

        URI target = relativeHtmlFile(destinationDirectory, packageName, nameInPackage);
        if (target == null) {
            target = namespace.getConfiguration().resolveExternalLinkToType(packageName, nameInPackage).orElse(null);
        }
        return new Link(namespace, target);
    }

    private static URI relativeHtmlFile(String destinationDirectory, String packageName, String nameInPackage) {
        final String directory = destinationDirectory + "/" + packageName.replace('.', '/');
        File relativeHtmlFile = new File(directory, nameInPackage + ".html");
        return relativeHtmlFile.isFile() ? relativeHtmlFile.toURI() : null;
    }

    /**
     * Sets the base path where relative links should be rendered from.
     * <p>
     * This setting is configured on a per-thread basis.
     *
     * @param basePath The base path to define relative links from.
     * @return whether the base path was modified or not
     */
    public static boolean linkFrom(String basePath) {
        if (Objects.equals(basePath, LINK_FROM.get())) return false;
        if (basePath == null) LINK_FROM.remove();
        else LINK_FROM.set(basePath);
        return true;
    }

    private Optional<File> linkFromDir() {
        String dir = LINK_FROM.get();
        if (dir == null) dir = getConfiguration().destinationDirectory();
        final File fromDir = new File(dir);
        return fromDir.isDirectory() ? Optional.of(fromDir) : Optional.empty();
    }

    private Optional<String> relativeTarget() {
        return Optional.ofNullable(target)
                .filter(uri -> "file".equals(uri.getScheme())).map(File::new)
                .flatMap(targetFile -> linkFromDir().map(dir -> relativePath(dir, targetFile)));
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        if (target != null) {
            output.append("[[").append(relativeTarget().orElseGet(target::toASCIIString)).append("]]");
        }
        return output;
    }

}
