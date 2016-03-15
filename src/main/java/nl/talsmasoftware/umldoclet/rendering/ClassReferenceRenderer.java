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

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Created on 23-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class ClassReferenceRenderer extends ClassRenderer {
    private static final Logger LOGGER = Logger.getLogger(ClassReferenceRenderer.class.getName());

    protected final String qualifiedName;
    protected final String umlreference;
    protected final ClassDoc referent;

    // Additiona info fields to be added to the reference.
    String cardinality1, cardinality2, note;

    protected ClassReferenceRenderer(UMLDiagram diagram, ClassDoc documentedClass, String umlreference, ClassDoc referent) {
        this(diagram, documentedClass, null, umlreference, referent);
    }

    protected ClassReferenceRenderer(UMLDiagram diagram, String documentedClassQualifiedName, String umlreference, ClassDoc referent) {
        this(diagram, null, documentedClassQualifiedName, umlreference, referent);
    }

    private ClassReferenceRenderer(UMLDiagram diagram, ClassDoc documentedClass, String qualifiedName,
                                   String umlreference, ClassDoc referent) {

        super(diagram, documentedClass == null ? referent : documentedClass);
        super.children.clear();
        this.qualifiedName = requireNonNull(documentedClass == null ? qualifiedName : documentedClass.qualifiedName(),
                "Qualified name of documented reference is required.");
        this.umlreference = requireNonNull(umlreference, "No UML reference type provided.");
        this.referent = requireNonNull(referent, "No referent provided.");
        if (diagram.config.includeAbstractSuperclassMethods() && documentedClass != null) {
            for (MethodDoc methodDoc : documentedClass.methods(false)) {
                if (methodDoc.isAbstract()) {
                    children.add(new MethodRenderer(diagram, methodDoc));
                }
            }
        }
    }

    static Collection<ClassReferenceRenderer> referencesFor(ClassRenderer includedClass) {
        requireNonNull(includedClass, "Included class is required in order to find its references.");
        final ClassDoc referent = includedClass.classDoc;
        final String referentName = referent.qualifiedName();
        LOGGER.log(Level.FINEST, "Adding references for included class {0}...", referentName);
        final Collection<ClassReferenceRenderer> references = new LinkedHashSet<>();
        final Collection<String> excludedReferences = includedClass.diagram.config.excludedReferences();

        // Add extended superclass reference.
        final String superclassName = referent.superclass() == null ? null : referent.superclass().qualifiedName();
        if (superclassName == null) {
            LOGGER.log(Level.FINE, "Encountered <null> as superclass of \"{0}\".", referentName);
        } else if (excludedReferences.contains(superclassName)) {
            LOGGER.log(Level.FINEST, "Excluding superclass \"{0}\" of \"{1}\"...",
                    new Object[]{superclassName, referentName});
        } else if (references.add(new ClassReferenceRenderer(
                includedClass.diagram, referent.superclass(), "<|--", referent))) {
            LOGGER.log(Level.FINEST, "Added reference to superclass \"{0}\" from \"{1}\".", new Object[]{superclassName, referentName});
        } else {
            LOGGER.log(Level.FINE, "Excluding reference to superclass \"{0}\" from \"{1}\"; the reference was already generated.", new Object[]{superclassName, referentName});
        }

        // Add implemented interface references.
        for (ClassDoc interfaceDoc : referent.interfaces()) {
            final String interfaceName = interfaceDoc == null ? null : interfaceDoc.qualifiedName();
            if (interfaceName == null) {
                LOGGER.log(Level.INFO, "Encountered <null> as implemented interface of \"{0}\".", referentName);
            } else if (excludedReferences.contains(interfaceName)) {
                LOGGER.log(Level.FINEST, "Excluding interface \"{0}\" of \"{1}\"...",
                        new Object[]{interfaceName, referentName});
            } else if (references.add(new ClassReferenceRenderer(
                    includedClass.diagram, interfaceDoc, "<|..", referent))) {
                LOGGER.log(Level.FINEST, "Added reference to interface \"{0}\" from \"{1}\".", new Object[]{interfaceName, referentName});
            } else {
                LOGGER.log(Level.FINE, "Excluding reference to interface \"{0}\" from \"{1}\"; the reference was already generated.", new Object[]{interfaceName, referentName});
            }
        }

        // Add reference to containing classes.
        if (referent.containingClass() != null) {
            references.add(new ClassReferenceRenderer(
                    includedClass.diagram, referent.containingClass(), "+--", referent));
        }

        // Support for tags defined in legacy doclet.
        references.addAll(LegacyTag.legacyReferencesFor(includedClass));

        return references;
    }

    private String guessClassOrInterface() {
        return "<|..".equals(umlreference) ? "interface" : "class";
    }

    protected IndentingPrintWriter writeTypeDeclarationTo(IndentingPrintWriter out) {
        if (!diagram.encounteredTypes.add(qualifiedName)) {
            LOGGER.log(Level.FINEST, "Not generating type declaration for \"{0}\"; " +
                    "type was previously encountered in this diagram.", qualifiedName);
            return out;
        } else if (!qualifiedName.equals(classDoc.qualifiedName())) {
            LOGGER.log(Level.FINEST, "Generating 'unknown' class type declaration for \"{0}\"; " +
                    "we only have a class name reference as declaration.", qualifiedName);
            return out.append(guessClassOrInterface()).whitespace().append(qualifiedName).append(" <<(?,orchid)>>").newline();
        }

        LOGGER.log(Level.FINEST, "Generating type declaration for \"{0}\"...", qualifiedName);
        out.append(umlType()).whitespace().append(qualifiedName);
        super.writeGenericsTo(out);
        if (!children.isEmpty()) {
            writeChildrenTo(out.append(" {").newline()).append('}');
        }
        return out.newline();
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        // Write type declaration if necessary.
        writeTypeDeclarationTo(out);

        // Write UML reference itself.
        LOGGER.log(Level.FINEST, "Generating reference: \"{0}\" {1} \"{2}\"...",
                new Object[]{qualifiedName, umlreference, referent.qualifiedName()});
        out.append(qualifiedName).whitespace()
                .append(quoted(cardinality2)).whitespace()
                .append(umlreference).whitespace()
                .append(quoted(cardinality1)).whitespace()
                .append(referent.qualifiedTypeName());
        if (note != null && !note.trim().isEmpty()) {
            out.append(": ").append(note);
        }
        return out.newline().newline();
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, umlreference, referent.qualifiedName());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ClassReferenceRenderer
                && Objects.equals(qualifiedName, ((ClassReferenceRenderer) other).qualifiedName)
                && Objects.equals(umlreference, ((ClassReferenceRenderer) other).umlreference)
                && Objects.equals(referent.qualifiedName(), ((ClassReferenceRenderer) other).referent.qualifiedName())
        );
    }

}
