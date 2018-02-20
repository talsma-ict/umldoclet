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

import nl.talsmasoftware.umldoclet.uml.configuration.Configuration;
import nl.talsmasoftware.umldoclet.rendering.Renderer;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingChildRenderer;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.io.StringWriter;
import java.util.Collection;

import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation for renderers.
 * <p>
 * Renderers are capable of rendering themselves to {@link IndentingPrintWriter} instances and have
 * chaining methods returning these writers for easier appending.
 *
 * @author Sjoerd Talsma
 */
abstract class UMLRenderer implements IndentingChildRenderer {

    protected final Configuration config;

    UMLRenderer(Configuration config) {
        this.config = requireNonNull(config, "Configuration is <null>.");
    }

    /**
     * To be overridden by renderers that actually have children.
     *
     * @return The children for this renderer.
     */
    @Override
    public Collection<? extends Renderer> getChildren() {
        return emptySet();
    }

    /**
     * Renders the entire content of this renderer and returns it as a String value.
     *
     * @return The rendered content of this renderer.
     */
    public String toString() {
        return writeTo(IndentingPrintWriter.wrap(new StringWriter(), config.getIndentation())).toString();
    }

}
