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
package nl.talsmasoftware.umldoclet.rendering.indent;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Character.isWhitespace;
import static java.util.Objects.requireNonNull;

/**
 * Writer implementation that will indent each new line with a specified number of whitespace
 * characters (four by default).
 * The writing itself can be delegated to any other {@link Writer} implementation.
 *
 * @author Sjoerd Talsma
 */
public class IndentingWriter extends Writer {

    private final Writer delegate;
    private final Indentation indentation;

    private final AtomicBoolean addWhitespace = new AtomicBoolean(false);
    private char lastWritten = '\n';

    protected IndentingWriter(Writer delegate, Indentation indentation) {
        super(requireNonNull(delegate, "Delegate writer is required."));
        this.delegate = delegate;
        this.indentation = indentation != null ? indentation : Indentation.DEFAULT;
        // maybe attempt to support extraction of 'lastWritten' from some types of writers?
    }

    private IndentingWriter(Writer delegate, Indentation indentation, char lastWritten, boolean addWhitespace) {
        this(delegate, indentation);
        this.lastWritten = lastWritten;
        this.addWhitespace.set(addWhitespace);
    }

    /**
     * Returns an indenting writer around the given <code>delegate</code>.<br>
     * If the <code>delegate</code> writer is already an indenting writer, it will simply be returned
     * {@link #withIndentation(Indentation) with the specified indentation}.<br>
     * If the <code>delegate</code> writer is not yet an indending writer, a new indenting writer class will be created
     * to wrap the delegate using the specified <code>indentation</code>.
     *
     * @param delegate    The delegate to turn into an indenting writer.
     * @param indentation The indentation to use for the indenting writer
     *                    (optional, specify <code>null</code> to use the default indentation).
     * @return The indenting delegate writer.
     * @see Indentation#DEFAULT
     */
    public static IndentingWriter wrap(Writer delegate, Indentation indentation) {
        return delegate instanceof IndentingWriter ? ((IndentingWriter) delegate).withIndentation(indentation)
                : new IndentingWriter(delegate, indentation);
    }

    /**
     * Returns an indenting writer with the new indentation.
     * <p>
     * Please note: Already written lines will not be modified to accomodate the new indentation.
     *
     * @param newIndentation The new indentation to apply to this writer (optional).
     * @return Either this writer if the indentation is already correct,
     * or a new IndentingWriter with the adapted indentation.
     */
    public IndentingWriter withIndentation(Indentation newIndentation) {
        return newIndentation == null || this.indentation.equals(newIndentation) ? this
                : new IndentingWriter(delegate, newIndentation, lastWritten, addWhitespace.get());
    }

    protected Indentation getIndentation() {
        return indentation;
    }

    public IndentingWriter indent() {
        return withIndentation(getIndentation().increase());
    }

    public IndentingWriter unindent() {
        return withIndentation(getIndentation().decrease());
    }

    /**
     * Makes sure there is at least one whitespace character between the last charater and the next.
     * <p>
     * This method attempts to avoid appending a whitespace character if it knows the last character was in fact a
     * whitespace character.
     * The whitespace character will also not be written until there are other characters that need to be written.
     *
     * @return Reference to this writer for chaining purposes.
     * @throws IOException
     */
    public IndentingWriter whitespace() throws IOException {
        addWhitespace.set(true);
        return this;
    }

    /**
     * Tests whether the character is an end-of-line character.
     *
     * @param ch The character to be tested.
     * @return <code>true</code> if the character was an end-of-line character, <code>false</code> otherwise.
     */
    private static boolean isEol(char ch) {
        return ch == '\r' || ch == '\n';
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            synchronized (lock) {
                if (addWhitespace.compareAndSet(true, false) && !isWhitespace(lastWritten) && !isWhitespace(cbuf[0])) {
                    delegate.write(' ');
                }
                for (int i = off; i < len; i++) {
                    if (isEol(lastWritten) && !isEol(cbuf[i])) indentation.writeTo(delegate);
                    delegate.write(cbuf[i]);
                    lastWritten = cbuf[i];
                }
            }
        }
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
