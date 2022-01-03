/*
 * Copyright 2016-2022 Talsma ICT
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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.uml.Reference.Side;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * UML diagram representing the dependencies between the documented Java packages.
 */
public class DependencyDiagram extends Diagram {

    private String pumlFileName;
    private File pumlFile = null;

    public DependencyDiagram(Configuration config, String pumlFileName) {
        super(config);
        this.pumlFileName = pumlFileName;
    }

    @Override
    public List<UMLNode> getChildren() {
        List<UMLNode> children = super.getChildren();
        List<UMLNode> exclusionFiltered = children.stream().filter(this::isIncludedChild).collect(toList());
        return exclusionFiltered.isEmpty() ? children : exclusionFiltered;
    }

    public void addPackageDependency(String fromPackage, String toPackage) {
        if (fromPackage != null && toPackage != null) {
            this.addChild(new Reference(
                    Side.from(unnamedIfEmpty(fromPackage), null),
                    "-->",
                    Side.to(unnamedIfEmpty(toPackage), null)));
        }
    }

    private boolean isExcludedPackage(String toPackage) {
        return getConfiguration().excludedPackageDependencies().stream()
                .anyMatch(excluded -> excluded.equals(toPackage)
                        || toPackage.startsWith(dotSuffixed(excluded))
                        || ("unnamed".equals(excluded) && toPackage.isEmpty()));
    }

    private boolean isIncludedChild(UMLNode child) {
        return child instanceof Reference && !isExcludedPackage(((Reference) child).to.toString());
    }

    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(getConfiguration().destinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            result.append(pumlFileName);
            pumlFile = new File(result.toString());
        }
        return pumlFile;
    }

    @Override
    protected IndentingPrintWriter writeChildrenTo(IndentingPrintWriter output) {
        output.append("set namespaceSeparator none").newline()
                .append("hide circle").newline()
                .append("hide empty fields").newline()
                .append("hide empty methods").newline().newline();
        super.writeChildrenTo(output);
        writePackageLinksTo(output.newline());
        return output;
    }

    private IndentingPrintWriter writePackageLinksTo(IndentingPrintWriter output) {
        output.println("' Package links");
        getChildren(Reference.class).stream()
                .flatMap(reference -> Stream.of(reference.from.toString(), reference.to.toString()))
                .distinct().map(packageName -> new Namespace(this, packageName))
                .forEach(namespace -> writePackageLinkTo(output, namespace));
        return output;
    }

    private IndentingPrintWriter writePackageLinkTo(IndentingPrintWriter output, Namespace namespace) {
        String link = Link.forPackage(namespace).toString().trim();
        if (!link.isEmpty()) {
            output.append("class \"").append(namespace.name).append("\" ").append(link).append(" {\n}\n");
        }
        return output;
    }

    private static String unnamedIfEmpty(String packageName) {
        return packageName.isEmpty() ? "unnamed" : packageName;
    }

    private static String dotSuffixed(String packageName) {
        return packageName.endsWith(".") ? packageName : packageName + '.';
    }
}
