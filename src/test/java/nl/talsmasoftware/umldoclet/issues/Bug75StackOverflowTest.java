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
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Comparator;
import java.util.function.Function;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Test fix for [bug 75](https://github.com/talsma-ict/umldoclet/issues/75).
public class Bug75StackOverflowTest {
    /// The package as a path with slashes.
    private static final String packageAsPath = Bug75StackOverflowTest.class.getPackageName().replace('.', '/');
    /// The directory to generate Javadoc and UML Diagrams for this test.
    private static final File outputDir = new File("target/issues/75");

    /// public interface defined as inner-class.
    public interface Comparable<T> {
        /// {@inheritDoc}
        <U extends Comparable<? super U>> Comparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor);
    }

    /// Default constructor.
    public Bug75StackOverflowTest() {
        super();
    }

    /// Test that infinite recursion is bounded to prevent StackOverflowException.
    @Test
    public void testInifiniteRecursionIsBounded() {
        String classAsPath = packageAsPath + '/' + Bug75StackOverflowTest.class.getSimpleName();
        int javadocResult = ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + Bug75StackOverflowTest.class.getName().replace('.', '/') + ".java"
        );
        assertThat(javadocResult).as("Javadoc result").isZero();
        TestUtil.read(new File(outputDir, classAsPath + ".puml"));
        String packageUml = TestUtil.read(new File(outputDir, packageAsPath + "/package.puml"));

        assertThat(packageUml).doesNotContain("? extends Comparable<? super Comparable<? super Comparable");
    }

}
