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
 *
 */

package nl.talsmasoftware.umldoclet.rendering;

import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Objects;

/**
 * This could well be the simplest renderer of them all; it simply renders the command that was
 * given on the appropriate position in the output.
 *
 * @author Sjoerd Talsma
 */
public class CommandRenderer extends Renderer {
    protected final String command;

    protected CommandRenderer(DiagramRenderer currentDiagram, String command) {
        super(currentDiagram);
        this.command = Objects.requireNonNull(command, "No command provided.");
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        LogSupport.trace("Writing command \"{0}\"...", command);
        return output.append(command).newline();
    }

    @Override
    public int hashCode() {
        return Objects.hash(command);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof CommandRenderer && Objects.equals(command, ((CommandRenderer) other).command));
    }
}
