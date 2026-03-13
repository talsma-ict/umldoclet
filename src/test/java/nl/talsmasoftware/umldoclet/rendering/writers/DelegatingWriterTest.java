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
package nl.talsmasoftware.umldoclet.rendering.writers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DelegatingWriterTest {

    @Test
    public void testEmptyDelegatingWriter() throws IOException {
        DelegatingWriter delegatingWriter = new DelegatingWriter();
        delegatingWriter.write("The quick brown fox jumps over the lazy dog");
        delegatingWriter.flush();
        delegatingWriter.close();
        assertThat(delegatingWriter).hasToString("DelegatingWriter[]");
    }

    @Test
    public void testSingleDelegation() throws IOException {
        StringWriter stringDelegate = new StringWriter();
        DelegatingWriter delegatingWriter = new DelegatingWriter(stringDelegate);
        delegatingWriter.write("The quick brown fox jumps over the lazy dog");
        delegatingWriter.flush();
        delegatingWriter.close();
        assertThat(stringDelegate).hasToString("The quick brown fox jumps over the lazy dog");
    }

    @Test
    public void testWriteSingleIOException() throws IOException {
        final IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        assertThatThrownBy(() -> delegatingWriter.write("The quick brown fox jumps over the lazy dog"))
                .isInstanceOf(IOException.class)
                .isSameAs(ioException);
    }

    @Test
    public void testWriteSingleRuntimeException() throws IOException {
        final RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        assertThatThrownBy(() -> delegatingWriter.write("The quick brown fox jumps over the lazy dog"))
                .isInstanceOf(RuntimeException.class)
                .isSameAs(runtimeException);
    }

    @Test
    public void testWriteMultipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        assertThatThrownBy(() -> delegatingWriter.write("The quick brown fox jumps over the lazy dog"))
                .isInstanceOf(IOException.class)
                .hasSuppressedException(expectedException1)
                .hasSuppressedException(expectedException2);
    }

    @Test
    public void testWriteCombinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        StringWriter stringWriter = new StringWriter();
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, stringWriter);
        assertThatThrownBy(() -> delegatingWriter.write("The quick brown fox jumps over the lazy dog"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Illegal state!");
        assertThat(stringWriter).hasToString("The quick brown fox jumps over the lazy dog");
    }

    @Test
    public void testFlushSingleIOException() throws IOException {
        final IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        assertThatThrownBy(delegatingWriter::flush).isInstanceOf(IOException.class).isSameAs(ioException);
    }

    @Test
    public void testFlushSingleRuntimeException() throws IOException {
        final RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        assertThatThrownBy(delegatingWriter::flush).isInstanceOf(RuntimeException.class).isSameAs(runtimeException);
    }

    @Test
    public void testFlushMultipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        assertThatThrownBy(delegatingWriter::flush)
                .isInstanceOf(IOException.class)
                .hasSuppressedException(expectedException1)
                .hasSuppressedException(expectedException2);
    }

    @Test
    public void testFlushCombinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        Writer mockWriter = mock(Writer.class);
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, mockWriter);
        assertThatThrownBy(delegatingWriter::flush).isInstanceOf(RuntimeException.class).hasMessage("Illegal state!");
        verify(mockWriter).flush();
    }

    @Test
    public void testCloseSingleIOException() throws IOException {
        final IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        assertThatThrownBy(delegatingWriter::close).isInstanceOf(IOException.class).isSameAs(ioException);
    }

    @Test
    public void testCloseSingleRuntimeException() throws IOException {
        final RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        assertThatThrownBy(delegatingWriter::close).isInstanceOf(RuntimeException.class).isSameAs(runtimeException);
    }

    @Test
    public void testCloseMultipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        assertThatThrownBy(delegatingWriter::close)
                .isInstanceOf(IOException.class)
                .hasSuppressedException(expectedException1)
                .hasSuppressedException(expectedException2);
    }

    @Test
    public void testCloseCombinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        Writer mockWriter = mock(Writer.class);
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, mockWriter);
        assertThatThrownBy(delegatingWriter::close).isInstanceOf(RuntimeException.class).hasMessage("Illegal state!");
        verify(mockWriter).close();
    }
}
