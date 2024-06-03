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
package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * This is a test for <a href="https://github.com/talsma-ict/umldoclet/issues/245">bug 245</a> where
 * a sub-interface relationship is incorrectly rendered with a dotted line,
 * which should be a solid line instead.
 */
public class Bug245SubInterfaceTest {
    private static String umlFile;

    public interface ParentInterface {

    }

    public interface SubInterface extends ParentInterface {

    }

    public static final class Implementation implements ParentInterface {

    }

    @BeforeAll
    public static void generateUmlForIssue245() {
        String pathToTest = Bug245SubInterfaceTest.class.getName().replace('.', '/');
        assertThat(ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/issues/245",
                "-doclet", UMLDoclet.class.getName(),
                "-createPumlFiles",
                "src/test/java/" + pathToTest + ".java"
        ), is(0));

        umlFile = "target/issues/245/" + pathToTest + ".puml";
    }

    /**
     * Test that the bug is fixed; interfase extension with solid line, not dotted.
     */
    @Test
    public void testExtensionOfInterfaseWithSolidLine() {
        String uml = TestUtil.read(new File(umlFile.replace(".puml", ".SubInterface.puml")));
        assertThat("SubInterfase extends ParentInterface", uml,
                containsString(getClass().getName() + ".ParentInterface"
                        + " <|-- " + getClass().getName() + ".SubInterface"));
    }

    /**
     * Tests that there is no new regression bug from this fix.
     */
    @Test
    public void testImplementationOfInterfaceWithDottedLine() {
        String uml = TestUtil.read(new File(umlFile.replace(".puml", ".Implementation.puml")));
        assertThat("Implementation implements ParentInterface", uml,
                containsString(getClass().getName() + ".ParentInterface"
                        + " <|.. " + getClass().getName() + ".Implementation"));
    }

}
