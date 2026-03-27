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
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Test correct substitution of JavaBean properties by UML references.
///
/// @author Sjoerd Talsma
public class Issue30JavaBeanPropertiesTest {
    /// Default constructor.
    public Issue30JavaBeanPropertiesTest() {
        super();
    }

    /// Method that should not be seen as a bean property.
    ///
    /// @param withArgument The argument that prevents the method from getting detected as getter.
    /// @return `this` as a return value.
    public Issue30JavaBeanPropertiesTest getSomeValue(Boolean withArgument) {
        return this;
    }

    /// Method that SHOULD be seen as a bean property.
    ///
    /// @return `this` as a return value.
    public Issue30JavaBeanPropertiesTest getSomeProperty() {
        return this;
    }

    /// Public setter.
    ///
    /// @param someProperty some public property.
    public void setSomeProperty(Issue30JavaBeanPropertiesTest someProperty) {
        // Empty body, just to simulate a setter method
    }

    /// Test correct property detection.
    @Test
    public void testIssue30() {
        String packageAsPath = getClass().getPackage().getName().replace('.', '/');
        String classAsPath = packageAsPath + "/" + getClass().getSimpleName();
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/issues/30",
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + classAsPath + ".java"
        );
        String uml = TestUtil.read(new File("target/issues/30/" + packageAsPath + "/package.puml"));

        String simpleName = getClass().getSimpleName();
        String nameInPackage = getClass().getPackageName() + "::" + simpleName;

        // someProperty should have been replaced by referene:
        assertThat(uml).as("Package UML diagram")
                .doesNotContain("+getSomeProperty()", "+setSomeProperty")
                .containsAnyOf(simpleName + " --> " + simpleName + ": someProperty",
                        nameInPackage + " --> " + nameInPackage + ": someProperty")
                // someValue must not be replaced by reference:
                .contains("+getSomeValue(Boolean): " + simpleName)
                .doesNotContain(simpleName + " --> " + simpleName + ": someValue",
                        nameInPackage + " --> " + nameInPackage + ": someValue");
    }

}
