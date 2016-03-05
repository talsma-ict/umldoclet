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
 */
package nl.talsmasoftware.umldoclet.rendering;

import com.sun.javadoc.*;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Renderer to produce PlantUML output for a single class.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class ClassRenderer extends Renderer {

    protected final ClassDoc classDoc;

    public ClassRenderer(UMLDocletConfig config, UMLDiagram diagram, ClassDoc classDoc) {
        super(config, diagram);
        this.classDoc = requireNonNull(classDoc, "No class documentation provided.");
        for (FieldDoc enumConstant : classDoc.enumConstants()) {
            children.add(new FieldRenderer(config, diagram, enumConstant));
        }
        for (FieldDoc field : classDoc.fields(false)) {
            children.add(new FieldRenderer(config, diagram, field));
        }
        for (ConstructorDoc constructor : classDoc.constructors(false)) {
            children.add(new MethodRenderer(config, diagram, constructor));
        }
        List<MethodRenderer> abstractMethods = new ArrayList<>();
        for (MethodDoc method : classDoc.methods(false)) {
            if (method.isAbstract()) {
                abstractMethods.add(new MethodRenderer(config, diagram, method));
            } else {
                children.add(new MethodRenderer(config, diagram, method));
            }
        }
        children.addAll(abstractMethods); // abstract methods come last in our UML diagrams.
    }

    protected String umlType() {
        return classDoc.isEnum() ? "enum"
                : classDoc.isInterface() ? "interface"
                : classDoc.isAbstract() ? "abstract class"
                : "class";
    }

    protected IndentingPrintWriter writeGenericsTo(IndentingPrintWriter out) {
        if (classDoc.typeParameters().length > 0) {
            out.append('<');
            String sep = "";
            for (TypeVariable generic : classDoc.typeParameters()) {
                out.append(sep).append(generic.typeName());
                sep = ", ";
            }
            out.append('>');
        }
        return out;
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        currentDiagram.encounteredTypes.add(classDoc.qualifiedTypeName());
        out.append(umlType()).append(' ').append(classDoc.qualifiedTypeName());
        writeGenericsTo(out);
        if (isDeprecated(classDoc)) {
            out.append(" <<deprecated>>"); // I don't know how to strikethrough a class name!
        }
        out.append(" {").newline();
        return writeChildrenTo(out).append("}").newline().newline();
    }

    @Override
    public int hashCode() {
        return Objects.hash(classDoc.qualifiedName());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other != null && ClassRenderer.class.equals(other.getClass())
                && Objects.equals(classDoc.qualifiedName(), ((ClassRenderer) other).classDoc.qualifiedName()));
        // || super.equals(other);
    }

}
