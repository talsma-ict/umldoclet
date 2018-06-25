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
package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.BeforeClass;
import org.junit.Test;

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
    private static final File outputdir = new File("target/test-71");
    private static String classUml, packageUml;

    @Deprecated
    public String deprecatedByAnnotation;

    /**
     * @deprecated Testing deprecation by JavaDoc tag with a comment.
     */
    public String deprecatedByJavadocTag;

    @Deprecated
    public void getDeprecatedByAnnotation() {
    }

    /**
     * @deprecated Testing deprecation by JavaDoc tag with a comment.
     */
    public void getDeprecatedByJavadocTag() {
    }

    @BeforeClass
    public static void setup() {
        String classAsPath = packageAsPath + '/' + Issue71DeprecationTest.class.getSimpleName();
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "src/test/java/" + classAsPath + ".java"
        );
        classUml = Testing.read(new File(outputdir, classAsPath + ".puml"));
        packageUml = Testing.read(new File(outputdir, packageAsPath + "/package.puml"));
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
    public void testIssue73_innerClassImageName() {
        File innerClassFile = new File(outputdir, "nl/talsmasoftware/umldoclet/issues/Issue71DeprecationTest.MoreDeprecation.svg");
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

        @Deprecated
        public MoreDeprecation(String ignored) {
        }
    }
}
