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
import java.util.Collection;
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
    private final Collection<NoteRenderer> notes = new ArrayList<>();

    public ClassRenderer(UMLDocletConfig config, UMLDiagram diagram, ClassDoc classDoc) {
        super(config, diagram);
        this.classDoc = requireNonNull(classDoc, "No class documentation provided.");
        // Enum constants are added first.
        for (FieldDoc enumConstant : classDoc.enumConstants()) {
            children.add(new FieldRenderer(config, diagram, enumConstant));
        }
        // TODO: Couldn't we make Renderer Comparable and have 'children' become a TreeSet?
        // --> Probably, after more tests are in place!
        List<FieldRenderer> fields = new ArrayList<>(); // static fields come before non-static fields.
        for (FieldDoc field : classDoc.fields(false)) {
            if (field.isStatic()) {
                children.add(new FieldRenderer(config, diagram, field));
            } else {
                fields.add(new FieldRenderer(config, diagram, field));
            }
        }
        children.addAll(fields);
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
        children.addAll(abstractMethods); // abstract methods come after regular methods in our UML diagrams.

        // Support for tags defined in legacy doclet.
        // TODO: Depending on the amount of code this generates this should be refactored away (after unit testing).
        addLegacyNoteTag();
    }

    private void addLegacyNoteTag() {
        // for (String tagname : new String[] {"note"}) {
        final String tagname = "note";
        for (Tag notetag : classDoc.tags(tagname)) {
            String note = notetag.text();
            if (note != null) {
                notes.add(new NoteRenderer(config, currentDiagram, note, classDoc.qualifiedName()));
            }
        }
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

    protected IndentingPrintWriter writeNotesTo(IndentingPrintWriter out) {
        for (NoteRenderer note : notes) {
            note.writeTo(out);
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
        writeChildrenTo(out.append(" {").newline()).append("}").newline().newline();
        return writeNotesTo(out);
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
