/*
 * Copyright 2016-2026 Talsma ICT
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

import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Character.isWhitespace;
import static java.util.Objects.requireNonNull;

/// Writer implementation that will indent each new line with a specified number of whitespace
/// characters (four by default).
/// The writing itself can be delegated to any other [Writer] implementation.
///
/// @author Sjoerd Talsma
public class IndentingWriter extends Writer {

    private final Appendable delegate;
    private final Indentation indentation;

    private final AtomicBoolean addWhitespace = new AtomicBoolean(false);
    private char lastWritten = '\n';

    protected IndentingWriter(Appendable delegate, Indentation indentation) {
        this(delegate, indentation, '\n', false);
        // maybe attempt to support extraction of 'lastWritten' from some types of writers?
    }

    private IndentingWriter(Appendable delegate, Indentation indentation, char lastWritten, boolean addWhitespace) {
        super(requireNonNull(delegate, "Delegate writer is required."));
        this.delegate = delegate;
        this.indentation = indentation == null ? Indentation.DEFAULT : indentation;
        this.lastWritten = lastWritten;
        this.addWhitespace.set(addWhitespace);
    }

    /// Returns an indenting writer around the given `delegate`.
    /// If the `delegate` writer is already an indenting writer, it will simply be returned
    /// [#withIndentation(Indentation) with the specified indentation].
    /// If the `delegate` writer is not yet an indending writer, a new indenting writer class will be created
    /// to wrap the delegate using the specified `indentation`.
    ///
    /// @param delegate    The delegate to turn into an indenting writer.
    /// @param indentation The indentation to use for the indenting writer
    ///                    (optional, specify `null` to use the default indentation).
    /// @return The indenting delegate writer.
    /// @see Indentation#DEFAULT
    public static IndentingWriter wrap(Appendable delegate, Indentation indentation) {
        return delegate instanceof IndentingWriter
                ? ((IndentingWriter) delegate).withIndentation(indentation)
                : new IndentingWriter(delegate, indentation);
    }

    /// Returns an indenting writer with the new indentation.
    ///
    /// Please note: Already written lines will not be modified to accomodate the new indentation.
    ///
    /// @param newIndentation The new indentation to apply to this writer (optional).
    /// @return Either this writer if the indentation is already correct,
    /// or a new IndentingWriter with the adapted indentation.
    public IndentingWriter withIndentation(Indentation newIndentation) {
        return newIndentation == null || this.indentation.equals(newIndentation) ? this
                : new IndentingWriter(delegate, newIndentation, lastWritten, addWhitespace.get());
    }

    /// @return The indentation used by this writer.
    protected Indentation getIndentation() {
        return indentation;
    }

    /// Returns a new indenting writer with the indentation level increased by one.
    ///
    /// @return A new indenting writer with increased indentation.
    public IndentingWriter indent() {
        return withIndentation(getIndentation().increase());
    }

    /// Returns a new indenting writer with the indentation level decreased by one.
    ///
    /// @return A new indenting writer with decreased indentation.
    public IndentingWriter unindent() {
        return withIndentation(getIndentation().decrease());
    }

    /// Makes sure there is at least one whitespace character between the last charater and the next.
    ///
    /// This method attempts to avoid appending a whitespace character if it knows the last character was in fact a
    /// whitespace character.
    /// The whitespace character will also not be written until there are other characters that need to be written.
    ///
    /// @return Reference to this writer for chaining purposes.
    public IndentingWriter whitespace() {
        addWhitespace.set(true);
        return this;
    }

    /// Tests whether the character is an end-of-line character.
    ///
    /// @param ch The character to be tested.
    /// @return `true` if the character was an end-of-line character, `false` otherwise.
    private static boolean isEol(char ch) {
        return ch == '\r' || ch == '\n';
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            char ch = cbuf[off];
            synchronized (lock) {
                if (addWhitespace.compareAndSet(true, false) && !isWhitespace(lastWritten) && !isWhitespace(ch)) {
                    delegate.append(' ');
                    lastWritten = ' ';
                }
                for (int i = 0; i < len; i++) {
                    ch = cbuf[off + i];
                    if (isEol(lastWritten) && !isEol(ch)) delegate.append(indentation);
                    delegate.append(ch);
                    lastWritten = ch;
                }
            }
        }
    }

    @Override
    public void flush() throws IOException {
        if (delegate instanceof Flushable) ((Flushable) delegate).flush();
    }

    @Override
    public void close() throws IOException {
        if (delegate instanceof AutoCloseable) try {
            ((AutoCloseable) delegate).close();
        } catch (IOException | RuntimeException rethrowable) {
            throw rethrowable;
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected exception closing " + this + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
