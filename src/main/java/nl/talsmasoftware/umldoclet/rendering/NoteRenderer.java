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

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Renderer for UML notes.
 *
 * @author Sjoerd Talsma
 */
public class NoteRenderer extends ParentAwareRenderer {

    protected final String note;
    private final String position = "bottom";

    protected NoteRenderer(Renderer parent, String note) {
        super(parent);
        this.note = requireNonNull(note, "Note to render may not be <null>!").trim();
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        output.append("note");
        if (parent instanceof ClassRenderer) {
            output.whitespace().append(position)
                    .whitespace().append("of")
                    .whitespace().append(((ClassRenderer) parent).name());
        }
        output.newline().indent().append(note).newline();
        return output.append("end note").newline().newline();
    }

}
