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

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.ProgramElementDoc;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class FieldRenderer extends Renderer {
    private static final Logger LOGGER = Logger.getLogger(FieldRenderer.class.getName());

    protected final FieldDoc fieldDoc;

    public FieldRenderer(UMLDocletConfig config, UMLDiagram diagram, FieldDoc fieldDoc) {
        super(config, diagram);
        this.fieldDoc = requireNonNull(fieldDoc, "No field documentation provided.");
    }

    static IndentingPrintWriter writeAccessibility(IndentingPrintWriter out, ProgramElementDoc element) {
        if (element.isStatic()) {
            out.append("{static}").whitespace();
        }
        return element.isPrivate() ? out.append('-')
                : element.isProtected() ? out.append('#')
                : element.isPackagePrivate() ? out.append('~')
                : out.append('+');
    }

    protected boolean includeFieldType() {
        return config.includeFieldTypes() && !fieldDoc.isEnumConstant();
    }

    protected boolean includeField() {
        boolean exclude = (fieldDoc.isPrivate() && !config.includePrivateFields())
                || (fieldDoc.isPackagePrivate() && !config.includePackagePrivateFields())
                || (fieldDoc.isProtected() && !config.includeProtectedFields())
                || (fieldDoc.isPublic() && !config.includePublicFields()
                || (!config.includeDeprecatedFields() && isDeprecated(fieldDoc) && !isDeprecated(fieldDoc.containingClass()))
        );
        if (LOGGER.isLoggable(Level.FINEST)) {
            String designation = fieldDoc.isStatic() ? "Static field" : "Field";
            if (isDeprecated(fieldDoc)) {
                designation = "Deprecated " + Character.toLowerCase(designation.charAt(0)) + designation.substring(1);
            }
            LOGGER.log(Level.FINEST, "{0} \"{1}\" {2}{3} included.",
                    new Object[]{
                            designation,
                            fieldDoc.qualifiedName(),
                            fieldDoc.isPrivate() ? "is private and "
                                    : fieldDoc.isPackagePrivate() ? "is package private and "
                                    : fieldDoc.isProtected() ? "is protected and "
                                    : fieldDoc.isPublic() ? "is public and " : "",
                            exclude ? "will not be" : "will be"});
        }
        return !exclude;
    }

    protected IndentingPrintWriter writeNameTo(IndentingPrintWriter out) {
        return isDeprecated(fieldDoc)
                ? out.whitespace().append("--").append(fieldDoc.name()).append("--").whitespace()
                : out.append(fieldDoc.name());
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        if (includeField()) {
            writeAccessibility(out, fieldDoc);
            writeNameTo(out);
            if (includeFieldType()) {
                writeTypeTo(out.append(":").whitespace(), fieldDoc.type());
            }
            out.newline();
        }
        return out;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldDoc.qualifiedName());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof FieldRenderer
                && Objects.equals(fieldDoc.qualifiedName(), ((FieldRenderer) other).fieldDoc.qualifiedName()));
    }

}
