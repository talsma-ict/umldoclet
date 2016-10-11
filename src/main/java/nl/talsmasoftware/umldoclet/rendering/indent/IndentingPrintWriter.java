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
import java.io.PrintWriter;
import java.io.Writer;

/**
 * PrintWriter implementation that will indent each new line with a specified number of whitespace
 * characters. The writing itself can be delegated to any other {@link Writer} implementation.
 * <p/>
 * Care was taken to ensure that not only lines ended by calls to {@link #println()} methods trigger indentation,
 * but self-written newline characters as well.
 *
 * @author Sjoerd Talsma
 */
public class IndentingPrintWriter extends PrintWriter {

    protected IndentingPrintWriter(Writer writer, int indentationWidth) {
        this(IndentingWriter.wrap(writer).withIndentationWidth(indentationWidth));
    }

    private IndentingPrintWriter(IndentingWriter delegate) {
        super(delegate);
    }

    /**
     * Returns an indenting printwriter around the given {@code delegate}.
     * If the {@code delegate} printwriter is already an indenting printwriter, it will simply be returned as-is.
     * If the {@code delegate} printwriter is not yet an indending printwriter, a new indenting printwriter class
     * will be created to wrap the delegate using the {@link IndentingWriter}'s {@code default indentation width}
     * and no initial {@link IndentingWriter#indentationLevel()}.
     *
     * @param delegate The delegate to turn into an indenting printwriter.
     * @return The indenting delegate writer.
     */
    public static IndentingPrintWriter wrap(Writer delegate) {
        return delegate instanceof IndentingPrintWriter
                ? (IndentingPrintWriter) delegate
                : new IndentingPrintWriter(delegate, -1);
    }

    private IndentingPrintWriter changeIndentation(final boolean up) {
        if (out instanceof IndentingWriter) {
            IndentingWriter delegate = (IndentingWriter) this.out;
            int newIndentationLevel = Math.max(0, delegate.indentationLevel() + (up ? 1 : -1));
            delegate = delegate.withIndentationLevel(newIndentationLevel);
            if (!out.equals(delegate)) {
                return new IndentingPrintWriter(delegate);
            }
        }
        return this;
    }

    public IndentingPrintWriter indent() {
        return changeIndentation(true);
    }

    public IndentingPrintWriter unindent() {
        return changeIndentation(false);
    }

    public IndentingPrintWriter whitespace() {
        try {
            if (out instanceof IndentingWriter) {
                ((IndentingWriter) out).whitespace();
            } else {
                out.append(' ');
            }
            return this;
        } catch (IOException ioe) {
            throw new IllegalStateException("Error writing whitespace: " + ioe.getMessage(), ioe);
        }
    }

    public IndentingPrintWriter newline() {
        super.println();
        return this;
    }

    @Override
    public IndentingPrintWriter append(CharSequence csq) {
        return (IndentingPrintWriter) super.append(csq);
    }

    @Override
    public IndentingPrintWriter append(CharSequence csq, int start, int end) {
        return (IndentingPrintWriter) super.append(csq, start, end);
    }

    @Override
    public IndentingPrintWriter append(char c) {
        return (IndentingPrintWriter) super.append(c);
    }

    @Override
    public String toString() {
        return out.toString();
    }
}
