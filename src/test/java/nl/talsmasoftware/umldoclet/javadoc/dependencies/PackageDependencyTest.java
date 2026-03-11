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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PackageDependencyTest {
    @Test
    public void testDependencyWithoutFromPackage() {
        assertThatThrownBy(() -> new PackageDependency(null, "b"))
                .isInstanceOf(NullPointerException.class)
                .message().isNotBlank();
    }

    @Test
    public void testDependencyWithoutToPackage() {
        assertThatThrownBy(() -> new PackageDependency("a", null))
                .isInstanceOf(NullPointerException.class)
                .message().isNotBlank();
    }

    @Test
    public void testHashCode() {
        assertThat(new PackageDependency("a", "b")).hasSameHashCodeAs(new PackageDependency("a", "b"));
    }

    @Test
    public void testEquals() {
        assertThat(new PackageDependency("a", "b")).isEqualTo(new PackageDependency("a", "b"));
        assertThat(new PackageDependency("a", "b")).isNotEqualTo(new PackageDependency("a", "a"));
        assertThat(new PackageDependency("a", "b")).isNotEqualTo(new PackageDependency("b", "b"));
    }

    @Test
    public void testToString() {
        assertThat(new PackageDependency("a", "b")).hasToString("a->b");
    }
}
