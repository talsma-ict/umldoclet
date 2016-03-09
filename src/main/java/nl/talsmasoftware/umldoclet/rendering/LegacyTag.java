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
import com.sun.javadoc.Tag;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is here to support references that were available from JavaDoc tags from the old PlantUML doclet.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public enum LegacyTag {

    /**
     * Add support for @extends Controller
     * <p>
     * Pattern: <associated class>
     * </p>
     */
    EXTENDS("<|--", 0),
    /**
     * Add support for @implements Interface
     */
    IMPLEMENTS("<|..", 0),

    ASSOC("--", 3),

    /**
     * Add support for @navassoc
     * <p>
     * Pattern: <cardinality> - <cardinality> <assoziated class>
     * </p>
     */
    NAVASSOC("<--", 3),

    DEPEND("<..", 3);

    private static final Logger LOGGER = Logger.getLogger(LegacyTag.class.getName());

    private final String tagname;
    private final String umlreference;
    private final int fromCardPos, notePos, classPos, refCardPos;

    private LegacyTag(String umlreference, int classPos) {
        this.tagname = name().toLowerCase(Locale.ENGLISH);
        this.umlreference = umlreference;
        this.fromCardPos = classPos == 3 ? 0 : -1;
        this.notePos = classPos == 3 ? 1 : -1;
        this.classPos = classPos;
        this.refCardPos = classPos == 3 ? 2 : -1;
    }

    private ClassReferenceRenderer createReferenceFrom(UMLDocletConfig config, UMLDiagram diagram, ClassDoc includedClassDoc, Tag tag) {
        ClassReferenceRenderer reference = null;
        if (tag != null) {
            // Split tag content.
            String[] parts = tag.text().trim().split("\\s");
            if (classPos >= parts.length) {
                LOGGER.log(Level.FINE, "No associated class found in @{0} tag \"{1}\".", new Object[]{tagname, tag.text()});
                // TODO: Mark this as a JavaDoc error?
                return null;
            }

            // Figure out the referred type name.
            String typename = parts[classPos].trim();
            ClassDoc referenceDoc = includedClassDoc.findClass(typename);
            if (referenceDoc != null) {
                typename = referenceDoc.qualifiedName();
            } else if (!typename.contains(".") && includedClassDoc.containingPackage() != null) {
                typename = includedClassDoc.containingPackage().name() + "." + typename;
            }

            // TODO: Maybe leave this concern to the ReferenceRenderer at rendering time?
            // Check if the type is not excluded from the UML rendering.
            if (config.excludedReferences().contains(typename)) {
                LOGGER.log(Level.FINE, "Excluding @{0} tag \"{1}\"; the reference is configured as \"excluded\".", new Object[]{tagname, typename});
                return null;
            }

            reference = referenceDoc == null
                    ? new ClassReferenceRenderer(config, diagram, typename, umlreference, includedClassDoc)
                    : new ClassReferenceRenderer(config, diagram, referenceDoc, umlreference, includedClassDoc);

            // TODO Do something with the cardinality!
            if (fromCardPos >= 0) {
                // Subclass CardinalityClassReferenceRenderer ??
            }
        }
        return reference;
    }

    static Collection<ClassReferenceRenderer> legacyReferencesFor(ClassRenderer includedClass) {
        Collection<ClassReferenceRenderer> legacyReferences = new LinkedHashSet<>();
        if (includedClass != null && includedClass.config.supportLegacyTags()) {
            for (LegacyTag legacytag : values()) {
                for (Tag tag : includedClass.classDoc.tags(legacytag.tagname)) {
                    ClassReferenceRenderer reference = legacytag.createReferenceFrom(
                            includedClass.config, includedClass.currentDiagram, includedClass.classDoc, tag);
                    if (reference == null) {
                        LOGGER.log(Level.FINEST, "Tag @{0} did not result in a reference from \"{1}\"...",
                                new Object[]{legacytag.tagname, tag.text()});
                    } else if (legacyReferences.add(reference)) {
                        LOGGER.log(Level.FINEST, "Tag @{0} resulted in a \"{1}\" reference to {2}...",
                                new Object[]{legacytag.tagname, legacytag.umlreference, reference.qualifiedName});
                    } else {
                        LOGGER.log(Level.FINEST, "Tag @{0} reference already existed to {1}...",
                                new Object[]{legacytag.tagname, reference.qualifiedName});
                    }
                }
            }
        }
        return legacyReferences;
    }


}
