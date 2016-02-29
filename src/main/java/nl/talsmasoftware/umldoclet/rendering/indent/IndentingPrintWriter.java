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

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class IndentingPrintWriter extends PrintWriter {

    protected IndentingPrintWriter(Writer writer, int indentationWidth) {
        this(IndentingDelegateWriter.wrap(writer).withIndentationWidth(indentationWidth));
    }

    private IndentingPrintWriter(IndentingDelegateWriter delegate) {
        super(delegate);
    }

    /**
     * Returns an indenting printwriter around the given {@code delegate}.
     * If the {@code delegate} printwriter is already an indenting printwriter, it will simply be returned as-is.
     * If the {@code delegate} printwriter is not yet an indending printwriter, a new indenting printwriter class
     * will be created to wrap the delegate using the {@link IndentingDelegateWriter#DEFAULT_INDENTATION_WIDTH}
     * and no initial {@link IndentingDelegateWriter#currentIndentationLevel()}.
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
        if (out instanceof IndentingDelegateWriter) {
            IndentingDelegateWriter delegate = (IndentingDelegateWriter) this.out;
            int newIndentationLevel = Math.max(0, delegate.currentIndentationLevel() + (up ? 1 : -1));
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
