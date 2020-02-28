/*
 * Copyright 2016-2020 Talsma ICT
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sjoerd Talsma
 */
public class StringBufferingWriterTest {

    @Test
    public void createWithNullDelegate() {
        NullPointerException expected = assertThrows(NullPointerException.class, () ->
                new StringBufferingWriter(null));
        assertThat(expected.getMessage(), notNullValue());
    }

    @Test
    public void testGetBuffer() throws IOException {
        final StringBufferingWriter writer = new StringBufferingWriter(new NoopWriter());
        final StringBuffer buffer = writer.getBuffer();

        writer.write("The quick brown fox jumps over the lazy dog");
        writer.flush();
        assertThat(buffer, hasToString("The quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void testToString() {
        assertThat(new StringBufferingWriter(new NoopWriter()), hasToString("StringBufferingWriter{NoopWriter}"));
    }

}
