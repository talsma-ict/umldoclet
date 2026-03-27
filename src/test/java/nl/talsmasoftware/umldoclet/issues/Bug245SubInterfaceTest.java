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

/// This is a test for [bug 245](https://github.com/talsma-ict/umldoclet/issues/245) where
/// a sub-interface relationship is incorrectly rendered with a dotted line,
/// which should be a solid line instead.
public class Bug245SubInterfaceTest {
    /// The generated UML file in this test.
    private static String umlFile;

    /// A parent interface defined as inner-class.
    public interface ParentInterface {

    }

    /// A sub-interface defined as inner-class.
    public interface SubInterface extends ParentInterface {

    }

    /// An implementation inner-class.
    public static final class Implementation implements ParentInterface {
        /// Default constructor.
        public Implementation() {
            super();
        }
    }

    /// Set-up; generate UML diagram to test the bugfix.
    @BeforeAll
    public static void generateUmlForIssue245() {
        String pathToTest = Bug245SubInterfaceTest.class.getName().replace('.', '/');
        int javadocResult = ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/issues/245",
                "-doclet", UMLDoclet.class.getName(),
                "-createPumlFiles",
                "src/test/java/" + pathToTest + ".java"
        );
        assertThat(javadocResult).as("Javadoc result").isZero();

        umlFile = "target/issues/245/" + pathToTest + ".puml";
    }

    /// Default constructor.
    public Bug245SubInterfaceTest() {
        super();
    }

    /// Test that the bug is fixed; interfase extension with solid line, not dotted.
    @Test
    public void testExtensionOfInterfaceWithSolidLine() {
        String uml = TestUtil.read(new File(umlFile.replace(".puml", ".SubInterface.puml")));
        assertThat(uml).as("Subinterface UML diagram")
                .contains(getClass().getName() + ".ParentInterface" + " <|-- " + getClass().getName() + ".SubInterface");
    }

    /// Tests that there is no new regression bug from this fix.
    @Test
    public void testImplementationOfInterfaceWithDottedLine() {
        String uml = TestUtil.read(new File(umlFile.replace(".puml", ".Implementation.puml")));
        assertThat(uml).as("Implementation UML diagram")
                .contains(getClass().getName() + ".ParentInterface" + " <|.. " + getClass().getName() + ".Implementation");
    }

}
