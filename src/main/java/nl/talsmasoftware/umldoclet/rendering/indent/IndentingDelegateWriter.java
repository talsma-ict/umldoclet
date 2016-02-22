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

    public IndentingDelegateWriter(Writer delegate) {
        this(delegate, -1);
    }

    public IndentingDelegateWriter(Writer delegate, int indentationWidth) {
        this(delegate, indentationWidth, null, true);
    }

    public static IndentingDelegateWriter wrap(Writer writer) {
        return writer instanceof IndentingDelegateWriter
                ? (IndentingDelegateWriter) writer
                : new IndentingDelegateWriter(writer);
    }

    private IndentingDelegateWriter(Writer delegate, int indentationWidth, char[] indentation, boolean isBeginningOfLine) {
        this.delegate = requireNonNull(delegate, "Delegate writer is required.");
        this.indentationWidth = indentationWidth < 0 ? DEFAULT_INDENTATION_WIDTH : indentationWidth;
        this.indentation = indentation == null ? NO_INDENTATION : indentation;
        this.isBeginningOfLine = isBeginningOfLine;
    }

    public int indentationWith() {
        return indentationWidth;
    }

    public int currentIndentationLevel() {
        return indentationWidth == 0 ? 0 : indentation.length / indentationWidth;
    }

    /**
     * Geeft een {@link IndentingDelegateWriter} terug met het level aangepast met de opgegeven delta.
     *
     * <p>
     * Level verlaging naar negatieve indentatie zal niet worden uitgevoerd.`
     *
     * @param levelChange De verhoging of verlaging van het indentation level (meestal {@code +1} of {@code -1}).
     * @return De writer met het aangepaste indent level.
     * @see #indent()
     * @see #unindent()
     */
    public IndentingDelegateWriter indent(final int levelChange) {
        final int newSize = indentation.length + levelChange * indentationWidth;
        if (indentation.length == newSize) {
            return this;
        } else if (newSize > 0) {
            final char[] newIndentation = new char[newSize];
            Arrays.fill(newIndentation, ' ');
            return new IndentingDelegateWriter(delegate, indentationWidth, newIndentation, isBeginningOfLine);
        }
        return new IndentingDelegateWriter(delegate, indentationWidth, NO_INDENTATION, isBeginningOfLine);
    }

    public IndentingDelegateWriter indent() {
        return indent(1);
    }

    public IndentingDelegateWriter unindent() {
        return indent(-1);
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
