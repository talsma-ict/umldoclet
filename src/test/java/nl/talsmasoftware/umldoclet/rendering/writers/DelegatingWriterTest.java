/*
 * Copyright 2016-2021 Talsma ICT
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
package nl.talsmasoftware.umldoclet.rendering.writers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DelegatingWriterTest {

    @Test
    public void testEmptyDelegatingWriter() throws IOException {
        DelegatingWriter delegatingWriter = new DelegatingWriter();
        delegatingWriter.write("The quick brown fox jumps over the lazy dog");
        delegatingWriter.flush();
        delegatingWriter.close();
        assertThat(delegatingWriter, hasToString("DelegatingWriter[]"));
    }

    @Test
    public void testSingleDelegation() throws IOException {
        StringWriter stringDelegate = new StringWriter();
        DelegatingWriter delegatingWriter = new DelegatingWriter(stringDelegate);
        delegatingWriter.write("The quick brown fox jumps over the lazy dog");
        delegatingWriter.flush();
        delegatingWriter.close();
        assertThat(stringDelegate, hasToString("The quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void testWriteSingleIOException() throws IOException {
        final IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        assertThat(
                assertThrows(IOException.class, () ->
                        delegatingWriter.write("The quick brown fox jumps over the lazy dog")),
                is(sameInstance(ioException)));
    }

    @Test
    public void testWriteSingleRuntimeException() throws IOException {
        final RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        assertThat(
                assertThrows(RuntimeException.class, () ->
                        delegatingWriter.write("The quick brown fox jumps over the lazy dog")),
                is(sameInstance(runtimeException)));
    }

    @Test
    public void testWriteMultipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        IOException ioe = assertThrows(IOException.class, () ->
                delegatingWriter.write("The quick brown fox jumps over the lazy dog"));
        Throwable[] suppressed = ioe.getSuppressed();
        assertThat(suppressed, is(notNullValue()));
        assertThat(suppressed.length, is(2));
        assertThat(suppressed[0], is(sameInstance(expectedException1)));
        assertThat(suppressed[1], is(sameInstance(expectedException2)));
    }

    @Test
    public void testWriteCombinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        StringWriter stringWriter = new StringWriter();
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, stringWriter);
        final RuntimeException expected = assertThrows(RuntimeException.class, () ->
                delegatingWriter.write("The quick brown fox jumps over the lazy dog"));
        assertThat(expected.getMessage(), is("Illegal state!"));
        assertThat(stringWriter, hasToString("The quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void testFlushSingleIOException() throws IOException {
        final IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        assertThat(
                assertThrows(IOException.class, delegatingWriter::flush),
                is(sameInstance(ioException)));
    }

    @Test
    public void testFlushSingleRuntimeException() throws IOException {
        final RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        assertThat(
                assertThrows(RuntimeException.class, delegatingWriter::flush),
                is(sameInstance(runtimeException)));
    }

    @Test
    public void testFlushMultipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        final IOException ioe = assertThrows(IOException.class, delegatingWriter::flush);
        Throwable[] suppressed = ioe.getSuppressed();
        assertThat(suppressed, is(notNullValue()));
        assertThat(suppressed.length, is(2));
        assertThat(suppressed[0], is(sameInstance(expectedException1)));
        assertThat(suppressed[1], is(sameInstance(expectedException2)));
    }

    @Test
    public void testFlushCombinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        Writer mockWriter = mock(Writer.class);
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, mockWriter);
        final RuntimeException expected = assertThrows(RuntimeException.class, delegatingWriter::flush);
        assertThat(expected.getMessage(), is("Illegal state!"));
        verify(mockWriter).flush();
    }

    @Test
    public void testCloseSingleIOException() throws IOException {
        final IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        assertThat(
                assertThrows(IOException.class, delegatingWriter::close),
                is(sameInstance(ioException)));
    }

    @Test
    public void testCloseSingleRuntimeException() throws IOException {
        final RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        assertThat(
                assertThrows(RuntimeException.class, delegatingWriter::close),
                is(sameInstance(runtimeException)));
    }

    @Test
    public void testCloseMultipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        final IOException ioe = assertThrows(IOException.class, delegatingWriter::close);
        Throwable[] suppressed = ioe.getSuppressed();
        assertThat(suppressed, is(notNullValue()));
        assertThat(suppressed.length, is(2));
        assertThat(suppressed[0], is(sameInstance(expectedException1)));
        assertThat(suppressed[1], is(sameInstance(expectedException2)));
    }

    @Test
    public void testCloseCombinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        Writer mockWriter = mock(Writer.class);
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, mockWriter);
        final RuntimeException expected = assertThrows(RuntimeException.class, delegatingWriter::close);
        assertThat(expected.getMessage(), is("Illegal state!"));
        verify(mockWriter).close();
    }
}
