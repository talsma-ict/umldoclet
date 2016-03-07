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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

/**
 * Created on 17-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class IndentingPrintWriterTest {

    /**
     * Determine the newline for this OS.
     */
    private static final String NEWLINE;

    static {
        Writer writer = new StringWriter();
        new PrintWriter(writer).println();
        NEWLINE = writer.toString();
    }

    @Test
    public void testIndentingPrintWriter_nullWriter() {
        try {
            new IndentingPrintWriter(null, -1);
            fail("Exception expected.");
        } catch (NullPointerException expected) {
            assertThat("Exception message", expected.getMessage(), is(not(nullValue())));
        }
    }

    @Test
    public void testIndentingWithNewlinesWithinString() throws IOException {
        StringWriter target = new StringWriter();
        IndentingPrintWriter.wrap(target)
                .indent().append("text").newline()
                .append("plus a test" + NEWLINE + "with contained newline")
                .flush();
        assertThat(target, hasToString(equalTo(
                "    text" + NEWLINE +
                        "    plus a test" + NEWLINE +
                        "    with contained newline")));
    }

}
