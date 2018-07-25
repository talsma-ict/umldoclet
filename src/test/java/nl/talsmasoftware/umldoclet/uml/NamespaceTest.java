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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

public class NamespaceTest {

    @Test
    public void testEquals() {
        PackageUml packageUml = new PackageUml(mock(Configuration.class), "a.b.c");
        Namespace namespace = new Namespace(packageUml, "a.b.c");
        assertThat(namespace.equals(namespace), is(true));
        assertThat(namespace, is(equalTo(new Namespace(null, "a.b.c"))));
        assertThat(namespace, is(equalTo(new Namespace(packageUml, "a.b.c"))));
        assertThat(namespace, is(not(equalTo(new Namespace(packageUml, "A.B.C")))));
    }

    @Test
    public void testCompareTo() {
        PackageUml packageUml = new PackageUml(mock(Configuration.class), "a.b.c");
        Namespace namespace = new Namespace(packageUml, "a.b.c");

        assertThat(namespace.compareTo(namespace), is(0));
        assertThat(namespace.compareTo(new Namespace(null, "a.b.c.d")), is(lessThan(0)));
        assertThat(namespace.compareTo(new Namespace(null, "A.B.C")), is(not(0)));
    }

}
