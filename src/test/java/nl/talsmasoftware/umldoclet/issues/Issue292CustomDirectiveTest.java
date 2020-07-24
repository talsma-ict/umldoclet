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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class Issue292CustomDirectiveTest {
    private static final String packageAsPath = Issue292CustomDirectiveTest.class.getPackageName().replace('.', '/');
    private static final File outputDir = new File("target/issues/292");
    private static String classUml;
    private static String packageUml;
    private static String packageDependenciesUml;

    @BeforeAll
    public static void prepareJavadocWithPumlFiles() {
        String classAsPath = packageAsPath + '/' + Issue292CustomDirectiveTest.class.getSimpleName();
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "--create-puml-files",
                "--uml-custom-directive", "!pragma graphviz_dot jdot",
                "src/test/java/" + classAsPath + ".java"
        );
        classUml = TestUtil.read(new File(outputDir, classAsPath + ".puml"));
        packageUml = TestUtil.read(new File(outputDir, packageAsPath + "/package.puml"));
        packageDependenciesUml = TestUtil.read(new File(outputDir, "package-dependencies.puml"));
    }

    @Test
    public void testPragmaInClassDiagram() {
        assertThat(classUml, containsString("!pragma graphviz_dot jdot"));
    }

    @Test
    public void testPragmaInPackageDiagram() {
        assertThat(packageUml, containsString("!pragma graphviz_dot jdot"));
    }

    @Test
    public void testPragmaInPackageDependenciesDiagram() {
        assertThat(packageDependenciesUml, containsString("!pragma graphviz_dot jdot"));
    }

}
