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
package nl.talsmasoftware.umldoclet.testing.annotations;

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * @author Sjoerd Talsma
 */
@Ignore // Cannot create uml javadoc yet..
@DummyAnnotation
public class AnnotationTest {

    @Test
    public void testAnnotationRendering() {
        String packageUml = Testing.readFile("testing/annotations/package.puml");
        assertThat(packageUml, containsString("annotation DummyAnnotation"));
    }

}
