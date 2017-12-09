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
import nl.talsmasoftware.umldoclet.configuration.Messages;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import javax.lang.model.element.TypeElement;
import java.io.*;

public class ClassDiagram extends UMLDiagram {

    private final Type cls;
    private String umlPath = null;

    public ClassDiagram(Configuration config, DocletEnvironment env, TypeElement classElement) {
        super(config, env);
        this.cls = new Type(this, classElement);
        super.children.add(cls);
    }

    public void render() {
        config.info(Messages.INFO_GENERATING_FILE, umlPath());
        try {
            File umlFile = new File(umlPath());
            if (!umlFile.getParentFile().exists() && !umlFile.getParentFile().mkdirs()) {
                throw new IllegalStateException("Can't create directory \"" + umlFile.getParent() + "\".");
            }
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(umlFile))) {
                this.writeTo(IndentingPrintWriter.wrap(writer, config.indentation));
            }
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Couldn't render \"" + umlPath() + "\": " + e.getMessage(), e);
        }
    }

    protected String umlPath() {
        if (umlPath == null) {
            StringBuilder result = new StringBuilder(config.destDirName);
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            result.append(cls.containingPackage().getQualifiedName().toString().replace('.', '/'));
            umlPath = result.append('/').append(cls.getSimpleName()).append(".puml").toString();
        }
        return umlPath;
    }

    protected String imgPath(String type) {
        // For now in the same location as the UML. TODO: add support for 'imagedir'
        return umlPath().replaceAll("\\.puml$", "." + type);
    }

}
