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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.spi.ToolProvider;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class Issue266Test {
    private static final String packageAsPath = Issue266Test.class.getPackageName().replace('.', '/');
    private static final File outputDir = new File("target/issues/266");
    private static String classUml;
    private static String packageUml;

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

    public static class TesterUtil {
        public static Set<TesterUtil> setOf(TesterUtil... testers) {
            return Collections.unmodifiableSet(new LinkedHashSet<>(asList(testers)));
        }
    }

    @Test
    public void testBug266Rendering() {
        // verify that no 'of' relation was rendered.
        assertThat(classUml, not(containsString(": of")));
        assertThat(packageUml, not(containsString(": of")));
    }
}
