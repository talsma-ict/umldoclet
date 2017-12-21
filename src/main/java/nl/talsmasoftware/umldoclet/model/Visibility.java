/*
 * Copyright 2016-2017 Talsma ICT
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

import nl.talsmasoftware.umldoclet.rendering.Renderer;

import java.io.IOException;

/**
 * The visibility values of members in UML diagrams.
 *
 * @author Sjoerd Talsma
 */
public enum Visibility implements Renderer {
    PRIVATE('-'),
    PROTECTED('#'),
    PACKAGE_PRIVATE('~'),
    PUBLIC('+');

    private final char umlVisibility;

    Visibility(char umlVisibility) {
        this.umlVisibility = umlVisibility;
    }

    @Override
    public <A extends Appendable> A writeTo(A output) {
        try {
            output.append(umlVisibility);
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O error writing visibility "
                    + name().toLowerCase().replace('_', ' ') + " to the output: "
                    + ioe.getMessage(), ioe);
        }
        return output;
    }

}
