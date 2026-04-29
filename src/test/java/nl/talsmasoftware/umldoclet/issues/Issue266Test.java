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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.spi.ToolProvider;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/// Test for [issue 266](https://github.com/talsma-ict/umldoclet/issues/266) JavaBean property detection.
public class Issue266Test {
    private static final String packageAsPath = Issue266Test.class.getPackageName().replace('.', '/');
    private static final File outputDir = new File("target/issues/266");
    private static String classUml;
    private static String packageUml;

    /// Set-up to generate Javadoc and UML Diagrams for this test.
    @BeforeAll
    public static void prepareJavadocWithPumlFiles() {
        String classAsPath = packageAsPath + "/Issue266Test.TesterUtil";
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "--create-puml-files",
                "src/test/java/" + packageAsPath + "/Issue266Test.java"
        );
        classUml = TestUtil.read(new File(outputDir, classAsPath + ".puml"));
        packageUml = TestUtil.read(new File(outputDir, packageAsPath + "/package.puml"));
    }

    /// Default constructor.
    Issue266Test() {
        super();
    }

    /// Inner class to be documented to check that no incorrect property is detected.
    public static class TesterUtil {
        /// Default constructor.
        TesterUtil() {
            super();
        }

        /// Method that should _not_ be renddered as a property.
        /// @param testers vararg parameter
        /// @return a return value.
        public static Set<TesterUtil> setOf(TesterUtil... testers) {
            return Collections.unmodifiableSet(new LinkedHashSet<>(asList(testers)));
        }
    }

    /// Test that non-property `set` methods do not get rendered as property.
    @Test
    public void testBug266Rendering() {
        // verify that no 'of' relation was rendered.
        assertThat(classUml).as("Class diagram").doesNotContain(": of");
        assertThat(packageUml).as("Package diagram").doesNotContain(": of");
    }
}
