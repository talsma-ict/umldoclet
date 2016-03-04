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
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    protected final String umlreference;
    protected final ClassDoc referent;

    protected ClassReferenceRenderer(UMLDocletConfig config, UMLDiagram diagram, ClassDoc classDoc, String umlreference, ClassDoc referent) {
        super(config, diagram, classDoc);
        super.children.clear();
        this.umlreference = umlreference;
        this.referent = referent;
        if (config.includeAbstractSuperclassMethods()) {
            for (MethodDoc methodDoc : classDoc.methods(false)) {
                if (methodDoc.isAbstract()) {
                    children.add(new MethodRenderer(config, diagram, methodDoc));
                }
            }
        }
    }

    static List<ClassReferenceRenderer> referencesFor(ClassRenderer includedClass) {
        requireNonNull(includedClass, "Included class is required in order to find its references.");
        final ClassDoc referent = includedClass.classDoc;
        final String referentName = referent.qualifiedName();
        LOGGER.log(Level.FINEST, "Adding references for included class {0}...", referentName);
        List<ClassReferenceRenderer> references = new ArrayList<>();

        // Add extended superclass reference.
        final Collection<String> excludedReferences = includedClass.config.excludedReferences();
        if (referent.superclass() == null) {
            LOGGER.log(Level.FINE, "Encountered <null> as superclass of \"{0}\".", referentName);
        } else if (excludedReferences.contains(referent.superclass().qualifiedName())) {
            LOGGER.log(Level.FINEST, "Excluding superclass \"{0}\" of \"{1}\"...",
                    new Object[]{referent.superclass().qualifiedName(), referentName});
        } else {
            references.add(new ClassReferenceRenderer(
                    includedClass.config, includedClass.currentDiagram, referent.superclass(), "<|--", referent));
        }

        // Add implemented interface references.
        for (ClassDoc interfaceDoc : referent.interfaces()) {
            if (interfaceDoc == null) {
                LOGGER.log(Level.INFO, "Encountered <null> as implemented interface of \"{0}\".", referentName);
            } else if (excludedReferences.contains(interfaceDoc.qualifiedName())) {
                LOGGER.log(Level.FINEST, "Excluding interface \"{0}\" of \"{1}\"...",
                        new Object[]{interfaceDoc.qualifiedName(), referentName});
            } else {
                references.add(new ClassReferenceRenderer(
                        includedClass.config, includedClass.currentDiagram, interfaceDoc, "<|--", referent));
            }
        }

        // Add reference to containing classes.
        if (referent.containingClass() != null) {
            references.add(new ClassReferenceRenderer(
                    includedClass.config, includedClass.currentDiagram, referent.containingClass(), "+--", referent));
        }

        return references;
    }

    protected IndentingPrintWriter writeTypeDeclarationTo(IndentingPrintWriter out) {
        final String referenceTypename = classDoc.qualifiedName();
        if (!currentDiagram.encounteredTypes.add(referenceTypename)) {
            LOGGER.log(Level.FINEST, "Not generating type declaration for \"{0}\"; " +
                    "type was previously encountered in this diagram.", referenceTypename);
            return out;
        }

        LOGGER.log(Level.FINEST, "Generating type declaration for \"{0}\"...", referenceTypename);
        out.append(umlType()).append(' ').append(referenceTypename);
        super.writeGenericsTo(out);
        if (!children.isEmpty()) {
            writeChildrenTo(out.append('{').newline()).append('}');
        }
        return out.newline();
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        // Write type declaration if necessary.
        writeTypeDeclarationTo(out);
        final String referenceTypename = classDoc.qualifiedTypeName();

        // Write UML reference itself.
        LOGGER.log(Level.FINEST, "Generating reference: \"{0}\" {1} \"{2}\"...",
                new Object[]{referenceTypename, umlreference, referent.qualifiedName()});
        return out.append(referenceTypename)
                .append(' ').append(umlreference).append(' ')
                .append(referent.qualifiedTypeName()).newline().newline();
    }

}