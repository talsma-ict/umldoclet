/*
 * Copyright 2016-2024 Talsma ICT
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.io.FileMatchers.anExistingFileOrDirectory;

public class Issue194SkipStandardDocletTest {
    private static final File outputdir = new File("target/issues/194");

    @Test
    public void testNoStandardDocletDelegation() {
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-sourcepath", "src/test/java",
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "--delegate-doclet", "false",
                "--uml-image-directory", ".",
                Issue194SkipStandardDocletTest.class.getPackageName()
        ), is(0));

        assertThat(new File(outputdir, "index.html"), not(anExistingFileOrDirectory()));
    }

    @Test
    public void testUsupportedDelegateDoclet() {
        assertThat("Delegate doclets currently unsupported", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-sourcepath", "src/test/java",
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "--delegate-doclet", "foo.bar.DummyDoclet",
                Issue194SkipStandardDocletTest.class.getPackageName()
        ), is(1));
    }
}
