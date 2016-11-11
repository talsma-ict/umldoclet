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
 *
 */

package nl.talsmasoftware.umldoclet.rendering;

import com.sun.javadoc.DocErrorReporter;
import nl.talsmasoftware.umldoclet.config.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static nl.talsmasoftware.umldoclet.testing.Testing.NEWLINE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;

/**
 * Unit tests for the base Renderer functionality.
 *
 * @author Sjoerd Talsma
 */
public class RendererTest {

    DocErrorReporter mockReporter;
    private Renderer renderer;

    @Before
    public void setUp() {
        mockReporter = Mockito.mock(DocErrorReporter.class);
        UMLDocletConfig config = new UMLDocletConfig(new String[0][], mockReporter);

        renderer = new YabadabadooRenderer(new DiagramRenderer(config));
    }

    @Test
    public void testToString() {
        assertThat(renderer, hasToString(equalTo("Yabadabadoo!" + NEWLINE)));
    }

    private static class YabadabadooRenderer extends Renderer {
        protected YabadabadooRenderer(DiagramRenderer currentDiagram) {
            super(currentDiagram);
        }

        @Override
        public IndentingPrintWriter writeTo(IndentingPrintWriter output) {
            return output.append("Yabadabadoo!").newline();
        }
    }

}
