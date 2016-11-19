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

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static nl.talsmasoftware.umldoclet.testing.Testing.NEWLINE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

/**
 * Tests for the intenting print writer.
 * By unit-testing this class, we effectively also test the IndentingWriter implementation
 * that provides the actual indenting functionality.
 *
 * @author Sjoerd Talsma
 */
public class IndentingPrintWriterTest {

    @Test
    public void testIndentingPrintWriter_nullWriter() {
        try {
            new IndentingPrintWriter(null, Indentation.DEFAULT);
            fail("Exception expected.");
        } catch (NullPointerException expected) {
            assertThat("Exception message", expected.getMessage(), is(not(nullValue())));
        }
    }

    @Test
    public void testIndentingWithNewlinesWithinString() throws IOException {
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
    public void testWhitespaceRendering() throws IOException {
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
    public void testWhitespaceRendering_beforeNewlines() throws IOException {
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
    public void testWhitespaceRendering_afterNewlines() throws IOException {
        StringWriter output = new StringWriter();
        final IndentingPrintWriter writer = IndentingPrintWriter.wrap(output, Indentation.DEFAULT);
        writer.append('\n').whitespace().append('-').flush();
        assertThat(output, hasToString(equalTo("\n-")));
        clear(output);

        writer.append('\r').whitespace().append('-').flush();
        assertThat(output, hasToString(equalTo("\r-")));
        clear(output);
    }

    static void clear(StringWriter target) {
        target.getBuffer().delete(0, target.getBuffer().length());
    }
}
