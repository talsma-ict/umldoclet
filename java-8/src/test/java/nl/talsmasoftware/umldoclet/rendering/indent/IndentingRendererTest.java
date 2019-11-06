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

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

/**
 * @author Sjoerd Talsma
 */
public class IndentingRendererTest {

    @Test
    public void testWriteToAppendable_defaultIndentation() {
        StringBuilder stringBuilder = new StringBuilder();
        new StringIndentingRenderer("The quick brown fox jumps over the lazy dog").writeTo(stringBuilder);

        assertThat(stringBuilder, hasToString("    The quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void testWriteToAppendableIPW() {
        StringBuilder stringBuilder = new StringBuilder();
        IndentingPrintWriter ipw = IndentingPrintWriter.wrap(stringBuilder, Indentation.tabs(0));
        new StringIndentingRenderer("The quick brown fox jumps over the lazy dog").writeTo((Appendable) ipw);

        assertThat(stringBuilder, hasToString("\tThe quick brown fox jumps over the lazy dog"));
    }

    private static class StringIndentingRenderer implements IndentingRenderer {
        private final String value;

        private StringIndentingRenderer(String value) {
            this.value = value;
        }

        @Override
        public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
            output.indent().write(value);
            return output;
        }
    }

}
