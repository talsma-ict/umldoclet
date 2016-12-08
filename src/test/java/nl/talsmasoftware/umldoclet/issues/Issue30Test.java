/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * @author Sjoerd Talsma
 */
public class Issue30Test {

    final static String packageDiag = Testing.readFile("issues/package.puml");

    // Method that should not be seen as a bean property.
    public Issue30Test getSomeValue(Boolean withArgument) {
        return this;
    }

    // Method that SHOULD be seen as a bean property.
    public Issue30Test getSomeProperty() {
        return this;
    }

    @Test
    public void testIssue30() {
        assertThat(packageDiag, containsString("+getSomeValue(Boolean): Issue30Test"));
        assertThat(packageDiag, not(containsString("+getSomeProperty(): Issue30Test")));
        assertThat(packageDiag, containsString("Issue30Test --> Issue30Test: someProperty"));
        assertThat(packageDiag, not(containsString("Issue30Test --> Issue30Test: someValue")));
    }

}
