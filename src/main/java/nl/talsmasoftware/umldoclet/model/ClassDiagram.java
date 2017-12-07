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

import javax.lang.model.element.TypeElement;
import javax.tools.DocumentationTool;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import java.io.IOException;

public class ClassDiagram extends UMLDiagram {

    private final Type cls;

    public ClassDiagram(Configuration config, DocletEnvironment env, TypeElement classElement) {
        super(config, env);
        this.cls = new Type(this, classElement);
        super.children.add(cls);
    }

    public void render() {
        try {
            config.info(Messages.INFO_GENERATING_FILE, umlPath());
            JavaFileManager fm = env.getJavaFileManager(); // TODO use this instead of writing to file directly
            DocumentationTool.Location loc = DocumentationTool.Location.DOCUMENTATION_OUTPUT;
//            JavaFileObject javaFileForOutput = fm.getJavaFileForOutput(loc, cls.typeElement.getQualifiedName().toString(),
//                    JavaFileObject.Kind.OTHER, new UmlFileObject());
            FileObject fileForOutput = fm.getFileForOutput(loc, cls.containingPackage().getQualifiedName().toString(),
                    cls.getSimpleName() + ".puml", new UmlFileObject());
//        try (Writer writer = new OutputStreamWriter(new FileOutputStream(umlPath()))) {
//            writeTo(IndentingPrintWriter.wrap(writer, config.indentation));
//        } catch (IOException | RuntimeException e) {
//            config.reporter().print(Diagnostic.Kind.ERROR, cls.typeElement, "Error rendering class diagram: " + e.getMessage());
//        }
        } catch (IOException | RuntimeException e) {
//            throw new IllegalStateException(e.getMessage(), e); TODO
        }
    }

    protected String umlPath() {
        StringBuilder result = new StringBuilder(config.destDirName);
        if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
        result.append(cls.containingPackage().getQualifiedName().toString().replace('.', '/')).append('/');
        return result.append(cls.getSimpleName()).append(".puml").toString();
    }

    protected String imgPath(String type) {
        // For now in the same location as the UML. TODO: add support for 'imagedir'
        return umlPath().replaceAll("\\.puml$", "." + type);
    }

}
