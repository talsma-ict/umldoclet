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

package nl.talsmasoftware.umldoclet.testing.legacy;

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit test for generated UML diagrams based on 'legacy' JavaDoc tags.
 */
public class LegacyTest {
    private final String channelIteratorName = ChannelIterator.class.getName();
    private final String setTopControllerName = SetTopController.class.getName();

    @Test
    public void testDependTag() {
        String packageUml = Testing.readFile("testing/legacy/package.puml");
        assertThat(packageUml, is(not(nullValue())));
        assertThat(packageUml, containsString(
                setTopControllerName + " <.. " + channelIteratorName + ": friend"));
    }

}
