/*
 * Copyright 2016-2024 Talsma ICT
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

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackageDependencyCycleTest {

    @Test
    public void testEmptyDependencyCycle() {
        IllegalArgumentException expected = assertThrows(IllegalArgumentException.class, PackageDependencyCycle::new);
        assertThat(expected.getMessage(), notNullValue());
    }

    @Test
    public void testIncompleteCycle() {
        IllegalArgumentException expected = assertThrows(IllegalArgumentException.class, () ->
                new PackageDependencyCycle(new PackageDependency("a", "b"), new PackageDependency("b", "c")));
        assertThat(expected.getMessage(), notNullValue());
    }

    @Test
    public void testDependencyCycleToString() {
        assertThat(new PackageDependencyCycle(
                        new PackageDependency("a", "b"),
                        new PackageDependency("b", "c"),
                        new PackageDependency("c", "a")),
                hasToString("a > b > c > a"));
    }

    @Test
    public void testSimpleCycleDetection() {
        PackageDependency ab = new PackageDependency("a", "b");
        PackageDependency ba = new PackageDependency("b", "a");

        Collection<PackageDependencyCycle> cycles = PackageDependencyCycle.detectCycles(asList(ab, ba));
        assertThat(cycles, hasItem(new PackageDependencyCycle(ab, ba)));
    }

    @Test
    public void testMultipeCycleDetection() {
        PackageDependency ab = new PackageDependency("a", "b");
        PackageDependency bc = new PackageDependency("b", "c");
        PackageDependency bd = new PackageDependency("b", "d");
        PackageDependency cd = new PackageDependency("c", "d");
        PackageDependency de = new PackageDependency("d", "e");

        PackageDependency ba = new PackageDependency("b", "a");
        PackageDependency ca = new PackageDependency("c", "a");
        PackageDependency ea = new PackageDependency("e", "a");
        List<PackageDependency> alldeps = asList(ab, bc, bd, cd, de, ba, ca, ea);

        Collection<PackageDependencyCycle> cycles = PackageDependencyCycle.detectCycles(alldeps);
        assertThat(cycles, hasItem(new PackageDependencyCycle(ab, ba)));
        assertThat(cycles, hasItem(new PackageDependencyCycle(ab, bc, ca)));
        assertThat(cycles, hasItem(new PackageDependencyCycle(ab, bc, cd, de, ea)));
        assertThat(cycles, hasItem(new PackageDependencyCycle(ab, bd, de, ea)));
    }

}
