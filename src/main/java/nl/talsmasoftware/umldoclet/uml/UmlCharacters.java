/*
 * Copyright 2016-2026 Talsma ICT
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

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

/// A literal piece of UML.
///
/// @author Sjoerd Talsma
public class UmlCharacters extends UMLNode {
    /// Constant for empty characters.
    ///
    /// This can be used if a non-`null` [UMLNode] is required, but no output should be rendered
    public static final UmlCharacters EMPTY = new UmlCharacters("");
    /// UML node for a new line.
    public static final UmlCharacters NEWLINE = new UmlLine("");

    private final String content;

    private UmlCharacters(String content) {
        super(null);
        this.content = content;
    }

    /// Writes the literal UML characters to the output.
    ///
    /// @param output The output to write to.
    /// @return The output for chaining purposes.
    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append(content);
        return output;
    }

    private static class UmlLine extends UmlCharacters {
        private UmlLine(String line) {
            super(line);
        }

        /// Writes the contained characters to the output, followed by a newline.
        ///
        /// @param output The output to write to.
        /// @return The output for chaining purposes.
        @Override
        public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
            super.writeTo(output).newline();
            return output;
        }
    }
}
