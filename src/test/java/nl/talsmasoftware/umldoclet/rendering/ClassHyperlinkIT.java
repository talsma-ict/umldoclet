/*
 * Copyright 2016-2017 Talsma ICT
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

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

/**
 * Test whether classes contain a hyperlink to the generated HTML javadoc.
 * <p>
 * This is an 'integration test' as it needs to test on the actual JavaDoc
 * because links are only rendered in combination with the standard doclet.
 *
 * @author Sjoerd Talsma
 */
public class ClassHyperlinkIT {

    @Test
    public void testClassRendererHyperlink() throws IOException {
        String uml = Testing.readClassUml(ClassRenderer.class);
        assertThat(uml, stringContainsInOrder(asList(
                "class ClassRenderer {",
                "[[ClassRenderer.html]]",
                //...
                "}"
        )));
    }

}