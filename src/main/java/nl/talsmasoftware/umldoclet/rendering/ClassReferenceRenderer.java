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
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Created on 23-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class ClassReferenceRenderer extends ClassRenderer {
    protected final ClassRenderer parent;
    protected final String qualifiedName;
    protected final String umlreference;

    // Additiona info fields to be added to the reference.
    String cardinality1, cardinality2, note;

    protected ClassReferenceRenderer(ClassRenderer parent, ClassDoc documentedClass, String umlreference) {
        this(parent, documentedClass, null, umlreference);
    }

    protected ClassReferenceRenderer(ClassRenderer parent, String documentedClassQualifiedName, String umlreference) {
        this(parent, null, documentedClassQualifiedName, umlreference);
    }

    private ClassReferenceRenderer(ClassRenderer parent, ClassDoc documentedClass, String qualifiedName, String umlreference) {
        super(parent, documentedClass == null ? parent.classDoc : documentedClass);
        this.parent = requireNonNull(parent, "No parent renderer for class reference provided.");
        super.children.clear();
        this.qualifiedName = requireNonNull(documentedClass == null ? qualifiedName : documentedClass.qualifiedName(),
                "Qualified name of documented reference is required.");
        this.umlreference = requireNonNull(umlreference, "No UML reference type provided.");
        if (diagram.config.includeAbstractSuperclassMethods() && documentedClass != null) {
            for (MethodDoc methodDoc : documentedClass.methods(false)) {
                if (methodDoc.isAbstract()) {
                    children.add(new MethodRenderer(diagram, methodDoc));
                }
            }
        }
    }

    static Collection<ClassReferenceRenderer> referencesFor(ClassRenderer parent) {
        requireNonNull(parent, "Included class is required in order to find its references.");
        final String referentName = parent.classDoc.qualifiedName();
        LogSupport.trace("Adding references for included class {0}...", referentName);
        final Collection<ClassReferenceRenderer> references = new LinkedHashSet<>();
        final Collection<String> excludedReferences = parent.diagram.config.excludedReferences();

        // Add extended superclass reference.
        ClassDoc superclass = parent.classDoc.superclass();
        final String superclassName = superclass == null ? null : superclass.qualifiedName();
        if (superclassName == null) {
            LogSupport.debug("Encountered <null> as superclass of \"{0}\".", referentName);
        } else if (excludedReferences.contains(superclassName)) {
            LogSupport.trace("Excluding superclass \"{0}\" of \"{1}\"...", superclassName, referentName);
        } else if (references.add(new ClassReferenceRenderer(parent, superclass, "<|--"))) {
            LogSupport.trace("Added reference to superclass \"{0}\" from \"{1}\".", superclassName, referentName);
        } else {
            LogSupport.trace("Excluding reference to superclass \"{0}\" from \"{1}\"; the reference was already generated.",
                    superclassName, referentName);
        }

        // Add implemented interface references.
        for (ClassDoc interfaceDoc : parent.classDoc.interfaces()) {
            final String interfaceName = interfaceDoc == null ? null : interfaceDoc.qualifiedName();
            if (interfaceName == null) {
                LogSupport.info("Encountered <null> as implemented interface of \"{0}\".", referentName);
            } else if (excludedReferences.contains(interfaceName)) {
                LogSupport.trace("Excluding interface \"{0}\" of \"{1}\"...", interfaceName, referentName);
            } else if (references.add(new ClassReferenceRenderer(parent, interfaceDoc, "<|.."))) {
                LogSupport.trace("Added reference to interface \"{0}\" from \"{1}\".", new Object[]{interfaceName, referentName});
            } else {
                LogSupport.debug("Excluding reference to interface \"{0}\" from \"{1}\"; the reference was already generated.", new Object[]{interfaceName, referentName});
            }
        }

        // Add reference to containing classes.
        if (parent.classDoc.containingClass() != null) {
            references.add(new ClassReferenceRenderer(parent, parent.classDoc.containingClass(), "+--"));
        }

        // Support for tags defined in legacy doclet.
        references.addAll(LegacyTag.legacyReferencesFor(parent));

        return references;
    }

    private String guessClassOrInterface() {
        return "<|..".equals(umlreference) ? "interface" : "class";
    }

    protected IndentingPrintWriter writeTypeDeclarationTo(IndentingPrintWriter out) {
        if (!diagram.encounteredTypes.add(qualifiedName)) {
            LogSupport.trace("Not generating type declaration for \"{0}\"; " +
                    "type was previously encountered in this diagram.", qualifiedName);
            return out;
        } else if (!qualifiedName.equals(classDoc.qualifiedName())) {
            LogSupport.trace("Generating 'unknown' class type declaration for \"{0}\"; " +
                    "we only have a class name reference as declaration.", name());
            return out.append(guessClassOrInterface()).whitespace().append(name()).append(" <<(?,orchid)>>").newline();
        }

        LogSupport.trace("Generating type declaration for \"{0}\"...", name());
        out.append(umlType()).whitespace().append(name());
        super.writeGenericsTo(out);
        if (!children.isEmpty()) {
            writeChildrenTo(out.append(" {").newline()).append('}');
        }
        return out.newline();
    }

    @Override
    protected String name() {
        // Optionally simplify the name within the referring class' package.
        return parent.simplifyClassnameWithinPackage(qualifiedName);
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        // Write type declaration if necessary.
        writeTypeDeclarationTo(out);

        // Write UML reference itself.
        LogSupport.trace("Generating reference: \"{0}\" {1} \"{2}\"...", qualifiedName, umlreference, parent.name());
        out.append(name()).whitespace()
                .append(quoted(cardinality2)).whitespace()
                .append(umlreference).whitespace()
                .append(quoted(cardinality1)).whitespace()
                .append(parent.name());
        if (note != null && !note.trim().isEmpty()) {
            out.append(": ").append(note);
        }
        return out.newline().newline();
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, parent, umlreference);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ClassReferenceRenderer
                && Objects.equals(parent, ((ClassReferenceRenderer) other).parent)
                && Objects.equals(qualifiedName, ((ClassReferenceRenderer) other).qualifiedName)
                && Objects.equals(umlreference, ((ClassReferenceRenderer) other).umlreference)
        );
    }

}
