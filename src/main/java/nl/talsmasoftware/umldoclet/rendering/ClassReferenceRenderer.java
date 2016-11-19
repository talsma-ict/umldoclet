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
import nl.talsmasoftware.umldoclet.logging.GlobalPosition;
import nl.talsmasoftware.umldoclet.model.Reference;
import nl.talsmasoftware.umldoclet.model.Reference.Side;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.logging.LogSupport.*;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.from;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.to;

/**
 * Renderer for class references.
 *
 * @author Sjoerd Talsma
 */
public class ClassReferenceRenderer extends ClassRenderer {
    protected final ClassRenderer parent;
    protected Reference reference;

    /**
     * Creates a new class type to be rendered.
     *
     * @param parent          The class the type is from (which is the parent of this referencerenderer).
     * @param documentedClass The class the type is to.
     * @param umlreference    The UML type itself (reversed, so inheritance is <code>&lt;|--</code>).
     */
    private ClassReferenceRenderer(ClassRenderer parent, ClassDoc documentedClass, String umlreference) {
        this(parent, requireNonNull(documentedClass, "Referred class was <null>.").qualifiedName(), umlreference);
    }

    /**
     * Creates a new class type, but the referred class is not (yet) available for documentation.
     *
     * @param parent           The class the type is from (which is the parent of this referencerenderer).
     * @param referenceFromFqn The qualified of the referred class.
     * @param umlreference     The UML type itself (from the second argument to the first, so reversed:
     *                         inheritance is <code>&lt;|--</code>).
     */
    protected ClassReferenceRenderer(ClassRenderer parent, String referenceFromFqn, String umlreference) {
        this(parent, new Reference(from(referenceFromFqn), umlreference,
                to(requireNonNull(parent, "Parent was <null>.").classDoc.qualifiedName())));
    }

    protected ClassReferenceRenderer(ClassRenderer parent, Reference reference) {
        super(parent, referredClassDoc(parent, reference));
        super.children.clear();
        this.parent = parent;
        this.reference = requireNonNull(reference, "Reference is <null>.");
        if (!reference.isSelfReference()
                && diagram.config.includeAbstractSuperclassMethods()
                && !classDoc.equals(parent.classDoc)) { // Append abstract methods from referred superclass methods.
            for (MethodDoc methodDoc : classDoc.methods(false)) {
                if (methodDoc.isAbstract()) children.add(new MethodRenderer(diagram, methodDoc));
            }
        }
    }

    private static ClassDoc referredClassDoc(ClassRenderer source, Reference reference) {
        if (source == null) return null;
        if (reference != null) for (Side side : new Side[]{reference.to, reference.from}) {
            if (!side.qualifiedName.equals(source.classDoc.qualifiedName())) {
                ClassDoc referredClassDoc = source.classDoc.findClass(side.qualifiedName);
                if (referredClassDoc != null) return referredClassDoc;
            }
        }
        return source.classDoc;
    }

    /**
     * This generator method creates a collection of references for a given class.
     *
     * @param parent The rendered class to create references for.
     * @return The references.
     */
    static Collection<ClassReferenceRenderer> referencesFor(ClassRenderer parent) {
        try (GlobalPosition pos = new GlobalPosition(parent.classDoc)) {
            requireNonNull(parent, "Included class is required in order to find its references.");
            final String referentName = parent.classDoc.qualifiedName();
            trace("Adding references for included class {0}...", referentName);
            final Collection<ClassReferenceRenderer> references = new LinkedHashSet<>();
            final Collection<String> excludedReferences = parent.diagram.config.excludedReferences();

            // Add extended superclass type.
            ClassDoc superclass = parent.classDoc.superclass();
            final String superclassName = superclass == null ? null : superclass.qualifiedName();
            if (superclassName == null) {
                debug("Encountered <null> as superclass of \"{0}\".", referentName);
            } else if (excludedReferences.contains(superclassName)) {
                trace("Excluding superclass \"{0}\" of \"{1}\"...", superclassName, referentName);
            } else if (references.add(new ClassReferenceRenderer(parent, superclass, "<|--"))) {
                trace("Added type to superclass \"{0}\" from \"{1}\".", superclassName, referentName);
            } else {
                trace("Excluding type to superclass \"{0}\" from \"{1}\"; the type was already generated.",
                        superclassName, referentName);
            }

            // Add implemented interface references.
            for (ClassDoc interfaceDoc : parent.classDoc.interfaces()) {
                final String interfaceName = interfaceDoc == null ? null : interfaceDoc.qualifiedName();
                if (interfaceName == null) {
                    info("Encountered <null> as implemented interface of \"{0}\".", referentName);
                } else if (excludedReferences.contains(interfaceName)) {
                    trace("Excluding interface \"{0}\" of \"{1}\"...", interfaceName, referentName);
                } else if (references.add(new ClassReferenceRenderer(parent, interfaceDoc, "<|.."))) {
                    trace("Added type to interface \"{0}\" from \"{1}\".", interfaceName, referentName);
                } else {
                    debug("Excluding type to interface \"{0}\" from \"{1}\"; the type was already generated.", interfaceName, referentName);
                }
            }

            // Add type to containing classes.
            if (parent.classDoc.containingClass() != null) {
                references.add(new ClassReferenceRenderer(parent, parent.classDoc.containingClass(), "+--"));
            }

            // Support for tags defined in legacy doclet.
            references.addAll(LegacyTag.legacyReferencesFor(parent));

            return references;
        }
    }

    private String guessClassOrInterface() {
        return "<|..".equals(reference.type) || "..|>".equals(reference.type) ? "interface" : "class";
    }

    protected IndentingPrintWriter writeTypeDeclarationsTo(IndentingPrintWriter out) {
        for (final Side side : new Side[]{reference.from, reference.to}) {
            if (!diagram.encounteredTypes.add(side.qualifiedName)) {
                trace("Not generating type declaration for \"{0}\"; " +
                        "type was previously encountered in this diagram.", side.qualifiedName);
                continue;
            }
            final ClassDoc typeInfo = classDoc.findClass(side.qualifiedName);
            if (typeInfo == null) {
                trace("Generating 'unknown' class type declaration for \"{0}\"; " +
                        "we only have a class name type as declaration.", name());
                out.append(guessClassOrInterface());
                out.whitespace().append(parent.nameOf(side.qualifiedName));
                out.whitespace().append("<<(?,orchid)>>").newline();
                continue;
            }

            trace("Generating type declaration for \"{0}\"...", typeInfo.qualifiedName());
            out.append(umlTypeOf(typeInfo));
            out.whitespace().append(parent.nameOf(typeInfo.qualifiedName()));
            writeGenericsOf(typeInfo, out);
            if (!children.isEmpty()) writeChildrenTo(out.whitespace().append("{").newline()).append('}');
            out.newline();
        }
        return out;
    }

    /**
     * @return Whether this type is to the class itself.
     */
    protected boolean isSelfReference() {
        return reference.isSelfReference();
    }

    /**
     * @param note The note to be added to this class reference.
     */
    protected void addNote(String note) {
        this.reference = reference.addNote(note);
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        // Write type declaration if necessary.
        writeTypeDeclarationsTo(out);

        // Write UML reference itself.
        trace("Generating type: {0}...", reference);
        out.append(parent.simplifyClassnameWithinPackage(reference.from.qualifiedName))
                .whitespace().append(quoted(reference.from.cardinality))
                .whitespace().append(reference.type)
                .whitespace().append(quoted(reference.to.cardinality))
                .whitespace().append(parent.simplifyClassnameWithinPackage(reference.to.qualifiedName));

        if (!reference.notes.isEmpty()) {
            String sep = ": ";
            for (String note : reference.notes) {
                out.append(sep).append(note);
                sep = "\\n";
            }
        }
        return out.newline().newline();
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ClassReferenceRenderer
                && reference.equals(((ClassReferenceRenderer) other).reference));
    }

}
