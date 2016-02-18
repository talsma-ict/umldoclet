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

import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class MethodRenderer extends Renderer {

    private final MethodDoc methodDoc;

    public MethodRenderer(UMLDocletConfig config, MethodDoc methodDoc) {
        super(config);
        this.methodDoc = requireNonNull(methodDoc, "No method documentation provided.");
    }

    public static IndentingPrintWriter writeParametersTo(IndentingPrintWriter out, ExecutableMemberDoc method) {
        String separator = "";
        for (Parameter parameter : method.parameters()) {
            out.append(separator).append(parameter.name()).append(":").append(parameter.type().simpleTypeName());
            separator = ", ";
        }
        return out;
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        FieldRenderer.writeAccessibility(out, methodDoc)
                .append(methodDoc.name()).append("(");
        return writeParametersTo(out, methodDoc).append("): ").append(methodDoc.returnType().typeName()).newline();
    }

}
