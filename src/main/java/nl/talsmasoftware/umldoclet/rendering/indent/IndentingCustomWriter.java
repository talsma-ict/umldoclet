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

import nl.talsmasoftware.indentation.Indentation;
import nl.talsmasoftware.indentation.io.IndentingWriter;

import java.io.IOException;
import java.io.PrintWriter;

/// PrintWriter implementation that will indent each new line with a specified number of whitespace
/// characters. The writing itself can be delegated to any other [Writer][java.io.Writer] implementation.
///
/// @author Sjoerd Talsma
public class IndentingCustomWriter extends IndentingWriter {
    /// Constructor for new [PrintWriter] with indentation.
    ///
    /// @param writer      Delegate writer to send output to.
    /// @param indentation Initial indentation to start out on.
    protected IndentingCustomWriter(Appendable writer, Indentation indentation) {
        super(writer, indentation);
    }

    /// Returns an indenting printwriter around the given `delegate`.
    /// If the `delegate` printwriter is already an indenting printwriter, it will simply be returned as-is.
    /// If the `delegate` printwriter is not yet an indending printwriter, a new indenting printwriter class
    /// will be created to wrap the delegate using the specified `indentation`.
    ///
    /// @param delegate    The delegate to turn into an indenting printwriter.
    /// @param indentation The indentation to use for the indenting printwriter
    ///                                                                                                                   (optional, specify `null` to use the default indentation).
    /// @return The indenting delegate writer.
    public static IndentingCustomWriter wrap(Appendable delegate, Indentation indentation) {
        if (delegate instanceof IndentingCustomWriter) {
            delegate = ((IndentingCustomWriter) delegate).getDelegate();
        }
        return new IndentingCustomWriter(delegate, indentation);
    }

    /// Return the delegate writer as an [IndentingWriter].
    ///
    /// @return Delegate writer.
    protected Appendable getDelegate() {
        return (Appendable) lock;
    }

    private IndentingCustomWriter withIndentation(Indentation indentation) {
        super.setIndentation(indentation);
        return this;
    }

    /// Returns a new indenting print writer with the indentation level increased by one.
    ///
    /// @return A new indenting print writer with increased indentation.
    public IndentingCustomWriter indent() {
        return withIndentation(getIndentation().indent());
    }

    /// Returns a new indenting print writer with the indentation level decreased by one.
    ///
    /// @return A new indenting print writer with decreased indentation.
    public IndentingCustomWriter unindent() {
        return withIndentation(getIndentation().unindent());
    }

    /// Adds a single whitespace character to the output, but only if the previous character was not a whitespace character.
    ///
    /// @return Reference to this print writer for method chaining.
    public IndentingCustomWriter whitespace() {
        if (getLastWritten() != ' ') {
            append(' ');
        }
        return this;
    }

    /// Adds a newline character to the output.
    ///
    /// @return Reference to this print writer for method chaining.
    public IndentingCustomWriter newline() {
        return append(System.lineSeparator());
    }

    /// Append the specified character sequence.
    ///
    /// When printing new lines, the writer will apply the current [indentation][#getIndentation()].
    ///
    /// @param csq The character sequence to append.
    /// @return Reference to this indenting [PrintWriter] for chaining purposes.
    @Override
    public IndentingCustomWriter append(CharSequence csq) {
        try {
            return (IndentingCustomWriter) super.append(csq);
        } catch (IOException ioe) {
            throw new IllegalStateException("Error appending characters: " + ioe.getMessage(), ioe);
        }
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
    public IndentingCustomWriter append(CharSequence csq, int start, int end) {
        try {
            return (IndentingCustomWriter) super.append(csq, start, end);
        } catch (IOException ioe) {
            throw new IllegalStateException("Error appending characters: " + ioe.getMessage(), ioe);
        }
    }

    /// Append a single character.
    ///
    /// When printing the first character on an empty line, the writer will apply the current [indentation][#getIndentation()].
    ///
    /// @param c The character to append.
    /// @return Reference to this indenting [PrintWriter] for chaining purposes.
    @Override
    public IndentingCustomWriter append(char c) {
        try {
            return (IndentingCustomWriter) super.append(c);
        } catch (IOException ioe) {
            throw new IllegalStateException("Error appending character: " + ioe.getMessage(), ioe);
        }
    }
}
