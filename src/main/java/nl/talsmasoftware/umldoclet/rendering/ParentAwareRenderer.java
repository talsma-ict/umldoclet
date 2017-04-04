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
package nl.talsmasoftware.umldoclet.rendering;

import java.util.Iterator;

import static java.util.Objects.requireNonNull;

/**
 * @author Sjoerd Talsma
 */
public abstract class ParentAwareRenderer extends Renderer {

    protected final Renderer parent;

    private volatile Renderer previousSibling, nextSibling;

    protected ParentAwareRenderer(Renderer parent) {
        super(requireNonNull(parent, "No parent renderer provided.").diagram);
        this.parent = parent;
    }

    /**
     * @return The renderer that is before this renderer within the parent, if any. Otherwise <code>null</code>.
     */
    protected Renderer getPreviousSibling() {
        if (previousSibling == null) for (Renderer current : parent.children) {
            if (equals(current)) break;
            else previousSibling = current;
        }
        return previousSibling;
    }

    /**
     * @return The renderer that is after this renderer within the parent, if any. Otherwise <code>null</code>.
     */
    protected Renderer getNextSibling() {
        if (nextSibling == null) for (Iterator<Renderer> it = parent.children.iterator(); it.hasNext(); ) {
            if (equals(it.next()) && it.hasNext()) {
                nextSibling = it.next();
                break;
            }
        }
        return nextSibling;
    }

    public boolean equals(Object other) {
        return this == other;
    }

}
