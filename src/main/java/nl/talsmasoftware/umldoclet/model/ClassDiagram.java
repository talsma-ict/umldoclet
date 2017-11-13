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

import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public class ClassDiagram extends UMLDiagram {

    private final Clazz clz;

    public ClassDiagram(Configuration config, DocletEnvironment env, TypeElement classElement) {
        super(config, env);
        this.clz = new Clazz(this, classElement);
        super.children.add(clz);
    }

    protected String diagramPath() {
        String packagePath = clz.containingPackage().getQualifiedName().toString().replace('.', '/');
        return packagePath + "/" + clz.getSimpleName() + ".puml";
    }

    public void render() {
//        env.getJavaFileManager().getLocationForModule()

        config.reporter().print(Diagnostic.Kind.OTHER, clz.classElement, "Diagram path: " + diagramPath());
        config.reporter().print(Diagnostic.Kind.OTHER, clz.classElement, this.toString());
    }

}
