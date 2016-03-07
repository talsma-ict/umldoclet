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
import com.sun.javadoc.Tag;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
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

    protected ClassReferenceRenderer(UMLDocletConfig config, UMLDiagram diagram, ClassDoc documentedClass, String umlreference, ClassDoc referent) {
        this(config, diagram, documentedClass, null, umlreference, referent);
    }

    protected ClassReferenceRenderer(UMLDocletConfig config, UMLDiagram diagram, String documentedClassQualifiedName, String umlreference, ClassDoc referent) {
        this(config, diagram, null, documentedClassQualifiedName, umlreference, referent);
    }

    private ClassReferenceRenderer(UMLDocletConfig config, UMLDiagram diagram, ClassDoc documentedClass, String qualifiedName,
                                   String umlreference, ClassDoc referent) {

        super(config, diagram, documentedClass == null ? referent : documentedClass);
        super.children.clear();
        this.qualifiedName = requireNonNull(documentedClass == null ? qualifiedName : documentedClass.qualifiedName(),
                "Qualified name of documented reference is required.");
        this.umlreference = requireNonNull(umlreference, "No UML reference type provided.");
        this.referent = requireNonNull(referent, "No referent provided.");
        if (config.includeAbstractSuperclassMethods() && documentedClass != null) {
            for (MethodDoc methodDoc : documentedClass.methods(false)) {
                if (methodDoc.isAbstract()) {
                    children.add(new MethodRenderer(config, diagram, methodDoc));
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
        final Collection<String> excludedReferences = includedClass.config.excludedReferences();

        // Add extended superclass reference.
        final String superclassName = referent.superclass() == null ? null : referent.superclass().qualifiedName();
        if (superclassName == null) {
            LOGGER.log(Level.FINE, "Encountered <null> as superclass of \"{0}\".", referentName);
        } else if (excludedReferences.contains(superclassName)) {
            LOGGER.log(Level.FINEST, "Excluding superclass \"{0}\" of \"{1}\"...",
                    new Object[]{superclassName, referentName});
        } else if (references.add(new ClassReferenceRenderer(
                includedClass.config, includedClass.currentDiagram, referent.superclass(), "<|--", referent))) {
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
                    includedClass.config, includedClass.currentDiagram, interfaceDoc, "<|--", referent))) {
                LOGGER.log(Level.FINEST, "Added reference to interface \"{0}\" from \"{1}\".", new Object[]{interfaceName, referentName});
            } else {
                LOGGER.log(Level.FINE, "Excluding reference to interface \"{0}\" from \"{1}\"; the reference was already generated.", new Object[]{interfaceName, referentName});
            }
        }

        // Add reference to containing classes.
        if (referent.containingClass() != null) {
            references.add(new ClassReferenceRenderer(
                    includedClass.config, includedClass.currentDiagram, referent.containingClass(), "+--", referent));
        }

        // Support for tags defined in legacy doclet.
        // TODO: Depending on the amount of code this generates this should be refactored away (after unit testing).
        addLegacyExtendsImplementsTag(references, includedClass);

        return references;
    }

    static void addLegacyExtendsImplementsTag(Collection<ClassReferenceRenderer> references, ClassRenderer includedClass) {
        if (references != null && includedClass != null && includedClass.config.supportLegacyTags()) {
            // add support for: @extends Controlle
            // add support for: @implements Interface
            for (String tagname : new String[]{"extends", "implements"}) {
                for (Tag tag : includedClass.classDoc.tags(tagname)) {
                    String extendedTypeName = tag.text().trim();
                    if (extendedTypeName.indexOf(' ') > 0) {
                        extendedTypeName = extendedTypeName.substring(0, extendedTypeName.indexOf(' '));
                    }
                    ClassDoc extendedType = includedClass.classDoc.findClass(extendedTypeName);
                    if (extendedType != null) {
                        extendedTypeName = extendedType.qualifiedTypeName();
                    } else if (!extendedTypeName.contains(".") && includedClass.classDoc.containingPackage() != null) {
                        extendedTypeName = includedClass.classDoc.containingPackage().name() + "." + extendedTypeName;
                    }

                    if (includedClass.config.excludedReferences().contains(extendedTypeName)) {
                        LOGGER.log(Level.FINE, "Excluding @{0} tag \"{1}\"; the reference is configured as \"excluded\".", new Object[]{tagname, extendedTypeName});
                        break;
                    }
                    if (references.add(new ClassReferenceRenderer(includedClass.config, includedClass.currentDiagram, extendedType, extendedTypeName, "<|--", includedClass.classDoc))) {
                        LOGGER.log(Level.FINEST, "Added @{0} reference to \"{1}\" from \"{2}\".", new Object[]{tagname, extendedTypeName, includedClass.classDoc.qualifiedName()});
                    } else {
                        LOGGER.log(Level.FINE, "Excluding @{0} tag \"{1}\"; the reference was already generated.", new Object[]{tagname, extendedTypeName});
                    }
                }
            }
        }
    }

    protected IndentingPrintWriter writeTypeDeclarationTo(IndentingPrintWriter out) {
        if (!currentDiagram.encounteredTypes.add(qualifiedName)) {
            LOGGER.log(Level.FINEST, "Not generating type declaration for \"{0}\"; " +
                    "type was previously encountered in this diagram.", qualifiedName);
            return out;
        } else if (!qualifiedName.equals(classDoc.qualifiedName())) {
            LOGGER.log(Level.FINEST, "Generating 'unknown' class type declaration for \"{0}\"; " +
                    "we only have a class name reference as declaration.", qualifiedName);
            return out.append("class ").append(qualifiedName).append(" <<(?,orchid)>>").newline();
        }

        LOGGER.log(Level.FINEST, "Generating type declaration for \"{0}\"...", qualifiedName);
        out.append(umlType()).append(' ').append(qualifiedName);
        super.writeGenericsTo(out);
        if (!children.isEmpty()) {
            writeChildrenTo(out.append('{').newline()).append('}');
        }
        return out.newline();
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        // Write type declaration if necessary.
        writeTypeDeclarationTo(out);

        // Write UML reference itself.
        LOGGER.log(Level.FINEST, "Generating reference: \"{0}\" {1} \"{2}\"...",
                new Object[]{qualifiedName, umlreference, referent.qualifiedName()});
        return out.append(qualifiedName)
                .append(' ').append(umlreference).append(' ')
                .append(referent.qualifiedTypeName()).newline().newline();
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
