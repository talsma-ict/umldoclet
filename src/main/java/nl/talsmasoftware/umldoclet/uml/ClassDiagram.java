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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.util.FileUtils;

import java.io.File;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class ClassDiagram extends UMLDiagram {

    final Type type;
    private File pumlFile = null;

    public ClassDiagram(Configuration config, Type type) {
        super(config);
        this.type = requireNonNull(type, "Type in classdiagram is <null>.");
        addChild(Literal.line("set namespaceSeparator none"));
        addChild(Literal.line("hide empty fields"));
        addChild(Literal.line("hide empty methods"));
        addChild(Literal.NEWLINE);
        addChild(type);
    }

    @Override
    public void addChild(UMLPart child) {
        if (child instanceof Type) child = ((Type) child).addPackageToName();
        super.addChild(child);
    }

    @Override
    public File pumlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(getConfiguration().destinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            String containingPackage = type.getNamespace().name;
            result.append(containingPackage.replace('.', '/')).append('/');
            if (type.name.qualified.startsWith(containingPackage + ".")) {
                result.append(type.name.qualified.substring(containingPackage.length() + 1));
            } else {
                result.append(type.name.simple);
            }
            pumlFile = FileUtils.ensureParentDir(new File(result.append(".puml").toString()));
        }
        return pumlFile;
    }

}
