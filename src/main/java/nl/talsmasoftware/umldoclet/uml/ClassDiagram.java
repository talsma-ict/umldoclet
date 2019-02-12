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

public class ClassDiagram extends Diagram {

    private File pumlFile = null;

    public ClassDiagram(Configuration config, Type type) {
        super(config);
        addChild(Literal.line("set namespaceSeparator none"));
        addChild(Literal.line("hide empty fields"));
        addChild(Literal.line("hide empty methods"));
        addChild(Literal.NEWLINE);
        addChild(type);
    }

    public Type getType() {
        return getChildren().stream()
                .filter(Type.class::isInstance).map(Type.class::cast)
                .findFirst().orElseThrow(() -> new IllegalStateException("No Type defined in Class diagram!"));
    }

    @Override
    public void addChild(UMLNode child) {
        super.addChild(child);
        if (child instanceof Type) ((Type) child).addPackageToName();
    }

    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            final Type type = getType();
            StringBuilder result = new StringBuilder(getConfiguration().destinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            String containingPackage = type.getNamespace().name;
            result.append(containingPackage.replace('.', '/')).append('/');
            if (type.getName().qualified.startsWith(containingPackage + ".")) {
                result.append(type.getName().qualified.substring(containingPackage.length() + 1));
            } else {
                result.append(type.getName().simple);
            }
            pumlFile = FileUtils.ensureParentDir(new File(result.append(".puml").toString()));
        }
        return pumlFile;
    }

}
