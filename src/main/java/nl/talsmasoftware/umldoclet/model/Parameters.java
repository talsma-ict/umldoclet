/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.umldoclet.model;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.Renderer;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sjoerd Talsma
 */
public class Parameters extends UMLRenderer {

    private final List<Parameter> params = new ArrayList<>();

    public Parameters(Configuration config) {
        super(config);
    }

    public Parameters add(String name, TypeName type) {
        params.add(new Parameter(name, type));
        return this;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append('(');
        String sep = "";
        for (Parameter param : params) {
            param.writeTo(output.append(sep));
            sep = ", ";
        }
        output.append(')');
        return output;
    }

    private class Parameter implements Renderer {
        private final String name;
        private final TypeName type;

        private Parameter(String name, TypeName type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public <A extends Appendable> A writeTo(A output) {
            try {
                output.append(name).append(": ");
                type.writeTo(output, false, false);
                return output;
            } catch (IOException ioe) {
                throw new IllegalStateException("I/O exeption writing parameter \"" + name + "\": " + ioe.getMessage(), ioe);
            }
        }
    }
}
