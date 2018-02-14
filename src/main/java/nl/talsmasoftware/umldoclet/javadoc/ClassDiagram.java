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
package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.uml.Namespace;
import nl.talsmasoftware.umldoclet.uml.Type;
import nl.talsmasoftware.umldoclet.uml.UMLDiagram;

import javax.lang.model.element.TypeElement;
import java.io.File;

/**
 * @author Sjoerd Talsma
 */
class ClassDiagram extends UMLDiagram {

    private final Namespace namespace;
    private final Type type;
    private File pumlFile = null;

    ClassDiagram(UMLFactory factory, TypeElement classElement) {
        super(factory.config);
        this.namespace = factory.packageOf(classElement);
        this.type = factory.createType(classElement);
        UMLFactory.addChild(namespace, type);
        children.add(namespace);
    }

    @Override
    protected File pumlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(config.getDestinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            result.append(type.containingPackage.name.replace('.', '/')).append('/');
            if (type.name.qualified.startsWith(type.containingPackage.name + ".")) {
                result.append(type.name.qualified.substring(type.containingPackage.name.length() + 1));
            } else {
                result.append(type.name.simple);
            }
            pumlFile = ensureParentDir(new File(result.append(".puml").toString()));
        }
        return pumlFile;
    }

}
