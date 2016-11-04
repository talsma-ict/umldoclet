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
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.model.Model;

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

    // TODO encounteredTypes is already in diagram.encounteredClasses but only populated at render-time.
    static void addDiagramDependenciesTo(Collection<ClassReferenceRenderer> references, ClassRenderer theClass, Collection<ClassRenderer> diagramClasses) {
        if (theClass != null) for (final Renderer child : theClass.children) {
            if (child instanceof FieldRenderer) {
                final FieldRenderer field = (FieldRenderer) child;
                if (field.includeField() && !field.fieldDoc.isStatic()) {
                    Type fieldType = Model.optionalType(field.fieldDoc.type());
                    final boolean optional = fieldType != null;
                    if (!optional) fieldType = field.fieldDoc.type();
                    final ClassRenderer toClass = findTypeInDiagram(fieldType, diagramClasses);
                    if (toClass != null) { // Disable field in class and add dependency.
                        final ClassDependencyRenderer dep = new ClassDependencyRenderer(theClass, toClass.classDoc);
                        if (optional) dep.cardinality2 = "0..1";
                        if (addDependency(field.fieldDoc.name(), references, dep)) field.disabled = true;
                    }
                }
            }
        }
    }

    private static ClassRenderer findTypeInDiagram(Type type, Collection<ClassRenderer> diagramClasses) {
        // TODO Add support for Arrays and known Collections.

        if (type != null) for (ClassRenderer renderedClass : diagramClasses) {
            if (type.qualifiedTypeName().equals(renderedClass.classDoc.qualifiedTypeName())) return renderedClass;
        }
        return null;
    }

    private static boolean addDependency(String name, Collection<ClassReferenceRenderer> refs, ClassDependencyRenderer dep) {
        if (dep.isSelfReference() && dep.classDoc.isEnum()) {
            LogSupport.debug("Not adding self-referencing Enum dependency {0}...", dep);
            return false;
        }
        refs.add(dep);
        find(refs, dep).notes.add(name);
        return true;
    }

}
