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
package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.spi.ToolProvider;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

/// Test [bugfix 64](https://github.com/talsma-ict/umldoclet/issues/64)
/// that any generic `EmptySet<T>` doesn't get rendered in UML as `EmptySet<T extends Object>`.
public class Issue64ExtendsObjectTest {
    private static String emptySetUml;

    /// Generic test class to generate a diagram to test the bugfix.
    public static class EmptySet<T> extends AbstractSet<T> {
        /// Default constructor.
        EmptySet() {
            super();
        }

        /// Empty iterator.
        ///
        /// @return iterator, always empty.
        @Override
        @SuppressWarnings("unchecked")
        public Iterator<T> iterator() {
            return (Iterator<T>) emptySet().iterator();
        }

        /// Size of empty set.
        ///
        /// @return size, always zero (`0`).
        @Override
        public int size() {
            return 0;
        }
    }

    /// Default constructor.
    Issue64ExtendsObjectTest() {
        super();
    }

    /// Set-up to generate Javadoc and UML Diagrams for this test.
    ///
    /// @throws IOException when I/O errors occur during rendering.
    @BeforeAll
    public static void produceUml() throws IOException {
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-sourcepath", "src/test/java",
                "-d", "target/issues/64",
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                Issue64ExtendsObjectTest.class.getPackageName()
        );
        emptySetUml = TestUtil.readUml(new FileInputStream(
                "target/issues/64/nl/talsmasoftware/umldoclet/issues/Issue64ExtendsObjectTest.EmptySet.puml"));
    }

    /// Test that `<T extends Object>` is no longer generated.
    @Test
    public void testIssue64_TextendsObject() {
        assertThat(emptySetUml).as("EmptySet class diagram")
                .doesNotContain("EmptySet<T extends Object>")
                .contains("EmptySet<T>");
    }

    /// Test [bugfix 82](https://github.com/talsma-ict/umldoclet/issues/82)
    /// to check correct names of inner-classes.
    @Test
    public void testIssue82_ContainingClassReference() {
        assertThat(emptySetUml).as("EmptySet class diagram")
                .contains(Issue64ExtendsObjectTest.class.getName()
                        + " +-- "
                        + EmptySet.class.getName().replace('$', '.'));
    }

}
