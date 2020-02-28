/*
 * Copyright 2016-2020 Talsma ICT
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

import static nl.talsmasoftware.umldoclet.testing.PatternMatcher.containsPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * @author Sjoerd Talsma
 */
@Deprecated
public class Issue71DeprecationTest {
    private static final String packageAsPath = Issue71DeprecationTest.class.getPackageName().replace('.', '/');
    private static final File outputDir = new File("target/issues/71");
    private static String classUml;
    private static String packageUml;

    @Deprecated
    public String deprecatedByAnnotation;

    /**
     * @deprecated Testing deprecation by JavaDoc tag with a comment.
     */
    public String deprecatedByJavadocTag;

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

    @Test
    public void testClassDeprecatedByAnnotation() {
        assertThat(classUml, containsPattern("class (\".*\" as )?" + getClass().getName() + " <<deprecated>>"));
    }

    @Test
    public void testClassDeprecatedByJavadoc() {
        assertThat(packageUml, containsString("class " + getClass().getName() + ".MoreDeprecation <<deprecated>>"));
    }

    @Test
    public void testConstructorDeprecatedByAnnotation() {
        assertThat(packageUml, containsString("+--MoreDeprecation--(String)"));
    }

    @Test
    public void testConstructorDeprecatedByJavadoc() {
        assertThat(packageUml, containsString("+--MoreDeprecation--()"));
    }

    @Test
    public void testFieldDeprecatedByAnnotation() {
        assertThat(classUml, containsString("+--deprecatedByAnnotation--: String"));
    }

    @Test
    public void testFieldDeprecatedByJavadoc() {
        assertThat(classUml, containsString("+--deprecatedByJavadocTag--: String"));
    }

    @Test
    public void testMethodDeprecatedByAnnotation() {
        assertThat(classUml, containsString("+--getDeprecatedByAnnotation--(): void"));
    }

    @Test
    public void testMethodDeprecatedByJavadoc() {
        assertThat(classUml, containsString("+--getDeprecatedByJavadocTag--(): void"));
    }

    @Test
    public void testIssue73InnerClassImageName() {
        File innerClassFile = new File(outputDir, "nl/talsmasoftware/umldoclet/issues/Issue71DeprecationTest.MoreDeprecation.svg");
        assertThat(innerClassFile + " exists?", innerClassFile.exists(), is(true));
    }

    /**
     * @deprecated Testing deprecation by JavaDoc tag with a comment.
     */
    public static class MoreDeprecation {
        /**
         * @deprecated Testing deprecation by JavaDoc tag with a comment.
         */
        public MoreDeprecation() {
            this(null);
        }

        @SuppressWarnings("unused")
        @Deprecated
        public MoreDeprecation(String ignored) {
            // Empty method to test UML generation.
        }
    }
}
