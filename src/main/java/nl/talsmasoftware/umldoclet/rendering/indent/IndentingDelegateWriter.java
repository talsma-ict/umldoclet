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

import static java.util.Objects.requireNonNull;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class IndentingDelegateWriter extends Writer {

    public static final int DEFAULT_INDENTATION_WIDTH = 4;
    private static final String EOL_CHARS = "\r\n";
    private static final char[] NO_INDENTATION = {};

    private final Writer delegate;
    protected final int indentationWidth;
    private final char[] indentation;

    private boolean isBeginningOfLine;

    protected IndentingDelegateWriter(Writer delegate, int indentationWidth) {
        this(delegate, indentationWidth, null, true);
    }

    private IndentingDelegateWriter(Writer delegate, int indentationWidth, char[] indentation, boolean isBeginningOfLine) {
        this.delegate = requireNonNull(delegate, "Delegate writer is required.");
        this.indentationWidth = indentationWidth < 0 ? DEFAULT_INDENTATION_WIDTH : indentationWidth;
        this.indentation = indentation == null ? NO_INDENTATION : indentation;
        this.isBeginningOfLine = isBeginningOfLine;
    }

    /**
     * Returns an indenting writer around the given {@code delegate}.
     * If the {@code delegate} writer is already an indenting writer, it will simply be returned as-is.
     * If the {@code delegate} writer is not yet an indending writer, a new indenting writer class will be created to
     * wrap the delegate using the {@link #DEFAULT_INDENTATION_WIDTH} and no initial {@link #currentIndentationLevel()}.
     *
     * @param delegate The delegate to turn into an indenting writer.
     * @return The indenting delegate writer.
     */
    public static IndentingDelegateWriter wrap(Writer delegate) {
        return delegate instanceof IndentingDelegateWriter
                ? (IndentingDelegateWriter) delegate
                : new IndentingDelegateWriter(delegate, -1);
    }

    /**
     * Returns an indenting writer with the new indentation width.
     * Please note: already written lines will not be modified to accomodate the new indentation width.
     * Negative indentation widths will have no effect; the same indentation writer will be returned.
     *
     * @param newIndentationWidth
     * @return
     */
    public IndentingDelegateWriter withIndentationWidth(int newIndentationWidth) {
        return newIndentationWidth < 0 || indentationWidth == newIndentationWidth ? this :
                new IndentingDelegateWriter(delegate, newIndentationWidth, indentation, isBeginningOfLine)
                        .withIndentationLevel(currentIndentationLevel());
    }

    public IndentingDelegateWriter withIndentationLevel(int newIndentationLevel) {
        if (newIndentationLevel < 0) {
            throw new IllegalArgumentException(String.format("Indentation level cannot be a negative value: %s.", newIndentationLevel));
        } else if (currentIndentationLevel() == newIndentationLevel) {
            return this;
        }
        final char[] newIndentation = new char[newIndentationLevel * indentationWidth];
        Arrays.fill(newIndentation, ' ');
        return new IndentingDelegateWriter(delegate, indentationWidth, newIndentation, isBeginningOfLine);
    }

    public int indentationWith() {
        return indentationWidth;
    }

    public int currentIndentationLevel() {
        return indentationWidth == 0 ? 0 : indentation.length / indentationWidth;
    }

    public IndentingDelegateWriter indent() {
        return withIndentationLevel(Math.max(0, currentIndentationLevel() + 1));
    }

    public IndentingDelegateWriter unindent() {
        return withIndentationLevel(Math.max(0, currentIndentationLevel() - 1));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len > 0) {
            synchronized (lock) {
                if (isBeginningOfLine) {
                    delegate.write(indentation);
                    isBeginningOfLine = false;
                }
                delegate.write(cbuf, off, len);
                isBeginningOfLine = EOL_CHARS.indexOf(cbuf[off + len - 1]) >= 0;
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
