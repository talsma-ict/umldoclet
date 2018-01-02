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
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingRenderer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public class Package extends UMLRenderer implements IndentingRenderer.WithChildren {

    public final String name;
    private final Set<Renderer> children = new LinkedHashSet<>();

    public Package(Configuration config, String name) {
        super(config);
        this.name = requireNonNull(name, "Package name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Package name is empty.");
    }

    @Override
    public Collection<? extends Renderer> getChildren() {
        return children;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append("namespace").whitespace().append(name);
        writeChildrenTo(output.whitespace().append('{').newline()).newline().append('}');
        return output;
    }

}
