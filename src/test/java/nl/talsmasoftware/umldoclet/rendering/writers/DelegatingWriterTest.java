/*
 * Copyright 2016-2018 Talsma ICT
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

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Sjoerd Talsma
 */
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
    public void testWrite_singleIOException() throws IOException {
        IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        try {
            delegatingWriter.write("The quick brown fox jumps over the lazy dog");
            fail("IO exception expected");
        } catch (IOException ioe) {
            assertThat(ioe, is(sameInstance(ioException)));
        }
    }

    @Test
    public void testWrite_singleRuntimeException() throws IOException {
        RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        try {
            delegatingWriter.write("The quick brown fox jumps over the lazy dog");
            fail("Runtime exception expected");
        } catch (RuntimeException rte) {
            assertThat(rte, is(sameInstance(runtimeException)));
        }
    }

    @Test
    public void testWrite_multipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        try {
            delegatingWriter.write("The quick brown fox jumps over the lazy dog");
            fail("IO exception expected");
        } catch (IOException ioe) {
            Throwable[] suppressed = ioe.getSuppressed();
            assertThat(suppressed, is(notNullValue()));
            assertThat(suppressed.length, is(2));
            assertThat(suppressed[0], is(sameInstance(expectedException1)));
            assertThat(suppressed[1], is(sameInstance(expectedException2)));
        }
    }

    @Test
    public void testWrite_combinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        StringWriter stringWriter = new StringWriter();
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, stringWriter);
        try {
            delegatingWriter.write("The quick brown fox jumps over the lazy dog");
            fail("Exception expected");
        } catch (RuntimeException expected) {
            assertThat(expected.getMessage(), is("Illegal state!"));
        }
        assertThat(stringWriter, hasToString("The quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void testFlush_singleIOException() throws IOException {
        IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        try {
            delegatingWriter.flush();
            fail("IO exception expected");
        } catch (IOException ioe) {
            assertThat(ioe, is(sameInstance(ioException)));
        }
    }

    @Test
    public void testFlush_singleRuntimeException() throws IOException {
        RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        try {
            delegatingWriter.flush();
            fail("Runtime exception expected");
        } catch (RuntimeException rte) {
            assertThat(rte, is(sameInstance(runtimeException)));
        }
    }

    @Test
    public void testFlush_multipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        try {
            delegatingWriter.flush();
            fail("IO exception expected");
        } catch (IOException ioe) {
            Throwable[] suppressed = ioe.getSuppressed();
            assertThat(suppressed, is(notNullValue()));
            assertThat(suppressed.length, is(2));
            assertThat(suppressed[0], is(sameInstance(expectedException1)));
            assertThat(suppressed[1], is(sameInstance(expectedException2)));
        }
    }

    @Test
    public void testFlush_combinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        Writer mockWriter = mock(Writer.class);
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, mockWriter);
        try {
            delegatingWriter.flush();
            fail("Exception expected");
        } catch (RuntimeException expected) {
            assertThat(expected.getMessage(), is("Illegal state!"));
        }
        verify(mockWriter).flush();
    }

    @Test
    public void testClose_singleIOException() throws IOException {
        IOException ioException = new IOException("IO error!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(ioException));
        try {
            delegatingWriter.close();
            fail("IO exception expected");
        } catch (IOException ioe) {
            assertThat(ioe, is(sameInstance(ioException)));
        }
    }

    @Test
    public void testClose_singleRuntimeException() throws IOException {
        RuntimeException runtimeException = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(ThrowingWriter.throwing(runtimeException));
        try {
            delegatingWriter.close();
            fail("Runtime exception expected");
        } catch (RuntimeException rte) {
            assertThat(rte, is(sameInstance(runtimeException)));
        }
    }

    @Test
    public void testClose_multipleExceptions() throws IOException {
        IOException expectedException1 = new IOException("IO error!");
        RuntimeException expectedException2 = new IllegalStateException("Illegal state!");
        DelegatingWriter delegatingWriter = new DelegatingWriter(
                ThrowingWriter.throwing(expectedException1), ThrowingWriter.throwing(expectedException2));
        try {
            delegatingWriter.close();
            fail("IO exception expected");
        } catch (IOException ioe) {
            Throwable[] suppressed = ioe.getSuppressed();
            assertThat(suppressed, is(notNullValue()));
            assertThat(suppressed.length, is(2));
            assertThat(suppressed[0], is(sameInstance(expectedException1)));
            assertThat(suppressed[1], is(sameInstance(expectedException2)));
        }
    }

    @Test
    public void testClose_combinedSuccessAndException() throws IOException {
        ThrowingWriter throwingWriter = ThrowingWriter.throwing(new IllegalStateException("Illegal state!"));
        Writer mockWriter = mock(Writer.class);
        DelegatingWriter delegatingWriter = new DelegatingWriter(throwingWriter, mockWriter);
        try {
            delegatingWriter.close();
            fail("Exception expected");
        } catch (RuntimeException expected) {
            assertThat(expected.getMessage(), is("Illegal state!"));
        }
        verify(mockWriter).close();
    }
}
