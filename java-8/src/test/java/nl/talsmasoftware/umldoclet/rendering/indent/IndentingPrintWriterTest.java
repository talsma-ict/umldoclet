/*
 * Copyright 2016-2019 Talsma ICT
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

import nl.talsmasoftware.umldoclet.rendering.writers.ThrowingWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static nl.talsmasoftware.umldoclet.util.TestUtil.NEWLINE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the intenting print writer.
 * By unit-testing this class, we effectively also test the IndentingWriter implementation
 * that provides the actual indenting functionality.
 *
 * @author Sjoerd Talsma
 */
public class IndentingPrintWriterTest {

    @Test
    public void testIndentingPrintWriterNullWriter() {
        try {
            new IndentingPrintWriter(null, Indentation.DEFAULT);
            fail("Exception expected.");
        } catch (NullPointerException expected) {
            assertThat("Exception message", expected.getMessage(), is(not(nullValue())));
        }
    }

    @Test
    public void testIndentingWithNewlinesWithinString() {
        StringWriter target = new StringWriter();
        IndentingPrintWriter.wrap(target, null)
                .indent().append("text").newline()
                .append("plus a test" + NEWLINE + "with contained newline")
                .flush();
        assertThat(target, hasToString(equalTo(
                "    text" + NEWLINE +
                        "    plus a test" + NEWLINE +
                        "    with contained newline")));
    }

    @Test
    public void testWhitespaceRendering() {
        StringWriter target = new StringWriter();
        IndentingPrintWriter.wrap(target, null)
                .whitespace().whitespace()
                .indent().whitespace().append("Text ending in whitespace ").whitespace().append("!").newline()
                .whitespace().whitespace().append("Whitespace on beginning of line.")
                .flush();
        assertThat(target, hasToString(equalTo(
                "    Text ending in whitespace !" + NEWLINE +
                        "    Whitespace on beginning of line.")));
    }

    @Test
    public void testWhitespaceRenderingBeforeNewlines() {
        StringWriter output = new StringWriter();
        final IndentingPrintWriter writer = IndentingPrintWriter.wrap(output, Indentation.DEFAULT);
        writer.append('-').whitespace().append('\n').flush();
        assertThat(output, hasToString(equalTo("-\n")));
        clear(output);

        writer.append('-').whitespace().append('\r').flush();
        assertThat(output, hasToString(equalTo("-\r")));
        clear(output);
    }

    @Test
    public void testWhitespaceRenderingAfterNewlines() {
        StringWriter output = new StringWriter();
        final IndentingPrintWriter writer = IndentingPrintWriter.wrap(output, Indentation.DEFAULT);
        writer.append('\n').whitespace().append('-').flush();
        assertThat(output, hasToString(equalTo("\n-")));
        clear(output);

        writer.append('\r').whitespace().append('-').flush();
        assertThat(output, hasToString(equalTo("\r-")));
        clear(output);
    }

    @Test
    public void testWhitespaceModifiedUnderlyingWriter() {
        final StringWriter output = new StringWriter();
        SettableIndentingPrintWriter writer = new SettableIndentingPrintWriter(output, Indentation.DEFAULT);
        writer.setOut(output);

        writer.append('\n').whitespace().append('-').flush();
        assertThat(output, hasToString(equalTo("\n -")));
        clear(output);
    }

    @Test
    public void testWhitespaceIoeByUnderlyingWriter() {
        SettableIndentingPrintWriter writer = new SettableIndentingPrintWriter(new StringWriter(), Indentation.DEFAULT);
        writer.setOut(ThrowingWriter.throwing(new IOException("Buffer is full!")));
        RuntimeException rte = assertThrows(RuntimeException.class, writer::whitespace);
        assertThat(rte.getMessage(), containsString("Buffer is full!"));
    }

    @Test
    public void testIndentModifiedUnderlyingWriter() {
        final StringWriter output = new StringWriter();
        SettableIndentingPrintWriter writer = new SettableIndentingPrintWriter(output, Indentation.DEFAULT);
        writer.setOut(output);
        NullPointerException npe = assertThrows(NullPointerException.class, writer::indent);
        assertThat(npe.getMessage(), notNullValue());
    }

    /**
     * IndendingPrintWriter that allows tests to set its {@code out} field.
     */
    private static class SettableIndentingPrintWriter extends IndentingPrintWriter {
        private SettableIndentingPrintWriter(Appendable writer, Indentation indentation) {
            super(writer, indentation);
        }

        private void setOut(Writer out) {
            super.out = out;
        }
    }

    static void clear(StringWriter target) {
        target.getBuffer().delete(0, target.getBuffer().length());
    }
}
