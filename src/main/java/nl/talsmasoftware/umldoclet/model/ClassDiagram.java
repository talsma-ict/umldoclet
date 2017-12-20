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
import nl.talsmasoftware.umldoclet.configuration.DocletConfig;

import javax.lang.model.element.TypeElement;
import java.io.File;

/**
 * @author Sjoerd Talsma
 */
public class ClassDiagram extends UMLDiagram {

    private final Type type;
    private File pumlFile = null;

    public ClassDiagram(DocletConfig config, DocletEnvironment env, TypeElement classElement) {
        super(config, env);
        this.type = new Type(this, classElement);
        super.children.add(type);
    }

    @Override
    protected File pumlFile() {
        if (pumlFile == null) {
            StringBuilder result = new StringBuilder(config.getDestinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            result.append(type.containingPackage().getQualifiedName().toString().replace('.', '/'));
            result.append('/').append(type.getSimpleName()).append(".puml");
            pumlFile = ensureParentDir(new File(result.toString()));
        }
        return pumlFile;
    }

}
