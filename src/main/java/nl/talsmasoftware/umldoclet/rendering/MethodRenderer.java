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

import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Method renderer.
 * <p/>
 * For the moment this renderer is also used for rendering Constructors.
 * If this turns out to be too complex, constructors may be separated into their own specialized renderer class.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class MethodRenderer extends Renderer {
    private static final Logger LOGGER = Logger.getLogger(MethodRenderer.class.getName());

    protected final ExecutableMemberDoc methodDoc;

    public MethodRenderer(UMLDocletConfig config, UMLDiagram diagram, ExecutableMemberDoc methodDoc) {
        super(config, diagram);
        this.methodDoc = requireNonNull(methodDoc, "No method documentation provided.");
    }

    static IndentingPrintWriter writeParametersTo(IndentingPrintWriter out, ExecutableMemberDoc method, UMLDocletConfig config) {
        if (config.includeMethodParams()) {
            String separator = "";
            for (Parameter parameter : method.parameters()) {
                out.append(separator);
                if (config.includeMethodParamNames()) {
                    out.append(parameter.name());
                    if (config.includeMethodParamTypes()) {
                        out.append(':');
                    }
                }
                if (config.includeMethodParamTypes()) {
                    out.append(parameter.type().simpleTypeName());
                }
                separator = ", ";
            }
        }
        return out;
    }

    private boolean isConstructor() {
        return methodDoc instanceof ConstructorDoc;
    }

    protected IndentingPrintWriter writeReturnTypeTo(IndentingPrintWriter out) {
        if (methodDoc instanceof MethodDoc) {
            out.append(": ").append(((MethodDoc) methodDoc).returnType().typeName());
        }
        return out;
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        if (includeMethod()) {
            FieldRenderer.writeAccessibility(out, methodDoc)
                    .append(methodDoc.name()).append("(");
            writeParametersTo(out, methodDoc, config).append(')');
            return writeReturnTypeTo(out).newline();
        }
        return out;
    }

    protected boolean includeMethod() {
        boolean exclude = (isConstructor() && !config.includeConstructors())
                || (methodDoc.isPrivate() && !config.includePrivateMethods())
                || (methodDoc.isPackagePrivate() && !config.includePackagePrivateMethods())
                || (methodDoc.isProtected() && !config.includeProtectedMethods())
                || (methodDoc.isPublic() && !config.includePublicMethods());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "{0} \"{1}()\" {2}{3} included.",
                    new Object[]{
                            methodDoc.isStatic() ? "Static method" : (isConstructor() ? "Constructor" : "Method"),
                            methodDoc.qualifiedName(),
                            methodDoc.isPrivate() ? "is private and "
                                    : methodDoc.isPackagePrivate() ? "is package private and "
                                    : methodDoc.isProtected() ? "is protected and "
                                    : methodDoc.isPublic() ? "is public and " : "",
                            exclude ? "will not be" : "will be"});
        }
        return !exclude;
    }


}
