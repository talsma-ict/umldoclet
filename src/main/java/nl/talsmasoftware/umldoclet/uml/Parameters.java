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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.configuration.MethodConfig;
import nl.talsmasoftware.umldoclet.configuration.TypeDisplay;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Iterator;

/**
 * @author Sjoerd Talsma
 */
public class Parameters extends UMLPart implements Comparable<Parameters> {

    private boolean varargs = false;
    private Method method;

    public Parameters() {
        super(null);
    }

    void setMethod(Method method) {
        this.method = method; // TODO: refactor this 'hack' away!
    }

    @Override
    protected UMLPart requireParent() {
        return method == null ? super.requireParent() : method;
    }

    @Override
    public void addChild(UMLPart child) {
        if (child instanceof Parameter) super.addChild(child);
    }

    public Parameters add(String name, TypeName type) {
        addChild(new Parameter(name, type));
        return this;
    }

    public Parameters varargs(boolean varargs) {
        this.varargs = varargs;
        return this;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        return writeChildrenTo(output);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        output.append('(');
        String sep = "";
        for (UMLPart param : getChildren()) {
            param.writeTo(output.append(sep));
            sep = ", ";
        }
        output.append(')');
        return output;
    }

    @Override
    public int compareTo(Parameters other) {
        int delta = Integer.compare(this.getChildren().size(), other.getChildren().size());
        for (Iterator<UMLPart> ours = this.getChildren().iterator(), theirs = other.getChildren().iterator();
             delta == 0 && ours.hasNext() && theirs.hasNext(); ) {
            delta = ((Parameter) ours.next()).type.compareTo(((Parameter) theirs.next()).type);
        }
        return delta;
    }

    private class Parameter extends UMLPart {
        private final String name;
        private final TypeName type;

        private Parameter(String name, TypeName type) {
            super(Parameters.this);
            this.name = name;
            this.type = type;
        }

        @Override
        public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
            String sep = "";
            MethodConfig methodConfig = getConfiguration().methods();
            if (name != null && MethodConfig.ParamNames.BEFORE_TYPE.equals(methodConfig.paramNames())) {
                output.append(name);
                sep = ": ";
            }
            if (type != null && !TypeDisplay.NONE.equals(methodConfig.paramTypes())) {
                String typeUml = type.toUml(methodConfig.paramTypes(), null);
                if (varargs && typeUml.endsWith("[]")) typeUml = typeUml.substring(0, typeUml.length() - 2) + "...";
                output.append(sep).append(typeUml);
                sep = " ";
            }
            if (name != null && MethodConfig.ParamNames.AFTER_TYPE.equals(methodConfig.paramNames())) {
                output.append(sep).append(name);
            }
            return output;
        }
    }
}
