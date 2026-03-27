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
package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.javadoc.dependencies.exceptions.TestException;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Test for package dependencies.
public class PackageDependenciesTest {
    /// Directory to write Javadoc and UML Diagrams to for this test.
    private static final File testDir = new File("target/test-uml/package-dependencies");

    /// Default constructor.
    public PackageDependenciesTest() {
        super();
    }

    /// Test the default exclusions for package diagrams.
    @Test
    public void testPackageDependenciesDefaultExclusions() {
        File output = TestUtil.createDirectory(new File(testDir, "default-exclusions"));
        int javadocResult = ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", output.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "-umlImageFormat", "none",
                "src/main/java/" + DependenciesElementScanner.class.getName().replace('.', '/') + ".java"
        );
        assertThat(javadocResult).as("Javadoc result").isZero();

        assertThat(TestUtil.read(new File(output, "package-dependencies.puml")))
                .as("Package dependencies")
                .contains("nl.talsmasoftware.umldoclet.javadoc.dependencies --> jdk.javadoc.doclet")
                .doesNotContain("java.lang", "javax.lang", "java.util");
    }

    /// Test package dependencies without exclusions.
    @Test
    public void testPackageDependenciesWithoutExclusions() {
        File output = TestUtil.createDirectory(new File(testDir, "without-exclusions"));
        int javadocResult = ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", output.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "-umlImageFormat", "none",
                "-umlExcludedPackageDependencies", "",
                "src/main/java/" + DependenciesElementScanner.class.getName().replace('.', '/') + ".java"
        );
        assertThat(javadocResult).as("Javadoc result").isZero();

        assertThat(TestUtil.read(new File(output, "package-dependencies.puml"))).as("Package dependencies")
                .contains("nl.talsmasoftware.umldoclet.javadoc.dependencies --> java.lang",
                        "nl.talsmasoftware.umldoclet.javadoc.dependencies --> javax.lang.model.type",
                        "nl.talsmasoftware.umldoclet.javadoc.dependencies --> jdk.javadoc.doclet",
                        "nl.talsmasoftware.umldoclet.javadoc.dependencies --> java.util");
    }

    /// Test package dependencies with custom exclusions.
    @Test
    public void testPackageDependenciesCustomExclusions() {
        File output = TestUtil.createDirectory(new File(testDir, "custom-exclusions"));
        int javadocResult = ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", output.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "-umlImageFormat", "none",
                "-umlExcludedPackageDependencies", "jdk.javadoc",
                "src/main/java/" + DependenciesElementScanner.class.getName().replace('.', '/') + ".java"
        );
        assertThat(javadocResult).as("Javadoc result").isZero();

        assertThat(TestUtil.read(new File(output, "package-dependencies.puml"))).as("Package dependencies")
                .doesNotContain("jdk.javadoc")
                .contains("nl.talsmasoftware.umldoclet.javadoc.dependencies --> java.lang",
                        "nl.talsmasoftware.umldoclet.javadoc.dependencies --> javax.lang.model.type",
                        "nl.talsmasoftware.umldoclet.javadoc.dependencies --> java.util");
    }

    /// Test that package dependencies include declared exception packages.
    @Test
    public void testPackageDependenciesIncludeExceptions() throws TestException {
        String expectedPackageDependency = getClass().getPackageName() + " --> " + TestException.class.getPackageName();
        File output = TestUtil.createDirectory(new File(testDir, "exception-dependencies"));
        int javadocResult = ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", output.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "src/test/java/" + PackageDependenciesTest.class.getName().replace('.', '/') + ".java"
        );
        assertThat(javadocResult).as("Javadoc result").isZero();

        assertThat(TestUtil.read(new File(output, "package-dependencies.puml"))).as("Package dependencies")
                .contains(expectedPackageDependency);
    }

}
