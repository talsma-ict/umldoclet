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

import com.sun.javadoc.*;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Objects;
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

    /**
     * Important method that determines whether or not the documented method or constructor should be included in the
     * UML diagram.
     * <p/>
     * This method is rather complex because it is highly configurable whether or not a method should be rendered.
     *
     * @return Whether this method or constructor should be included in the UML diagram.
     */
    protected boolean includeMethod() {
        boolean exclude = isMethodFromExcludedClass()
                || (isConstructor() && !config.includeConstructors())
                || (isDefaultAndOnlyConstructor() && !config.includeDefaultConstructors())
                || (methodDoc.isPrivate() && !config.includePrivateMethods())
                || (methodDoc.isPackagePrivate() && !config.includePackagePrivateMethods())
                || (methodDoc.isProtected() && !config.includeProtectedMethods())
                || (methodDoc.isPublic() && !config.includePublicMethods())
                || (!config.includeDeprecatedMethods() && isDeprecated(methodDoc) && !isDeprecated(methodDoc.containingClass()));

        if (LOGGER.isLoggable(Level.FINEST)) {
            String designation = methodDoc.isStatic() ? "Static method"
                    : isDefaultConstructor() ? "Default constructor"
                    : isConstructor() ? "Constructor"
                    : isAbstract() ? "Abstract method"
                    : "Method";
            if (isDeprecated(methodDoc)) {
                designation = "Deprecated " + Character.toLowerCase(designation.charAt(0)) + designation.substring(1);
            }
            LOGGER.log(Level.FINEST, "{0} \"{1}{2}\" {3}{4}.",
                    new Object[]{
                            designation,
                            methodDoc.qualifiedName(),
                            methodDoc.flatSignature(),
                            methodDoc.isPrivate() ? "is private and "
                                    : methodDoc.isPackagePrivate() ? "is package private and "
                                    : methodDoc.isProtected() ? "is protected and "
                                    : methodDoc.isPublic() ? "is public and "
                                    : "",
                            exclude ? "will not be included" : "will be included"});
        }
        return !exclude;
    }

    protected IndentingPrintWriter writeNameTo(IndentingPrintWriter out) {
        return isDeprecated(methodDoc)
                ? out.append(" --").append(methodDoc.name()).append("-- ")
                : out.append(methodDoc.name());
    }

    protected IndentingPrintWriter writeParametersTo(IndentingPrintWriter out) {
        if (config.includeMethodParams()) {
            String separator = "";
            for (Parameter parameter : methodDoc.parameters()) {
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

    protected IndentingPrintWriter writeReturnTypeTo(IndentingPrintWriter out) {
        if (methodDoc instanceof MethodDoc) {
            out.append(": ").append(((MethodDoc) methodDoc).returnType().typeName());
        }
        return out;
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        if (includeMethod()) {
            // deprecation:
            //        + --deprecatedString--(): String <<deprecated>>

            if (isAbstract()) {
                out.write("{abstract} ");
            }
            FieldRenderer.writeAccessibility(out, methodDoc);
            writeNameTo(out);
            writeParametersTo(out.append("(")).append(')');
            if (methodDoc instanceof MethodDoc) {
                writeTypeTo(out.append(": "), ((MethodDoc) methodDoc).returnType());
            }
            return out.newline();
        }
        return out;
    }

    private boolean isConstructor() {
        return methodDoc instanceof ConstructorDoc;
    }

    private boolean isDefaultConstructor() {
        return isConstructor() && methodDoc.parameters().length == 0;
    }

    private boolean isDefaultAndOnlyConstructor() {
        return isDefaultConstructor() && methodDoc.containingClass().constructors(false).length == 1;
    }

    private boolean isAbstract() {
        return methodDoc instanceof MethodDoc && ((MethodDoc) methodDoc).isAbstract();
    }

    private static MethodDoc findMethod(ClassDoc classDoc, String methodName, String flatSignature) {
        for (MethodDoc method : classDoc.methods(false)) {
            if (method != null && method.name().equals(methodName) && method.flatSignature().equals(flatSignature)) {
                return method;
            }
        }
        return null;
    }

    /**
     * @return Whether overridden methods from excluded classes (such as java.lang.Object normally)
     * and this method happens to be such a method.
     */
    private boolean isMethodFromExcludedClass() {
        if (methodDoc instanceof MethodDoc && !config.includeOverridesFromExcludedReferences()) {
            ClassDoc overriddenClass = ((MethodDoc) methodDoc).overriddenClass();
            while (overriddenClass != null) {
                if (config.excludedReferences().contains(overriddenClass.qualifiedName())) {
                    LOGGER.log(Level.FINEST, "Method \"{0}{1}\" overrides method from excluded reference \"{2}\".",
                            new Object[]{methodDoc.qualifiedName(), methodDoc.flatSignature(), overriddenClass.qualifiedName()});
                    return true;
                }
                MethodDoc foundMethod = findMethod(overriddenClass, methodDoc.name(), methodDoc.flatSignature());
                overriddenClass = foundMethod == null ? null : foundMethod.overriddenClass();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodDoc.qualifiedName(), methodDoc.flatSignature());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof MethodRenderer
                && Objects.equals(methodDoc.qualifiedName(), ((MethodRenderer) other).methodDoc.qualifiedName())
                && Objects.equals(methodDoc.flatSignature(), ((MethodRenderer) other).methodDoc.flatSignature())
        );
    }

}
