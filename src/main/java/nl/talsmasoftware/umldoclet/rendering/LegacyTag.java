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
    private final int classPos;

    private LegacyTag(String umlreference, int classPos) {
        this.tagname = name().toLowerCase(Locale.ENGLISH);
        this.umlreference = umlreference;
        this.classPos = classPos;
    }

    private ClassReferenceRenderer createReferenceFrom(ClassRenderer parent, Tag tag) {
        ClassReferenceRenderer reference = null;
        if (parent != null && tag != null) {
            // Split tag content.
            String[] parts = tag.text().trim().split("\\s");
            if (classPos >= parts.length) {
                LOGGER.log(Level.FINE, "No associated class found in @{0} tag \"{1}\".", new Object[]{tagname, tag.text()});
                // TODO: Mark this as a JavaDoc error?
                return null;
            }

            // Figure out the referred type name.
            String typename = parts[classPos].trim();
            ClassDoc referenceDoc = parent.classDoc.findClass(typename);
            if (referenceDoc != null) {
                typename = referenceDoc.qualifiedName();
            } else if (!typename.contains(".") && parent.classDoc.containingPackage() != null) {
                typename = parent.classDoc.containingPackage().name() + "." + typename;
            }

            // TODO: Maybe leave this concern to the ReferenceRenderer at rendering time?
            // Check if the type is not excluded from the UML rendering.
            if (parent.diagram.config.excludedReferences().contains(typename)) {
                LOGGER.log(Level.FINE, "Excluding @{0} tag \"{1}\"; the reference is configured as \"excluded\".", new Object[]{tagname, typename});
                return null;
            }

            reference = referenceDoc == null
                    ? new ClassReferenceRenderer(parent, typename, umlreference)
                    : new ClassReferenceRenderer(parent, referenceDoc, umlreference);

            // Support for Pattern: <cardinality> - <cardinality> <associated class>
            if (classPos == 3) {
                reference.cardinality1 = emptyToNull(parts[0]);
                reference.note = emptyToNull(parts[1]);
                reference.cardinality2 = emptyToNull(parts[2]);
            }
        }
        return reference;
    }

    private String emptyToNull(String value) {
        return value == null || value.trim().isEmpty() || "-".equals(value.trim()) ? null : value.trim();
    }

    static Collection<ClassReferenceRenderer> legacyReferencesFor(ClassRenderer includedClass) {
        Collection<ClassReferenceRenderer> legacyReferences = new LinkedHashSet<>();
        UMLDocletConfig config = includedClass == null ? null : includedClass.diagram.config;
        if (config != null && config.supportLegacyTags()) {
            for (LegacyTag legacytag : values()) {
                for (Tag tag : includedClass.classDoc.tags(legacytag.tagname)) {
                    ClassReferenceRenderer reference = legacytag.createReferenceFrom(
                            includedClass, tag);
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
