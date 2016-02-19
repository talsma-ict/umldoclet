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
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

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

    private final FieldDoc fieldDoc;

    public FieldRenderer(UMLDocletConfig config, FieldDoc fieldDoc) {
        super(config);
        this.fieldDoc = requireNonNull(fieldDoc, "No field documentation provided.");
    }

    static IndentingPrintWriter writeAccessibility(IndentingPrintWriter out, ProgramElementDoc element) {
        if (element.isStatic()) {
            out.append("{static} ");
        }
        return element.isPrivate() ? out.append("-")
                : element.isProtected() ? out.append("#")
                : element.isPackagePrivate() ? out.append("~")
                : out.append("+");
    }

    protected boolean includeFieldType() {
        return config.includeFieldTypes() && !fieldDoc.isEnumConstant();
    }

    protected boolean includeField() {
        boolean exclude = (fieldDoc.isPrivate() && !config.includePrivateFields())
                || (fieldDoc.isPackagePrivate() && !config.includePackagePrivateFields())
                || (fieldDoc.isProtected() && !config.includeProtectedFields())
                || (fieldDoc.isPublic() && !config.includePublicFields());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "{0} \"{1}\" {2}{3} included.",
                    new Object[]{
                            fieldDoc.isStatic() ? "Static field" : "Field",
                            fieldDoc.qualifiedName(),
                            fieldDoc.isPrivate() ? "is private and "
                                    : fieldDoc.isPackagePrivate() ? "is package private and "
                                    : fieldDoc.isProtected() ? "is protected and "
                                    : fieldDoc.isPublic() ? "is public and " : "",
                            exclude ? "will not be" : "will be"});
        }
        return !exclude;
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        if (includeField()) {
            writeAccessibility(out, fieldDoc).append(fieldDoc.name());
            if (includeFieldType()) {
                out.append(": ").append(fieldDoc.type().typeName());
                ParameterizedType parameterizedType = fieldDoc.type().asParameterizedType();
                // TODO add parameterized type for generics.
            }
            out.newline();
        }
        return out;
    }

}
