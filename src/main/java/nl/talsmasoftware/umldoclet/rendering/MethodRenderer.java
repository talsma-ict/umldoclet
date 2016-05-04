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
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.model.Model;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.model.Model.isDeprecated;

/**
 * Method renderer.
 * <p/>
 * For the moment this renderer is also used for rendering Constructors.
 * If this turns out to be too complex, constructors may be separated into their own specialized renderer class.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class MethodRenderer extends Renderer {
    protected final ExecutableMemberDoc methodDoc;

    protected MethodRenderer(UMLDiagram diagram, ExecutableMemberDoc methodDoc) {
        super(diagram);
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
                || (isConstructor() && !diagram.config.includeConstructors())
                || (isDefaultAndOnlyConstructor() && !diagram.config.includeDefaultConstructors())
                || (methodDoc.isPrivate() && !diagram.config.includePrivateMethods())
                || (methodDoc.isPackagePrivate() && !diagram.config.includePackagePrivateMethods())
                || (methodDoc.isProtected() && !diagram.config.includeProtectedMethods())
                || (methodDoc.isPublic() && !diagram.config.includePublicMethods())
                || (!diagram.config.includeDeprecatedMethods() && isDeprecated(methodDoc) && !isDeprecated(methodDoc.containingClass()));

        if (LogSupport.isTraceEnabled()) {
            String designation = methodDoc.isStatic() ? "Static method"
                    : isDefaultConstructor() ? "Default constructor"
                    : isConstructor() ? "Constructor"
                    : isAbstract() ? "Abstract method"
                    : "Method";
            if (isDeprecated(methodDoc)) {
                designation = "Deprecated " + Character.toLowerCase(designation.charAt(0)) + designation.substring(1);
            }
            LogSupport.trace("{0} \"{1}{2}\" {3}{4}.",
                    designation,
                    methodDoc.qualifiedName(),
                    methodDoc.flatSignature(),
                    methodDoc.isPrivate() ? "is private and "
                            : methodDoc.isPackagePrivate() ? "is package private and "
                            : methodDoc.isProtected() ? "is protected and "
                            : methodDoc.isPublic() ? "is public and "
                            : "",
                    exclude ? "will not be included" : "will be included");
        }
        return !exclude;
    }

    protected IndentingPrintWriter writeNameTo(IndentingPrintWriter out) {
        return isDeprecated(methodDoc)
                ? out.whitespace().append("--").append(methodDoc.name()).append("--").whitespace()
                : out.append(methodDoc.name());
    }

    protected IndentingPrintWriter writeParametersTo(IndentingPrintWriter out) {
        if (diagram.config.includeMethodParams()) {
            String separator = "";
            for (Parameter parameter : methodDoc.parameters()) {
                out.append(separator);
                if (diagram.config.includeMethodParamNames()) {
                    out.append(parameter.name());
                    if (diagram.config.includeMethodParamTypes()) {
                        out.append(':');
                    }
                }
                if (diagram.config.includeMethodParamTypes()) {
                    out.append(parameter.type().simpleTypeName()).append(parameter.type().dimension());
                }
                separator = ", ";
            }
        }
        return out;
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        if (includeMethod()) {
            // deprecation:
            //        + --deprecatedString--(): String <<deprecated>>

            if (isAbstract()) {
                out.append("{abstract}").whitespace();
            }
            FieldRenderer.writeAccessibility(out, methodDoc);
            writeNameTo(out);
            writeParametersTo(out.append('(')).append(')');
            if (methodDoc instanceof MethodDoc && diagram.config.includeMethodReturntypes()) {
                writeTypeTo(out.append(':').whitespace(), ((MethodDoc) methodDoc).returnType());
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
        if (methodDoc instanceof MethodDoc && !diagram.config.includeOverridesFromExcludedReferences()) {
            ClassDoc overriddenClass = ((MethodDoc) methodDoc).overriddenClass();
            while (overriddenClass != null) {
                if (diagram.config.excludedReferences().contains(overriddenClass.qualifiedName())) {
                    LogSupport.trace("Method \"{0}{1}\" overrides method from excluded reference \"{2}\".",
                            methodDoc.qualifiedName(), methodDoc.flatSignature(), overriddenClass.qualifiedName());
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
