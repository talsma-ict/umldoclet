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
import nl.talsmasoftware.umldoclet.util.FileUtils;

import java.io.File;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class PackageDiagram extends Diagram {

    final String packageName; // TODO: maybe auto-detect lazily by first addChild() of type Namespace?
    private File pumlFile = null;

    public PackageDiagram(Configuration config, String packageName) {
        super(config);
        this.packageName = requireNonNull(packageName, "Package name is <null>.");
    }

    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(getConfiguration().destinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            result.append(packageName.replace('.', '/'));
            result.append("/package.puml");
            pumlFile = FileUtils.ensureParentDir(new File(result.toString()));
        }
        return pumlFile;
    }
}
