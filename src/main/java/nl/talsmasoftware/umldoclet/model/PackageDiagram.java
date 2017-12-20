/*
 * Copyright 2016-2017 Talsma ICT
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
package nl.talsmasoftware.umldoclet.model;

import jdk.javadoc.doclet.DocletEnvironment;
import nl.talsmasoftware.umldoclet.configuration.Configuration;

import javax.lang.model.element.PackageElement;
import java.io.File;

/**
 * @author Sjoerd Talsma
 */
public class PackageDiagram extends UMLDiagram {

    protected final PackageElement packageElement;
    private File pumlFile = null;

    public PackageDiagram(Configuration config, DocletEnvironment env, PackageElement packageElement) {
        super(config, env);
        this.packageElement = packageElement;
        this.children.add(new Package(this, packageElement));
    }

    @Override
    protected File pumlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(config.getDestinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            result.append(packageElement.getQualifiedName().toString().replace('.', '/'));
            result.append("/package.puml");
            pumlFile = ensureParentDir(new File(result.toString()));
        }
        return pumlFile;
    }

}
