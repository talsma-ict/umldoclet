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
package nl.talsmasoftware.umldoclet.features;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.features.cycle.CyclicDependencyClass;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Class for cyclic dependency testing.
public class Feature182CyclicDependencyTest {
    /// Directory to write Javadoc and UML Diagrams to.
    private static final File outputdir = new File("target/issues/182");

    /// Default constructor.
    public Feature182CyclicDependencyTest() {
        super();
    }

    /// Method to intentionally cause a cyclic package dependency.
    /// @param ignored Just here to cause a cyclic package dependency.
    public void cycle(CyclicDependencyClass ignored) {
        // no-op, the parameter triggers a cyclic package dependency.
    }

    /// Test the warning about cyclic dependencies.
    /// @throws UnsupportedEncodingException If java doesn't support `UTF-8` encoding, i.e., never.
    @Test
    public void testCyclicDependencyWarning() throws UnsupportedEncodingException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String myPackage = Feature182CyclicDependencyTest.class.getPackageName();
        String cyclicPackage = CyclicDependencyClass.class.getPackageName();
        PrintStream err = new PrintStream(output);
        int resultcode = ToolProvider.findFirst("javadoc").get().run(
                System.out, err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "--show-types", "public",
                "-sourcepath", "src/test/java",
                myPackage, cyclicPackage
        );
        err.flush();
        assertThat(resultcode).as("Javadoc result").isZero();
        assertThat(output.toString("UTF-8")).contains(myPackage + " > " + cyclicPackage + " > " + myPackage);
    }

    /// Test the failure for cyclic package dependencies.
    /// @throws UnsupportedEncodingException If java doesn't support `UTF-8` encoding, i.e., never.
    @Test
    public void testCyclicDependencyFailure() throws UnsupportedEncodingException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String myPackage = Feature182CyclicDependencyTest.class.getPackageName();
        String cyclicPackage = CyclicDependencyClass.class.getPackageName();
        PrintStream err = new PrintStream(output);
        int resultcode = ToolProvider.findFirst("javadoc").get().run(
                System.out, err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "--show-types", "public",
                "-sourcepath", "src/test/java",
                "-failOnCyclicPackageDependencies", "true",
                myPackage, cyclicPackage
        );
        err.flush();
        assertThat(resultcode).as("Javadoc result").isOne();

        assertThat(output.toString("UTF-8")).contains(myPackage + " > " + cyclicPackage + " > " + myPackage);
    }

}
