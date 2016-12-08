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
import nl.talsmasoftware.umldoclet.logging.GlobalPosition;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static java.lang.Character.toLowerCase;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.logging.LogSupport.*;
import static nl.talsmasoftware.umldoclet.model.Model.isDeprecated;

/**
 * Method renderer.
 * <p>
 * For the moment this renderer is also used for rendering Constructors.
 * If this turns out to be too complex, constructors may be separated into their own specialized renderer class.
 *
 * @author Sjoerd Talsma
 */
public class MethodRenderer extends Renderer {
    private static final Set<String> IMPLICIT_ENUM_METHODS = unmodifiableSet(new LinkedHashSet<>(asList(
            "values()", "valueOf(String)")));

    protected final ExecutableMemberDoc methodDoc;
    boolean disabled = false;

    protected MethodRenderer(DiagramRenderer diagram, ExecutableMemberDoc methodDoc) {
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

        if (isTraceEnabled()) {
            final String designation = concatLowercaseParts(
                    isDeprecated(methodDoc) ? "Deprecated" : null,
                    isAbstract() ? "Abstract" : null,
                    methodDoc.isStatic() ? "Static" : null,
                    isDefaultConstructor() ? "Default" : null,
                    isConstructor() ? "Constructor" : "Method");

            trace("{0} \"{1}{2}\" {3}{4}.",
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
                if (diagram.config.includeMethodParamNames()) {
                    out.append(separator).append(parameter.name());
                    if (diagram.config.includeMethodParamTypes()) {
                        writeTypeTo(out.append(':'), parameter.type());
                    }
                    separator = ", ";
                } else if (diagram.config.includeMethodParamTypes()) {
                    writeTypeTo(out.append(separator), parameter.type());
                    separator = ", ";
                }
            }
        }
        return out;
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        try (GlobalPosition gp = new GlobalPosition(methodDoc.position())) {
            if (includeMethod() && !disabled) {
                // if (disabled) out.append("' ");
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
     * @return The name of the property this method is either a getter or a setter for,
     * or <code>null</code> if this method is not a property accessor.
     */
    protected String propertyName() {
        if (!methodDoc.isStatic() && methodDoc instanceof MethodDoc) {
            String name = methodDoc.name();
            int len = name != null ? name.length() : 0;
            char[] propname = null;

            if (len > 3 && name.startsWith("get") && methodDoc.parameters().length == 0) { // prop getXyz()
                propname = name.substring(3).toCharArray();
            } else if (len > 3 && name.startsWith("set") && methodDoc.parameters().length == 1) { // setXyz(prop)
                propname = name.substring(3).toCharArray();
            } else if (len > 2 && name.startsWith("is") && methodDoc.parameters().length == 0) { // boolean isXyz()
                propname = name.substring(2).toCharArray();
            }
            if (propname != null && propname.length > 0) {
                propname[0] = toLowerCase(propname[0]);
                return String.valueOf(propname);
            }
        }
        return null;
    }

    protected Type propertyType() {
        Type propertyType = null;
        if (propertyName() != null) {
            propertyType = methodDoc.name().startsWith("set") ? methodDoc.parameters()[0].type()
                    : ((MethodDoc) methodDoc).returnType();
        }
        return propertyType;
    }

    /**
     * @return Whether overridden methods from excluded classes (such as java.lang.Object normally)
     * and this method happens to be such a method.
     */
    private boolean isMethodFromExcludedClass() {
        if (methodDoc instanceof MethodDoc && !diagram.config.includeOverridesFromExcludedReferences()) {
            if (isImplicitStaticEnumMethod() && diagram.config.excludedReferences().contains(Enum.class.getName())) {
                return true;
            }

            final MethodDoc md = (MethodDoc) methodDoc;
            Type originatingType = md.overriddenType() != null ? md.overriddenType() : md.containingClass();
            while (originatingType instanceof ClassDoc) {
                final ClassDoc originatingClass = (ClassDoc) originatingType;

                if (diagram.config.excludedReferences().contains(originatingClass.qualifiedName())) {
                    trace("Method \"{0}{1}\" overrides method from excluded type \"{2}\".",
                            methodDoc.qualifiedName(), methodDoc.flatSignature(), originatingClass.qualifiedName());
                    return true;
                }

                MethodDoc foundMethod = findMethod(originatingClass, methodDoc.name(), methodDoc.flatSignature());
                originatingType = foundMethod != null && !originatingClass.equals(foundMethod.overriddenType())
                        ? foundMethod.overriddenType() : null;
            }
        }
        return false;
    }

    private boolean isImplicitStaticEnumMethod() {
        if (methodDoc.isStatic() && methodDoc.containingClass().isEnum() && methodDoc instanceof MethodDoc) {
            boolean implitEnumMethod = IMPLICIT_ENUM_METHODS.contains(methodDoc.name() + methodDoc.flatSignature());
            trace("Method \"{0}{1}\" {2} an implicit static Enum method.",
                    methodDoc.qualifiedName(), methodDoc.flatSignature(), implitEnumMethod ? "is" : "is not");
            return implitEnumMethod;
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
