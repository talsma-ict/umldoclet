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
import nl.talsmasoftware.umldoclet.logging.GlobalPosition;
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.logging.LogSupport.concatLowercaseParts;
import static nl.talsmasoftware.umldoclet.logging.LogSupport.trace;
import static nl.talsmasoftware.umldoclet.model.Model.isDeprecated;

/**
 * Created on 17-02-2016.
 *
 * @author Sjoerd Talsma
 */
public class FieldRenderer extends Renderer {
    protected final FieldDoc fieldDoc;
    boolean disabled = false;

    protected FieldRenderer(DiagramRenderer diagram, FieldDoc fieldDoc) {
        super(diagram);
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
        return diagram.config.includeFieldTypes() && !fieldDoc.isEnumConstant();
    }

    protected boolean includeField() {
        boolean exclude = (disabled && !diagram.config.includeDisabledFields())
                || (fieldDoc.isPrivate() && !diagram.config.includePrivateFields())
                || (fieldDoc.isPackagePrivate() && !diagram.config.includePackagePrivateFields())
                || (fieldDoc.isProtected() && !diagram.config.includeProtectedFields())
                || (fieldDoc.isPublic() && !diagram.config.includePublicFields()
                || (!diagram.config.includeDeprecatedFields() && isDeprecated(fieldDoc) && !isDeprecated(fieldDoc.containingClass())));

        if (LogSupport.isTraceEnabled()) {
            final String designation = concatLowercaseParts(
                    disabled ? "Disabled" : null,
                    isDeprecated(fieldDoc) ? "Deprecated" : null,
                    fieldDoc.isStatic() ? "Static" : null,
                    "Field");

            trace("{0} \"{1}\" {2}{3} included.",
                    designation,
                    fieldDoc.qualifiedName(),
                    fieldDoc.isPrivate() ? "is private and "
                            : fieldDoc.isPackagePrivate() ? "is package private and "
                            : fieldDoc.isProtected() ? "is protected and "
                            : fieldDoc.isPublic() ? "is public and " : "",
                    exclude ? "will not be" : "will be");
        }
        return !exclude;
    }

    protected IndentingPrintWriter writeNameTo(IndentingPrintWriter out) {
        return isDeprecated(fieldDoc)
                ? out.whitespace().append("--").append(fieldDoc.name()).append("--").whitespace()
                : out.append(fieldDoc.name());
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        try (GlobalPosition gp = new GlobalPosition(fieldDoc)) {
            if (includeField()) {
                if (disabled) out.append("' ");
                writeAccessibility(out, fieldDoc);
                writeNameTo(out);
                if (includeFieldType()) {
                    writeTypeTo(out.append(":").whitespace(), fieldDoc.type());
                }
                out.newline();
            }
            return out;
        }
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
