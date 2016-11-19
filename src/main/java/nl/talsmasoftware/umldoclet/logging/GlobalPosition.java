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
package nl.talsmasoftware.umldoclet.logging;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SourcePosition;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * For setting a source position within a try-with-resources code block.
 *
 * @author Sjoerd Talsma
 */
public class GlobalPosition implements Closeable {
    private static final ThreadLocal<GlobalPosition> GLOBAL_POSITION = new ThreadLocal<>();

    private final GlobalPosition prev = GLOBAL_POSITION.get();
    private final SourcePosition pos;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Initiates a new global source position based on the specified javadoc element.
     * This global position will be active until it is closed or another global position is initiated (although closing
     * that will restore this position).<br>
     * Because it is important to always open / close in a stack-like fashion, it is advised to only create new
     * <code>GlobalPosition</code> objects within try-with-resources constructions. This ensures proper unfolding of
     * the global position even in case of exceptions.
     *
     * @param doc The javadoc element to be used as current global position.
     */
    public GlobalPosition(Doc doc) {
        this(doc != null ? doc.position() : null);
    }

    /**
     * Only sets a new global position if the source position is non-null.
     *
     * @param pos The new 'current' global position (<code>null</code> will be ignored).
     */
    public GlobalPosition(SourcePosition pos) {
        this.pos = pos;
        if (pos != null) setPos(this);
    }

    /**
     * @return The current source position in this doclet or <code>null</code> if unknown.
     */
    public static SourcePosition current() {
        final GlobalPosition current = GLOBAL_POSITION.get();
        return current != null ? current.pos : null;
    }

    private void setPos(GlobalPosition pos) {
        if (pos == null) {
            GLOBAL_POSITION.remove();
        } else {
            GLOBAL_POSITION.set(pos);
        }
    }

    public void close() {
        // Close only once:
        if (closed.compareAndSet(false, true)) setPos(prev);
    }

    public String toString() {
        return getClass().getSimpleName() + "{closed: " + closed + ", pos: " + pos + '}';
    }
}
