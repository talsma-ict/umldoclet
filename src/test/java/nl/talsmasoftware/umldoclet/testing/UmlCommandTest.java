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
package nl.talsmasoftware.umldoclet.testing;

import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * Test whether the UML commands are processed the correct way.
 *
 * @author Sjoerd Talsma
 */
@Ignore // Cannot create content javadoc yet..
public class UmlCommandTest {

    @Test
    public void testUmlCommandSetting() {
        String packageUml = TestUtil.readFile("testing/package.puml");
        assertThat(packageUml, containsString("hide empty members"));
        assertThat(packageUml, containsString("hide <<Stereotype>> circle"));
    }

}
