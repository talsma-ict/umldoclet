/*
 * Copyright 2016-2019 Talsma ICT
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
package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import nl.talsmasoftware.umldoclet.uml.UMLNode;

/**
 * A literal piece of UML.
 *
 * @author Sjoerd Talsma
 */
class UmlCharacters extends UMLNode {
    static final UmlCharacters EMPTY = new UmlCharacters("");
    static final UmlCharacters NEWLINE = new UmlLine("");

    private final String content;

    private UmlCharacters(String content) {
        super(null);
        this.content = content;
    }

    @Override
    public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        output.append(content);
        return output;
    }

    static class UmlLine extends UmlCharacters {
        private UmlLine(String line) {
            super(line);
        }

        @Override
        public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
            super.writeTo(output).newline();
            return output;
        }
    }
}
