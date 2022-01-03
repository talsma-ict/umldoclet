/*
 * Copyright 2016-2022 Talsma ICT
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class PackageDependenciesTest {
    private static final File testDir = new File("target/test-uml/package-dependencies");

    @Test
    public void testPackageDependenciesDefaultExclusions() {
        File output = TestUtil.createDirectory(new File(testDir, "default-exclusions"));
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", output.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "-umlImageFormat", "none",
                "src/main/java/" + DependenciesElementScanner.class.getName().replace('.', '/') + ".java"
        ), is(0));

        String puml = TestUtil.read(new File(output, "package-dependencies.puml"));
        assertThat(puml, containsString("nl.talsmasoftware.umldoclet.javadoc.dependencies --> jdk.javadoc.doclet"));
        assertThat(puml, not(containsString("java.lang")));
        assertThat(puml, not(containsString("javax.lang")));
        assertThat(puml, not(containsString("java.util")));
    }

    @Test
    public void testPackageDependenciesWithoutExclusions() {
        File output = TestUtil.createDirectory(new File(testDir, "without-exclusions"));
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", output.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "-umlImageFormat", "none",
                "-umlExcludedPackageDependencies", "",
                "src/main/java/" + DependenciesElementScanner.class.getName().replace('.', '/') + ".java"
        ), is(0));

        String puml = TestUtil.read(new File(output, "package-dependencies.puml"));
        assertThat(puml, containsString("nl.talsmasoftware.umldoclet.javadoc.dependencies --> java.lang"));
        assertThat(puml, containsString("nl.talsmasoftware.umldoclet.javadoc.dependencies --> javax.lang.model.type"));
        assertThat(puml, containsString("nl.talsmasoftware.umldoclet.javadoc.dependencies --> jdk.javadoc.doclet"));
        assertThat(puml, containsString("nl.talsmasoftware.umldoclet.javadoc.dependencies --> java.util"));
    }

    @Test
    public void testPackageDependenciesCustomExclusions() {
        File output = TestUtil.createDirectory(new File(testDir, "custom-exclusions"));
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", output.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "-umlImageFormat", "none",
                "-umlExcludedPackageDependencies", "jdk.javadoc",
                "src/main/java/" + DependenciesElementScanner.class.getName().replace('.', '/') + ".java"
        ), is(0));

        String puml = TestUtil.read(new File(output, "package-dependencies.puml"));
        assertThat(puml, not(containsString("jdk.javadoc")));
        assertThat(puml, containsString("nl.talsmasoftware.umldoclet.javadoc.dependencies --> java.lang"));
        assertThat(puml, containsString("nl.talsmasoftware.umldoclet.javadoc.dependencies --> javax.lang.model.type"));
        assertThat(puml, containsString("nl.talsmasoftware.umldoclet.javadoc.dependencies --> java.util"));
    }

    @Test
    public void testPackageDependenciesIncludeExceptions() throws TestException {
        String expectedPackageDependency = getClass().getPackageName() + " --> " + TestException.class.getPackageName();
        File output = TestUtil.createDirectory(new File(testDir, "exception-dependencies"));
        assertThat("Javadoc reult", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", output.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "src/test/java/" +PackageDependenciesTest.class.getName().replace('.', '/') + ".java"
        ), is(0));

        String puml = TestUtil.read(new File(output, "package-dependencies.puml"));
        assertThat(puml, containsString(expectedPackageDependency));
    }

}
