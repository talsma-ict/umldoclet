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
package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.fail;

public class DependencyCycleTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyDependencyCycle() {
        new DependencyCycle();
        fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompleteCycle() {
        new DependencyCycle(new Dependency("a", "b"), new Dependency("b", "c"));
    }

    @Test
    public void testDependencyCycleToString() {
        assertThat(new DependencyCycle(new Dependency("a", "b"), new Dependency("b", "c"), new Dependency("c", "a")),
                hasToString("a > b > c > a"));
    }

    @Test
    public void testSimpleCycleDetection() {
        Dependency ab = new Dependency("a", "b");
        Dependency ba = new Dependency("b", "a");

        Collection<DependencyCycle> cycles = DependencyCycle.detect(asList(ab, ba));
        assertThat(cycles, hasItem(new DependencyCycle(ab, ba)));
    }

    @Test
    public void testMultipeCycleDetection() {
        Dependency ab = new Dependency("a", "b");
        Dependency bc = new Dependency("b", "c");
        Dependency bd = new Dependency("b", "d");
        Dependency cd = new Dependency("c", "d");
        Dependency de = new Dependency("d", "e");

        Dependency ba = new Dependency("b", "a");
        Dependency ca = new Dependency("c", "a");
        Dependency ea = new Dependency("e", "a");
        List<Dependency> alldeps = asList(ab, bc, bd, cd, de, ba, ca, ea);

        Collection<DependencyCycle> cycles = DependencyCycle.detect(alldeps);
        assertThat(cycles, hasItem(new DependencyCycle(ab, ba)));
        assertThat(cycles, hasItem(new DependencyCycle(ab, bc, ca)));
        assertThat(cycles, hasItem(new DependencyCycle(ab, bc, cd, de, ea)));
        assertThat(cycles, hasItem(new DependencyCycle(ab, bd, de, ea)));
    }

}
