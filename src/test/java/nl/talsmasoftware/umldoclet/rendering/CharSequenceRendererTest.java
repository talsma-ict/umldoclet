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
package nl.talsmasoftware.umldoclet.rendering;

import org.junit.Test;

import java.io.StringWriter;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Sjoerd Talsma
 */
public class CharSequenceRendererTest {

    @Test(expected = NullPointerException.class)
    public void testOfNull() {
        CharSequenceRenderer.of(null);
    }

    @Test
    public void testCharSequenceDelegation() {
        Stream.of("", " ", "The quick brown fox jumps over the lazy dog").forEach(s -> {
            CharSequenceRenderer csr = CharSequenceRenderer.of(s);
            assertThat(csr.length(), is(s.length()));
            range(0, csr.length()).forEach(i -> {
                assertThat(csr.charAt(i), is(s.charAt(i)));
                range(0, i).forEach(j -> {
                    assertThat(csr.subSequence(0, j).toString(), equalTo(s.substring(0, j)));
                    assertThat(csr.subSequence(j, i).toString(), equalTo(s.substring(j, i)));
                });
            });
            assertThat(csr.hashCode(), is(CharSequenceRenderer.of(s).hashCode()));
            assertThat(csr.equals(csr), is(true));
            assertThat(csr.equals(CharSequenceRenderer.of(s)), is(true));
            assertThat(csr, hasToString(equalTo(s)));
        });
    }

    @Test
    public void testWriteTo() {
        StringWriter output = new StringWriter();
        CharSequenceRenderer.of("The quick brown fox jumps over the lazy dog").writeTo(output);
        output.flush();
        assertThat(output, hasToString("The quick brown fox jumps over the lazy dog"));
    }

    @Test(expected = RuntimeException.class)
    public void testWriteTo_IOException() {
        CharSequenceRenderer.of("The quick brown fox jumps over the lazy dog").writeTo(new ThrowingAppendable());
    }

}
