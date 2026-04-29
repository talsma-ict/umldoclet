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

/// Test for [feature 292](https://github.com/talsma-ict/umldoclet/issues/292)
/// to add custom lines to generated UML.
public class Issue292CustomDirectiveTest {
    private static final String packageAsPath = Issue292CustomDirectiveTest.class.getPackageName().replace('.', '/');
    private static final File outputDir = new File("target/issues/292");
    private static String classUml;
    private static String packageUml;
    private static String packageDependenciesUml;

    /// Default constructor.
    Issue292CustomDirectiveTest() {
        super();
    }

    /// Set-up generating diagrams for this test.
    @BeforeAll
    public static void prepareJavadocWithPumlFiles() {
        String classAsPath = packageAsPath + '/' + Issue292CustomDirectiveTest.class.getSimpleName();
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "--create-puml-files",
                "--uml-custom-directive", "skinparam handwritten true",
                "src/test/java/" + classAsPath + ".java"
        );
        classUml = TestUtil.read(new File(outputDir, classAsPath + ".puml"));
        packageUml = TestUtil.read(new File(outputDir, packageAsPath + "/package.puml"));
        packageDependenciesUml = TestUtil.read(new File(outputDir, "package-dependencies.puml"));
    }

    /// Test a custom directive in a class diagram.
    @Test
    public void testCustomDirectiveInClassDiagram() {
        assertThat(classUml).as("Class diagram").contains("skinparam handwritten true");
    }

    /// Test a custom directive in a package diagram.
    @Test
    public void testCustomDirectiveInPackageDiagram() {
        assertThat(packageUml).as("Package diagram").contains("skinparam handwritten true");
    }

    /// Test a custom directive in a package-dependencies diagram.
    @Test
    public void testCustomDirectiveInPackageDependenciesDiagram() {
        assertThat(packageDependenciesUml).as("Dependencies diagram").contains("skinparam handwritten true");
    }

}
