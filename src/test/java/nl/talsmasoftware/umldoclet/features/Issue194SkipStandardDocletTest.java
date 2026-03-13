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
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

class Issue194SkipStandardDocletTest {
    static final File outputdir = new File("target/issues/194");

    @Test
    void testNoStandardDocletDelegation() {
        int javadocResult = ToolProvider.findFirst("javadoc")
                .orElseGet(() -> fail("No javadoc implementation available"))
                .run(System.out, System.err,
                        "-d", outputdir.getPath(),
                        "-sourcepath", "src/test/java",
                        "-doclet", UMLDoclet.class.getName(),
                        "-quiet",
                        "--delegate-doclet", "false",
                        "--uml-image-directory", ".",
                        Issue194SkipStandardDocletTest.class.getPackageName());
        assertThat(javadocResult).as("Javadoc result").isZero();

        assertThat(new File(outputdir, "index.html")).doesNotExist();
    }

    @Test
    void testUsupportedDelegateDoclet() {
        int javadocResult = ToolProvider.findFirst("javadoc")
                .orElseGet(() -> fail("No javadoc implementation available"))
                .run(System.out, System.err,
                        "-d", outputdir.getPath(),
                        "-sourcepath", "src/test/java",
                        "-doclet", UMLDoclet.class.getName(),
                        "-quiet",
                        "--delegate-doclet", "foo.bar.DummyDoclet",
                        Issue194SkipStandardDocletTest.class.getPackageName());
        assertThat(javadocResult).as("Delegate doclets currently unsupported").isOne();
    }
}
