/*
 * Copyright 2016-2024 Talsma ICT
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
package nl.talsmasoftware.umldoclet.rendering.writers;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Delegates to another {@link Writer} retaining a {@link StringBuffer} of all written characters.
 *
 * <p>
 * Manipulating the contained StringBuffer is not thread-safe.
 *
 * @author Sjoerd Talsma
 */
public class StringBufferingWriter extends DelegatingWriter {

    /**
     * Constructor. Creates a new writer that delegates to the given writer and also retains a
     * {@link StringBuffer} of all written characters.
     *
     * @param delegate The delegate writer to write to.
     */
    public StringBufferingWriter(Writer delegate) {
        super(new StringWriter(), delegate);
    }

    /**
     * A buffer of the written characters.
     *
     * <p>
     * Changes to this buffer do not propagate towards the delegate writer.
     * Furthermore, write operations on this writer and buffer changes are not considered
     * thread-safe and should be avoided.
     *
     * @return A StringBuffer of the written characters.
     */
    public StringBuffer getBuffer() {
        return ((StringWriter) delegates.get(0)).getBuffer();
    }

    /**
     * @return The name of this class plus the wrapped delegate writer.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' + delegates.get(1) + '}';
    }

}
