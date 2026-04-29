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

import java.io.File;
import java.util.Optional;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Test for fix of [bug 97](https://github.com/talsma-ict/umldoclet/issues/97) generics that have equivalent html tags.
public class Bug79GenericsAsMarkupTest {
    private static final String packageAsPath = Bug79GenericsAsMarkupTest.class.getPackageName().replace('.', '/');
    private static final File outputdir = new File("target/issues/79");
    private static String classUml;

    /// Method for testing no underline HTML tag gets rendered as generic.
    /// @param <U> The generic type alias `U` to test difference with HTML underline tag.
    /// @return Always empty
    public <U> Optional<U> underlineMarkup() {
        return Optional.empty();
    }

    /// Method for testing no italic HTML tag gets rendered as generic.
    /// @param <I> The generic type alias `I` to test difference with HTML underline tag.
    /// @return Always empty
    public <I> Optional<I> italicMarkup() {
        return Optional.empty();
    }

    /// Method for testing no bold HTML tag gets rendered as generic.
    /// @param <B> The generic type alias `B` to test difference with HTML bold tag.
    /// @return Always empty
    public <B> Optional<B> boldMarkup() {
        return Optional.empty();
    }

    /// Set-up to create Javadoc and UML Diagrams for this test.
    @BeforeAll
    public static void createJavadoc() {
        String classAsPath = packageAsPath + '/' + Bug79GenericsAsMarkupTest.class.getSimpleName();
        int javadocResult = ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + Bug79GenericsAsMarkupTest.class.getName().replace('.', '/') + ".java"
        );
        assertThat(javadocResult).as("Javadoc result").isZero();
        classUml = TestUtil.read(new File(outputdir, classAsPath + ".puml"));
    }

    /// Default constructor.
    Bug79GenericsAsMarkupTest() {
        super();
    }

    /// Test that the generated UML generics do not contain HTML tag values.
    @Test
    public void testNoMarkup() {
        assertThat(classUml).as("Class diagram UML")
                .doesNotContain("Optional<U>", "Optional<I>", "Optional<B>");

        String stripped = classUml.replace('\u200B', '?'); // Make zero-width-space 'visible' for test
        assertThat(stripped).as("Class diagram with zero-width-space visible")
                .contains("Optional<?U>", "Optional<?I>", "Optional<?B>");
    }

}
