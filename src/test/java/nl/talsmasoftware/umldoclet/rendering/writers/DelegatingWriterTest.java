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
package nl.talsmasoftware.umldoclet.rendering.writers;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

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

}
