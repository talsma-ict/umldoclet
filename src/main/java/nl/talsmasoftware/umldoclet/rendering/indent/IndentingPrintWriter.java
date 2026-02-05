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

import java.io.IOException;
import java.io.PrintWriter;

import static java.util.Objects.requireNonNull;

/// PrintWriter implementation that will indent each new line with a specified number of whitespace
/// characters. The writing itself can be delegated to any other [Writer][java.io.Writer] implementation.
///
/// Care was taken to ensure that not only lines ended by calls to [#println()] methods trigger indentation,
/// but any other newline characters as well.
///
/// @author Sjoerd Talsma
public class IndentingPrintWriter extends PrintWriter {

    /// Constructor for new [PrintWriter] with indentation.
    ///
    /// @param writer      Delegate writer to send output to.
    /// @param indentation Initial indentation to start out on.
    protected IndentingPrintWriter(Appendable writer, Indentation indentation) {
        super(IndentingWriter.wrap(writer, indentation));
    }

    /// Returns an indenting printwriter around the given `delegate`.
    /// If the `delegate` printwriter is already an indenting printwriter, it will simply be returned as-is.
    /// If the `delegate` printwriter is not yet an indending printwriter, a new indenting printwriter class
    /// will be created to wrap the delegate using the specified `indentation`.
    ///
    /// @param delegate    The delegate to turn into an indenting printwriter.
    /// @param indentation The indentation to use for the indenting printwriter
    ///                                       (optional, specify `null` to use the default indentation).
    /// @return The indenting delegate writer.
    /// @see Indentation#DEFAULT
    public static IndentingPrintWriter wrap(Appendable delegate, Indentation indentation) {
        return delegate instanceof IndentingPrintWriter
                ? ((IndentingPrintWriter) delegate).withIndentation(indentation)
                : new IndentingPrintWriter(delegate, indentation);
    }

    /// Return the delegate writer as an [IndentingWriter].
    ///
    /// @return Delegate writer.
    protected IndentingWriter getDelegate() {
        return (IndentingWriter) super.out;
    }

    /// The indentation; must be non-`null` in all practical instances of this object.
    ///
    /// @return The indentation (non-`null`).
    private Indentation getIndentation() {
        return requireNonNull(out instanceof IndentingWriter ? ((IndentingWriter) out).getIndentation() : null,
                "No indentation detected in IndentingPrintWriter!");
    }

    private IndentingPrintWriter withIndentation(Indentation indentation) {
        return indentation == null || indentation.equals(getIndentation()) ? this
                : new IndentingPrintWriter(out, indentation);
    }

    /// Returns a new indenting print writer with the indentation level increased by one.
    ///
    /// @return A new indenting print writer with increased indentation.
    public IndentingPrintWriter indent() {
        return withIndentation(getIndentation().increase());
    }

    /// Returns a new indenting print writer with the indentation level decreased by one.
    ///
    /// @return A new indenting print writer with decreased indentation.
    public IndentingPrintWriter unindent() {
        return withIndentation(getIndentation().decrease());
    }

    /// Adds a single whitespace character to the output, but only if the previous character was not a whitespace character.
    ///
    /// @return Reference to this print writer for method chaining.
    public IndentingPrintWriter whitespace() {
        try {
            if (out instanceof IndentingWriter) ((IndentingWriter) out).whitespace();
            else out.append(' ');
            return this;
        } catch (IOException ioe) {
            throw new IllegalStateException("Error writing whitespace: " + ioe.getMessage(), ioe);
        }
    }

    /// Adds a newline character to the output.
    ///
    /// @return Reference to this print writer for method chaining.
    public IndentingPrintWriter newline() {
        super.println();
        return this;
    }

    /// Append the specified character sequence.
    ///
    /// When printing new lines, the writer will apply the current [indentation][#getIndentation()].
    ///
    /// @param csq The character sequence to append.
    /// @return Reference to this indenting [PrintWriter] for chaining purposes.
    @Override
    public IndentingPrintWriter append(CharSequence csq) {
        return (IndentingPrintWriter) super.append(csq);
    }

    /// Append the specified characters.
    ///
    /// When printing new lines, the writer will apply the current [indentation][#getIndentation()].
    ///
    /// @param csq   The character sequence to append.
    /// @param start The index of the first character to be appended.
    /// @param end   The index after the last character to append.
    /// @return Reference to this indenting [PrintWriter] for chaining purposes.
    @Override
    public IndentingPrintWriter append(CharSequence csq, int start, int end) {
        return (IndentingPrintWriter) super.append(csq, start, end);
    }

    /// Append a single character.
    ///
    /// When printing the first character on an empty line, the writer will apply the current [indentation][#getIndentation()].
    ///
    /// @param c The character to append.
    /// @return Reference to this indenting [PrintWriter] for chaining purposes.
    @Override
    public IndentingPrintWriter append(char c) {
        return (IndentingPrintWriter) super.append(c);
    }

    /// Returns the delegate writer as `String`.
    ///
    /// @return the delegate as String.
    @Override
    public String toString() {
        return out.toString();
    }

}
