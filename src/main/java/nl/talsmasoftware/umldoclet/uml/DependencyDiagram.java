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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.uml.Reference.Side;

import java.io.File;
import java.util.stream.Stream;

public class DependencyDiagram extends Diagram {

    private String pumlFileName;
    private File pumlFile = null;

    public DependencyDiagram(Configuration config, String pumlFileName) {
        super(config);
        this.pumlFileName = pumlFileName;
    }

    public void addPackageDependency(String fromPackage, String toPackage) {
        if (fromPackage != null && toPackage != null && !isExcludedPackage(toPackage)) {
            if (fromPackage.isEmpty()) fromPackage = "unnamed";
            if (toPackage.isEmpty()) toPackage = "unnamed";
            addChild(new Reference(Side.from(fromPackage, null), "-->", Side.to(toPackage, null)));
        }
    }

    private boolean isExcludedPackage(String toPackage) {
        return getConfiguration().excludedPackageDependencies().stream()
                .anyMatch(excluded -> excluded.equals(toPackage)
                        || toPackage.startsWith(dotSuffixed(excluded))
                        || ("unnamed".equals(excluded) && toPackage.isEmpty()));
    }

    private static String dotSuffixed(String packageName) {
        return packageName.endsWith(".") ? packageName : packageName + '.';
    }

    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            pumlFile = new File(getConfiguration().destinationDirectory(), pumlFileName);
        }
        return pumlFile;
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        output.append("set namespaceSeparator none").newline()
                .append("hide circle").newline()
                .append("hide empty fields").newline()
                .append("hide empty methods").newline().newline();
        super.writeChildrenTo(output);
        writePackageLinksTo(output.newline());
        return output;
    }

    private <IPW extends IndentingPrintWriter> IPW writePackageLinksTo(IPW output) {
        output.println("' Package links");
        getChildren().stream()
                .filter(Reference.class::isInstance).map(Reference.class::cast)
                .flatMap(reference -> Stream.of(reference.from.toString(), reference.to.toString()))
                .distinct().map(packageName -> new Namespace(this, packageName))
                .forEach(namespace -> writePackageLinkTo(output, namespace));
        return output;
    }

    private <IPW extends IndentingPrintWriter> IPW writePackageLinkTo(IPW output, Namespace namespace) {
        String link = Link.forPackage(namespace).toString().trim();
        if (!link.isEmpty()) {
            output.append("class \"").append(namespace.name).append("\" ").append(link).append(" {\n}\n");
        }
        return output;
    }
}
