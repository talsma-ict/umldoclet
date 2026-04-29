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
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Test for deprecation [issue 71](https://github.com/talsma-ict/umldoclet/issues/71).
///
/// @author Sjoerd Talsma
@Deprecated
public class Issue71DeprecationTest {
    private static final String packageAsPath = Issue71DeprecationTest.class.getPackageName().replace('.', '/');
    private static final File outputDir = new File("target/issues/71");
    private static String classUml;
    private static String packageUml;

    /// Public field deprecated by java annotation, **no** Javadoc tag.
    @Deprecated
    public String deprecatedByAnnotation;

    /// Public static field deprecated by java annotation, **no** Javadoc tag.
    @Deprecated
    public static String deprecatedStaticField;

    /**
     * @deprecated Testing deprecation by JavaDoc tag with a comment.
     */
    public String deprecatedByJavadocTag;

    /// Default constructor.
    Issue71DeprecationTest() {
        super();
    }

    /// Public method deprecated by annotation, **no** Javadoc tag.
    @Deprecated
    public void getDeprecatedByAnnotation() {
        // Empty method to test UML generation.
    }

    /**
     * @deprecated Testing deprecation by JavaDoc tag with a comment.
     */
    public void getDeprecatedByJavadocTag() {
        // Empty method to test UML generation.
    }

    /**
     * @deprecated Testing deprecation of static method.
     */
    @Deprecated
    public static void deprecatedStaticMethod() {
        // Empty method to test UML generation.
    }

    /// Set-up generating Javadoc and UML Diagrams.
    @BeforeAll
    public static void prepareJavadocWithPumlFiles() {
        String classAsPath = packageAsPath + '/' + Issue71DeprecationTest.class.getSimpleName();
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + classAsPath + ".java"
        );
        classUml = TestUtil.read(new File(outputDir, classAsPath + ".puml"));
        packageUml = TestUtil.read(new File(outputDir, packageAsPath + "/package.puml"));
    }

    /// Test deprecated class by annotation.
    @Test
    public void testClassDeprecatedByAnnotation() {
        assertThat(classUml).as("Class uml diagram")
                .containsPattern("class (\".*\" as )?" + getClass().getName() + " <<deprecated>>");
    }

    /// Test deprecated class by Javadoc tag.
    @Test
    public void testClassDeprecatedByJavadoc() {
        assertThat(packageUml).as("Package uml diagram")
                .contains("class " + getClass().getSimpleName() + ".MoreDeprecation <<deprecated>>");
    }

    /// Test deprecated constructor by annotation.
    @Test
    public void testConstructorDeprecatedByAnnotation() {
        assertThat(packageUml).as("Package uml diagram").contains("+--MoreDeprecation--(String)");
    }

    /// Test deprecated constructor by Javadoc tag.
    @Test
    public void testConstructorDeprecatedByJavadoc() {
        assertThat(packageUml).as("Package diagram").contains("+--MoreDeprecation--()");
    }

    /// Test field deprecated by annotation.
    @Test
    public void testFieldDeprecatedByAnnotation() {
        assertThat(classUml).as("Class diagram").contains("+--deprecatedByAnnotation--: String");
    }

    /// Test field deprecated by Javadoc tag.
    @Test
    public void testFieldDeprecatedByJavadoc() {
        assertThat(classUml).as("Class diagram").contains("+--deprecatedByJavadocTag--: String");
    }

    /// Test method deprecated by annotation.
    @Test
    public void testMethodDeprecatedByAnnotation() {
        assertThat(classUml).as("Class diagram").contains("+--getDeprecatedByAnnotation--(): void");
    }

    /// Test method deprecated by Javadoc tag.
    @Test
    public void testMethodDeprecatedByJavadoc() {
        assertThat(classUml).as("Class diagram").contains("+--getDeprecatedByJavadocTag--(): void");
    }

    /// Test [issue 73](https://github.com/talsma-ict/umldoclet/issues/73) about the image name of inner classes.
    @Test
    public void testIssue73InnerClassImageName() {
        File innerClassFile = new File(outputDir, "nl/talsmasoftware/umldoclet/issues/Issue71DeprecationTest.MoreDeprecation.svg");
        assertThat(innerClassFile).exists();
    }

    /**
     * Inner class for more deprecation testing.
     *
     * @deprecated Testing deprecation by Javadoc tag with a comment.
     */
    public static class MoreDeprecation {
        /**
         * Default constructor for deprecation testing.
         *
         * @deprecated Testing deprecation by JavaDoc tag with a comment.
         */
        public MoreDeprecation() {
            this(null);
        }

        /// Constructor with parameter for deprecation tests.
        ///
        /// @param ignored The parameter.
        @SuppressWarnings("unused") // Used by UML Diagram generation.
        @Deprecated
        public MoreDeprecation(String ignored) {
            super();
        }
    }
}
