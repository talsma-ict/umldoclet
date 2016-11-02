/*
 * Copyright (C) 2016 Talsma ICT
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
 *
 */

package nl.talsmasoftware.umldoclet.rendering;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;

import java.util.Collection;

import static nl.talsmasoftware.umldoclet.model.Model.find;

/**
 * This class is specifically meant for class dependencies among each-other.
 *
 * @author Sjoerd Talsma
 */
public class ClassDependencyRenderer extends ClassReferenceRenderer {

    protected ClassDependencyRenderer(ClassRenderer fromClass, ClassDoc toClass) {
        super(fromClass, toClass, "<..");
    }

    static void addDiagramDependenciesTo(Collection<ClassReferenceRenderer> references, ClassRenderer theClass, Collection<ClassRenderer> diagramClasses) {
        if (theClass != null) for (final Renderer child : theClass.children) {
            if (child instanceof FieldRenderer) {
                final FieldRenderer field = (FieldRenderer) child;
                if (field.includeField()) {
                    final ClassRenderer toClass = findTypeInDiagram(field.fieldDoc.type(), diagramClasses);
                    if (toClass != null) { // Disable field in class and add dependency.
                        field.disabled = true;
                        final ClassDependencyRenderer dep = new ClassDependencyRenderer(theClass, toClass.classDoc);
                        references.add(dep);
                        find(references, dep).notes.add(field.fieldDoc.name());
                    }
                }
            }
        }
    }

    private static ClassRenderer findTypeInDiagram(Type type, Collection<ClassRenderer> diagramClasses) {
        // TODO Add support for Optionals, Arrays and known Collections.
        // TODO remove self-reference for Enums

        if (type != null) for (ClassRenderer renderedClass : diagramClasses) {
            if (type.qualifiedTypeName().equals(renderedClass.classDoc.qualifiedTypeName())) return renderedClass;
        }
        return null;
    }

}
