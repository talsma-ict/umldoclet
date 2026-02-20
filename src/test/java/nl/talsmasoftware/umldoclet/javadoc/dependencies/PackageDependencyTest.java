/*
 * Copyright 2016-2026 Talsma ICT
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
package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackageDependencyTest {
    @Test
    public void testDependencyWithoutFromPackage() {
        NullPointerException expected = assertThrows(NullPointerException.class, () -> new PackageDependency(null, "b"));
        assertThat(expected.getMessage(), notNullValue());
    }

    @Test
    public void testDependencyWithoutToPackage() {
        NullPointerException expected = assertThrows(NullPointerException.class, () -> new PackageDependency("a", null));
        assertThat(expected.getMessage(), notNullValue());
    }

    @Test
    public void testHashCode() {
        assertThat(new PackageDependency("a", "b").hashCode(), is(new PackageDependency("a", "b").hashCode()));
    }

    @Test
    public void testEquals() {
        assertThat(new PackageDependency("a", "b"), is(equalTo(new PackageDependency("a", "b"))));
        assertThat(new PackageDependency("a", "b"), not(equalTo(new PackageDependency("a", "a"))));
        assertThat(new PackageDependency("a", "b"), not(equalTo(new PackageDependency("b", "b"))));
    }

    @Test
    public void testToString() {
        assertThat(new PackageDependency("a", "b"), hasToString("a->b"));
    }
}
