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

    public IndentingPrintWriter(Writer writer, int indentationWidth) {
        this(new IndentingDelegateWriter(writer, indentationWidth));
    }

    private IndentingPrintWriter(IndentingDelegateWriter delegate) {
        super(delegate);
    }

    /**
     * @param levelChange De wijziging in het indentatie niveau (zal meestal {@code +1} of {@code -1} zijn).
     * @return De printwriter met aangepaste indentatie.
     * @see IndentingDelegateWriter#indent(int)
     */
    public IndentingPrintWriter indent(final int levelChange) {
        IndentingDelegateWriter indented = ((IndentingDelegateWriter) out).indent(levelChange);
        return out.equals(indented) ? this : new IndentingPrintWriter(indented);
    }

    public IndentingPrintWriter indent() {
        return indent(1);
    }

    public IndentingPrintWriter unindent() {
        return indent(-1);
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
