/*
 * Copyright 2016-2024 Talsma ICT
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class PackageDiagram extends Diagram {
    private static final String SEPARATOR_DIRECTIVE = "set separator ";
    public static final String SEPARATOR = "::";

    final String moduleName;
    final String packageName;
    private File pumlFile = null;

    public PackageDiagram(Configuration config, String packageName, String moduleName) {
        super(config);
        this.packageName = requireNonNull(packageName, "Package name is <null>.");
        this.moduleName = moduleName;
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeCustomDirectives(List<String> customDirectives, IPW output) {
        final List<String> directives = new ArrayList<>(customDirectives == null ? Collections.emptyList() : customDirectives);
        directives.removeIf(directive -> directive.startsWith(SEPARATOR_DIRECTIVE));
        directives.add(SEPARATOR_DIRECTIVE + SEPARATOR);
        return super.writeCustomDirectives(directives, output);
    }

    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(getConfiguration().destinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            if (moduleName != null) result.append(moduleName).append('/');
            result.append(packageName.replace('.', '/'));
            result.append("/package.puml");
            pumlFile = new File(result.toString());
        }
        return pumlFile;
    }
}
