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
import nl.talsmasoftware.umldoclet.config.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.model.Reference;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;

import static nl.talsmasoftware.umldoclet.model.Reference.Side.from;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.to;

/**
 * This class is here to support references that were available from JavaDoc tags from the old PlantUML doclet.
 *
 * @author Sjoerd Talsma
 */
public enum LegacyTag {

    /**
     * Add support for @extends Controller
     * <p>
     * Pattern: &lt;associated class>
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
     * Pattern: &lt;cardinality> - &lt;cardinality> &lt;assoziated class>
     */
    NAVASSOC("<--", 3),

    DEPEND("<..", 3);

    private final String tagname;
    private final String umlreference;
    private final int classPos;

    private LegacyTag(String umlreference, int classPos) {
        this.tagname = name().toLowerCase(Locale.ENGLISH);
        this.umlreference = umlreference;
        this.classPos = classPos;
    }

    private ClassReferenceRenderer createReferenceFrom(final ClassRenderer parent, final Tag tag) {
        ClassReferenceRenderer refRenderer = null;
        if (parent != null && tag != null) {
            // Split tag content.
            String[] parts = tag.text().trim().split("\\s");
            if (classPos >= parts.length) {
                LogSupport.trace("No associated class found in @{0} tag \"{1}\".", tagname, tag.text());
                // TODO: Mark this as a JavaDoc error?
                return null;
            }

            // Figure out the referred type name.
            String referredType = parts[classPos].trim();
            ClassDoc referenceDoc = parent.classDoc.findClass(referredType);
            if (referenceDoc != null) {
                referredType = referenceDoc.qualifiedName();
            } else if (!referredType.contains(".") && parent.classDoc.containingPackage() != null) { // assume local name
                referredType = parent.classDoc.containingPackage().name() + "." + referredType;
            }

            // TODO: Maybe leave this concern to the ReferenceRenderer at rendering time?
            // Check if the type is not excluded from the UML rendering.
            if (parent.diagram.config.excludedReferences().contains(referredType)) {
                LogSupport.debug("Excluding @{0} tag \"{1}\"; the type is configured as \"excluded\".", tagname, referredType);
                return null;
            }

//            refRenderer = referenceDoc == null
//                    ? new ClassReferenceRenderer(parent, referredType, umlreference)
//                    : new ClassReferenceRenderer(parent, referenceDoc, umlreference);
            refRenderer = new ClassReferenceRenderer(parent, referredType, umlreference);

            // Support for Pattern: <cardinality> - <cardinality> <associated class>
            if (classPos == 3) { // Add optional cardinalities and note.
                final Reference withCardinalities = new Reference(
                        from(refRenderer.reference.from.qualifiedName, emptyToNull(parts[2])),
                        refRenderer.reference.type,
                        to(refRenderer.reference.to.qualifiedName, emptyToNull(parts[0])),
                        emptyToNull(parts[1])
                ).canonical();
                LogSupport.debug("Added cardinalities from legacy tag: {0}.", withCardinalities);
                refRenderer = new ClassReferenceRenderer(parent, withCardinalities);
            }
        }
        return refRenderer;
    }

    private String emptyToNull(String value) {
        value = value != null ? value.trim() : "";
        return value.isEmpty() || "-".equals(value) ? null : value;
    }

    static Collection<ClassReferenceRenderer> legacyReferencesFor(ClassRenderer includedClass) {
        Collection<ClassReferenceRenderer> legacyReferences = new LinkedHashSet<>();
        UMLDocletConfig config = includedClass == null ? null : includedClass.diagram.config;
        if (config != null && config.supportLegacyTags()) {
            for (LegacyTag legacytag : values()) {
                for (Tag tag : includedClass.classDoc.tags(legacytag.tagname)) {
                    ClassReferenceRenderer refRenderer = legacytag.createReferenceFrom(includedClass, tag);
                    if (refRenderer == null) {
                        LogSupport.trace("Tag @{0} did not result in a type from \"{1}\"...",
                                legacytag.tagname, tag.text());
                    } else if (legacyReferences.add(refRenderer)) {
                        LogSupport.trace("Tag @{0} resulted in a \"{1}\" type: {2}.",
                                legacytag.tagname, legacytag.umlreference, refRenderer.reference);
                    } else {
                        LogSupport.trace("Tag @{0} type already existed: {1}.",
                                legacytag.tagname, refRenderer.reference);
                    }
                }
            }
        }
        return legacyReferences;
    }

}
