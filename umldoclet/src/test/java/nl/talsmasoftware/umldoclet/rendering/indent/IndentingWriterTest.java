/*
 * Copyright 2016-2024 Talsma ICT
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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static nl.talsmasoftware.umldoclet.rendering.writers.ThrowingWriter.throwing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class IndentingWriterTest {

    @Test
    public void testWritingFromNonZeroOffset() throws IOException {
        StringWriter output = new StringWriter();
        new IndentingWriter(output, Indentation.DEFAULT).write("1234".toCharArray(), 1, 2);
        assertThat(output, hasToString("23"));
    }

    @Test
    public void testUnindentFromZero() {
        IndentingWriter indentingWriter = new IndentingWriter(new StringWriter(), Indentation.DEFAULT);
        assertThat(indentingWriter.unindent(), is(sameInstance(indentingWriter)));
    }

    @Test
    public void testIndent() {
        assertThat(new IndentingWriter(new StringWriter(), Indentation.DEFAULT).indent().getIndentation(),
                is(equalTo(Indentation.DEFAULT.increase())));
    }

    @Test
    public void testNoFlushingTerminatingWhitespace() throws IOException {
        StringWriter output = new StringWriter();
        IndentingWriter indentingWriter = new IndentingWriter(output, Indentation.DEFAULT);

        indentingWriter.write("Some content");
        indentingWriter.whitespace();
        indentingWriter.flush();
        assertThat(output, hasToString("Some content"));

        indentingWriter.write("more content");
        indentingWriter.whitespace();
        indentingWriter.flush();
        assertThat(output, hasToString("Some content more content"));
    }

    @Test
    public void testToStringDelegation() throws IOException {
        IndentingWriter indentingWriter = new IndentingWriter(new StringWriter(), Indentation.DEFAULT);
        indentingWriter.write("first line\n");
        indentingWriter.indent().write("second line");
        assertThat(indentingWriter, hasToString("first line\n    second line"));
    }

    @Test
    public void testCloseRethrowingRuntimeExceptions() throws IOException {
        final RuntimeException runtimeException = new RuntimeException("Runtime exception!");
        assertThat(
                assertThrows(RuntimeException.class, () ->
                        new IndentingWriter(throwing(runtimeException), Indentation.DEFAULT).close()),
                is(sameInstance(runtimeException)));
    }

    @Test
    public void testCloseRethrowingIOExceptions() {
        IOException ioException = new IOException("I/O exception!");
        try {
            new IndentingWriter(throwing(ioException), Indentation.DEFAULT).close();
            fail("I/O exception expected.");
        } catch (IOException expected) {
            assertThat(expected, is(sameInstance(ioException)));
        }
    }

    @Test
    public void testCloseWrappingCheckedExceptions() throws IOException {
        Exception checkedException = new Exception("Checked exception!");
        try {
            new IndentingWriter(throwing(checkedException), Indentation.DEFAULT).close();
            fail("Runtime exception expected.");
        } catch (RuntimeException expected) {
            assertThat(expected.getCause(), is(sameInstance(checkedException)));
        }
    }

}
