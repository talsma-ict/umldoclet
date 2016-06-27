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

import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Iterator;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class SeparatorRenderer extends Renderer {

    // TODO Think about how to maintain state between what was just rendered
    // and what still needs to be rendered

    protected final Renderer parent;
    protected final String separator;

    private volatile Renderer previousSibling, nextSibling;

    protected SeparatorRenderer(Renderer parent, String separator) {
        super(parent.diagram);
        this.parent = parent;
        this.separator = separator;
    }

    /**
     * @return The renderer that is before this renderer within the parent, if any. Otherwise <code>null</code>.
     */
    protected Renderer previousSibling() {
        if (previousSibling == null) for (Renderer current : parent.children) {
            if (equals(current)) break;
            else previousSibling = current;
        }
        return previousSibling;
    }

    /**
     * @return The renderer that is after this renderer within the parent, if any. Otherwise <code>null</code>.
     */
    protected Renderer nextSibling() {
        if (nextSibling == null) for (Iterator<Renderer> it = parent.children.iterator(); it.hasNext(); ) {
            if (equals(it.next()) && it.hasNext()) {
                nextSibling = it.next();
                break;
            }
        }
        return nextSibling;
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter output) {
        return output.append(separator).newline();
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

}
