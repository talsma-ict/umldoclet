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
import java.util.Arrays;

import static java.lang.Character.isWhitespace;
import static java.util.Objects.requireNonNull;

/**
 * Writer implementation that will indent each new line with a specified number of whitespace
 * characters. The writing itself can be delegated to any other {@link Writer} implementation.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class IndentingWriter extends Writer {

    public static final int DEFAULT_INDENTATION_WIDTH = 4;
    private static final String EOL_CHARS = "\r\n";
    private static final char[] NO_INDENTATION = {};

    private final Writer delegate;
    protected final int indentationWidth;
    private final char[] indentation;

    private boolean wasWhitespace, isBeginningOfLine;

    protected IndentingWriter(Writer delegate, int indentationWidth) {
        this(delegate, indentationWidth, null, true, true);
    }

    private IndentingWriter(Writer delegate, int indentationWidth, char[] indentation, boolean wasWhitespace, boolean isBeginningOfLine) {
        this.delegate = requireNonNull(delegate, "Delegate writer is required.");
        this.indentationWidth = indentationWidth < 0 ? DEFAULT_INDENTATION_WIDTH : indentationWidth;
        this.indentation = indentation == null ? NO_INDENTATION : indentation;
        this.wasWhitespace = wasWhitespace;
        this.isBeginningOfLine = isBeginningOfLine;
    }

    /**
     * Returns an indenting writer around the given {@code delegate}.
     * If the {@code delegate} writer is already an indenting writer, it will simply be returned as-is.
     * If the {@code delegate} writer is not yet an indending writer, a new indenting writer class will be created to
     * wrap the delegate using the {@link #DEFAULT_INDENTATION_WIDTH} and no initial {@link #indentationLevel()}.
     *
     * @param delegate The delegate to turn into an indenting writer.
     * @return The indenting delegate writer.
     */
    public static IndentingWriter wrap(Writer delegate) {
        return delegate instanceof IndentingWriter
                ? (IndentingWriter) delegate
                : new IndentingWriter(delegate, -1);
    }

    /**
     * Returns an indenting writer with the new indentation width.
     * Please note: already written lines will not be modified to accomodate the new indentation width.
     * Negative indentation widths will have no effect; the same indentation writer will be returned.
     *
     * @param newIndentationWidth The new indentation width to use on the indenting delegate.
     * @return Either this writer if the indentation width is already equal to the requested new width,
     * or a new IndentingWriter with the specified width otherwise.
     */
    public IndentingWriter withIndentationWidth(int newIndentationWidth) {
        return newIndentationWidth < 0 || indentationWidth == newIndentationWidth ? this :
                new IndentingWriter(delegate, newIndentationWidth, indentation, wasWhitespace, isBeginningOfLine)
                        .withIndentationLevel(indentationLevel());
    }

    public IndentingWriter withIndentationLevel(int newIndentationLevel) {
        if (newIndentationLevel < 0) {
            throw new IllegalArgumentException(String.format("Indentation level cannot be a negative value: %s.", newIndentationLevel));
        } else if (indentationLevel() == newIndentationLevel) {
            return this;
        }
        final char[] newIndentation = new char[newIndentationLevel * indentationWidth];
        Arrays.fill(newIndentation, ' ');
        return new IndentingWriter(delegate, indentationWidth, newIndentation, wasWhitespace, isBeginningOfLine);
    }

    protected int indentationWidth() {
        return indentationWidth;
    }

    /**
     * @return The current indentation level (in concrete steps) of this indenting delegate writer.
     */
    protected int indentationLevel() {
        return indentationWidth == 0 ? 0 : indentation.length / indentationWidth;
    }

    public IndentingWriter indent() {
        return withIndentationLevel(Math.max(0, indentationLevel() + 1));
    }

    public IndentingWriter unindent() {
        return withIndentationLevel(Math.max(0, indentationLevel() - 1));
    }

    /**
     * Makes sure there is at least one whitespace character between the last charater and the next.
     * <p/>
     * This method attempts to avoid appending a whitespace character if it knows the last character was in fact a
     * whitespace character.
     *
     * @return Reference to this writer for chaining purposes.
     * @throws IOException
     */
    public IndentingWriter whitespace() throws IOException {
        return wasWhitespace ? this : (IndentingWriter) append(' ');
    }

    /**
     * Tests whether the character is an end-of-line character.
     *
     * @param ch The character to be tested.
     * @return {@code true} if the character was an end-of-line character, {@code false} otherwise.
     */
    private static boolean isEol(char ch) {
        return EOL_CHARS.indexOf(ch) >= 0;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            synchronized (lock) {
                for (int i = off; i < len; i++) {
                    final boolean isEol = isEol(cbuf[i]);
                    if (isBeginningOfLine && !isEol) {
                        delegate.write(indentation);
                        isBeginningOfLine = false;
                    }
                    delegate.write(cbuf[i]);
                    wasWhitespace = isWhitespace(cbuf[i]);
                    isBeginningOfLine = isEol;
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
