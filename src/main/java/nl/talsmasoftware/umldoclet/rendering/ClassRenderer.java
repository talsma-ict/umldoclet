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
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.logging.LogSupport.GlobalPosition;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.model.Model.isDeprecated;

/**
 * Renderer to produce PlantUML output for a single class.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class ClassRenderer extends ParentAwareRenderer {

    protected final ClassDoc classDoc;
    private final Collection<NoteRenderer> notes;

    protected ClassRenderer(Renderer parent, ClassDoc classDoc) {
        super(parent);
        this.classDoc = requireNonNull(classDoc, "No class documentation provided.");
        this.notes = findLegacyNoteTags();

        // Add the various parts of the class UML, order matters here, obviously!
        initChildren_AddEnumConstants();
        initChildren_AddFields();
        initChildren_AddConstructors();
        initChildren_AddMethods();
    }

    private void initChildren_AddEnumConstants() {
        for (FieldDoc enumConstant : classDoc.enumConstants()) {
            children.add(new FieldRenderer(diagram, enumConstant));
        }
    }

    private void initChildren_AddFields() {
        // static fields come before non-static fields.
        final FieldDoc[] allFields = classDoc.fields(false);
        final List<FieldRenderer> nonStaticFields = new ArrayList<>(allFields.length);
        for (FieldDoc field : allFields) {
            if (field.isStatic()) {
                children.add(new FieldRenderer(diagram, field));
            } else {
                nonStaticFields.add(new FieldRenderer(diagram, field));
            }
        }
        children.addAll(nonStaticFields);
    }

    private void initChildren_AddConstructors() {
        for (ConstructorDoc constructor : classDoc.constructors(false)) {
            children.add(new MethodRenderer(diagram, constructor));
        }
    }

    private void initChildren_AddMethods() {
        final MethodDoc[] allMethods = classDoc.methods(false);
        List<MethodRenderer> abstractMethods = new ArrayList<>(allMethods.length);
        for (MethodDoc method : allMethods) {
            if (method.isAbstract()) {
                abstractMethods.add(new MethodRenderer(diagram, method));
            } else {
                children.add(new MethodRenderer(diagram, method));
            }
        }
        // abstract methods come after regular methods in our UML diagrams.
        if (!abstractMethods.isEmpty()) {
            children.addAll(abstractMethods);
        }
    }

    private Collection<NoteRenderer> findLegacyNoteTags() {
        Tag[] allNotes = classDoc.tags("note");
        ArrayList<NoteRenderer> legacyNoteTags = new ArrayList<>(allNotes.length);
        for (Tag notetag : allNotes) {
            final String note = notetag.text();
            if (note != null) legacyNoteTags.add(new NoteRenderer(this, note));
        }
        legacyNoteTags.trimToSize();
        return legacyNoteTags;
    }

    /**
     * Determines the 'UML' type for the class to be rendered.
     * Currently, this can return one of the following: {@code "enum"}, {@code "interface"}, {@code "abstract class"}
     * or (obviously) {@code "class"}.
     *
     * @return The UML type for the class to be rendered.
     */
    protected String umlType() {
        return classDoc.isEnum() ? "enum"
                : classDoc.isInterface() ? "interface"
                : classDoc.isAbstract() ? "abstract class"
                : "class";
    }

    /**
     * This method writes the 'generic' information to the writer, if available in the class documentation.
     * If data is written, starts with {@code '<'} and ends with {@code '>'}.
     *
     * @param out The writer to write to.
     * @return The writer so more content can easily be written.
     */
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

    /**
     * This method writes the notes for this class to the output.
     *
     * @param out The writer to write the notes to.
     * @return The writer so more content can easily be written.
     */
    protected IndentingPrintWriter writeNotesTo(IndentingPrintWriter out) {
        for (NoteRenderer note : notes) {
            note.writeTo(out);
        }
        return out;
    }

    /**
     * Determines the name of the class to be rendered.
     * This method considers whether to use the fully qualified class name (including package denomination etc) or
     * a shorter simple name.
     *
     * @return The name of the class to be rendered.
     */
    protected String name() {
        String name = classDoc.qualifiedName();
        if (parent instanceof UMLDiagram) {
            name = classDoc.name();
        } else if (parent instanceof PackageRenderer) {
            name = simplifyClassnameWithinPackage(name);
        }
        return name;
    }

    /**
     * This method simplifies the given className within the containing package under certain conditions:
     * <ol>
     * <li>The class must start with: the containing package name followed by a dot ({@code '.'})</li>
     * <li>The remaining simplified name may not contain any more dot characters
     * (plantUML cannot distinguish these outer classes from packages).</li>
     * <li>The setting {@code "-umlAlwaysUseQualifiedClassnames"} is {@code false}.</li>
     * </ol>
     * <p>
     * This method was introduced as a result of improvement documented in
     * <a href="https://github.com/talsma-ict/umldoclet/issues/15">issue 15</a>
     * </p>
     *
     * @param className The (qualified) class name to potentially simplify within the containing package.
     * @return The simplified class name or the specified (qualified) name if any condition was not met.
     */
    protected String simplifyClassnameWithinPackage(final String className) {
        final String packageName = classDoc.containingPackage().name();
        final String packagePrefix = packageName + ".";
        if (!className.startsWith(packagePrefix)) {
            LogSupport.trace("Cannot simplify classname \"{0}\" as it does not belong in package \"{1}\".", className, packageName);
        } else if (className.lastIndexOf('.') >= packagePrefix.length()) {
            LogSupport.trace("Inner-class \"{0}\" within package \"{1}\" could be simplified but will be left as-is because " +
                            "the remaining dot will make plantUML unable to distinguish the outer class from another package.",
                    className, packageName);
        } else if (diagram.config.alwaysUseQualifiedClassnames()) {
            LogSupport.debug("Not simplifying classname \"{0}\" to \"{1}\" because doclet parameters told us not to...",
                    className, className.substring(packagePrefix.length()));
        } else {
            String simpleClassname = className.substring(packagePrefix.length());
            LogSupport.trace("Simplifying class name \"{0}\" to \"{1}\" because it is contained within package \"{2}\"...",
                    className, simpleClassname, packageName);
            return simpleClassname;
        }
        return className;
    }

    /**
     * This method writes the name of the class to the output and marks (the fully qualified name of) the class as
     * an 'encountered type'.
     *
     * @param out The writer to write the class name to.
     * @return The writer so more content can easily be written.
     */
    protected IndentingPrintWriter writeNameTo(IndentingPrintWriter out) {
        diagram.encounteredTypes.add(classDoc.qualifiedName());
        return out.append(this.name());
    }

    /**
     * This method renders the class information to the specified output.
     *
     * @param out The writer to write the class name to.
     * @return The writer so more content can easily be written.
     */
    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        try (GlobalPosition gp = new GlobalPosition(classDoc.position())) {
            writeNameTo(out.append(umlType()).whitespace());
            writeGenericsTo(out);
            if (isDeprecated(classDoc)) {
                out.whitespace().append("<<deprecated>>"); // I don't know how to strikethrough a class name!
            }
            writeChildrenTo(out.whitespace().append('{').newline()).append('}').newline().newline();
            return writeNotesTo(out);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(classDoc.qualifiedName());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other != null && ClassRenderer.class.equals(other.getClass())
                && Objects.equals(classDoc.qualifiedName(), ((ClassRenderer) other).classDoc.qualifiedName()));
    }

}
