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
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.model.Model;
import nl.talsmasoftware.umldoclet.model.Reference;

import java.util.Collection;

import static nl.talsmasoftware.umldoclet.model.Model.find;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.from;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.to;

/**
 * This class is specifically meant for class dependencies among each-other from object properties..
 *
 * @author Sjoerd Talsma
 */
public class ClassPropertyRenderer extends ClassReferenceRenderer {

    protected ClassPropertyRenderer(ClassRenderer fromClass, ClassDoc toClass, String cardinality) {
        super(fromClass, new Reference(from(fromClass.classDoc.qualifiedName()), "-->", to(toClass.qualifiedName(), cardinality)));
    }

    // TODO encounteredTypes is already in diagram.encounteredClasses but only populated at render-time.
    static void addDiagramDependenciesTo(Collection<ClassReferenceRenderer> references, ClassRenderer theClass, Collection<ClassDoc> diagramClasses) {
        if (theClass != null) for (final Renderer child : theClass.children) {
            if (child instanceof FieldRenderer) {
                final FieldRenderer field = (FieldRenderer) child;
                if (field.includeField() && !field.fieldDoc.isStatic()) {
                    Type fieldType = Model.optionalType(field.fieldDoc.type());
                    String cardinality = fieldType != null ? "0..1" : null;
                    if (fieldType == null) {
                        fieldType = Model.iterableType(field.fieldDoc.type());
                        cardinality = fieldType != null ? "*" : null;
                    }
                    if (fieldType == null) fieldType = field.fieldDoc.type();
                    final ClassDoc toClass = findTypeInDiagram(fieldType, diagramClasses);
                    if (toClass != null) { // Disable field in class and add dependency.
                        final ClassPropertyRenderer dep = new ClassPropertyRenderer(theClass, toClass, cardinality);
                        if (addDependency(field.fieldDoc.name(), references, dep)) field.disabled = true;
                    }
                }
            } else if (child instanceof MethodRenderer) {
                final MethodRenderer method = (MethodRenderer) child;
                final String propertyname = method.propertyName();
                if (propertyname != null && method.methodDoc instanceof MethodDoc) {
                    final Type type = ((MethodDoc) method.methodDoc).returnType();
                    Type propertyType = Model.optionalType(type);
                    String cardinality = propertyType != null ? "0..1" : null;
                    if (propertyType == null) {
                        propertyType = Model.iterableType(type);
                        cardinality = propertyType != null ? "*" : null;
                    }
                    if (propertyType == null) propertyType = type;
                    final ClassDoc toClass = findTypeInDiagram(propertyType, diagramClasses);
                    if (toClass != null) { // Disable accessor method and add dependency.
                        final ClassPropertyRenderer dep = new ClassPropertyRenderer(theClass, toClass, cardinality);
                        if (addDependency(propertyname, references, dep)) method.disabled = true;
                    }
                }
            }
        }
    }

    private static ClassDoc findTypeInDiagram(Type type, Collection<ClassDoc> diagramClasses) {
        if (type != null && diagramClasses != null) {
            final String qualifiedTypeName = type.qualifiedTypeName();
            for (ClassDoc renderedClass : diagramClasses) {
                if (qualifiedTypeName.equals(renderedClass.qualifiedTypeName())) return renderedClass;
            }
        }
        return null;
    }

    private static boolean addDependency(String name, Collection<ClassReferenceRenderer> refs, ClassPropertyRenderer dep) {
        if (dep.isSelfReference() && dep.classDoc.isEnum()) {
            LogSupport.debug("Not adding self-referencing Enum dependency {0}...", dep);
            return false;
        }
        refs.add(dep);
        find(refs, dep).addNote(name);
        return true;
    }

}
