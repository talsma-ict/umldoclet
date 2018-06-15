/*
 * Copyright 2016-2018 Talsma ICT
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
import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.not;

/**
 * Test correct substitution of JavaBean properties by UML references.
 *
 * @author Sjoerd Talsma
 */
public class Issue30Test {

    // Method that should not be seen as a bean property.
    public Issue30Test getSomeValue(Boolean withArgument) {
        return this;
    }

    // Method that SHOULD be seen as a bean property.
    public Issue30Test getSomeProperty() {
        return this;
    }

    public void setSomeProperty(Issue30Test someProperty) {
    }

    @Test
    public void testIssue30() {
        String packageAsPath = getClass().getPackage().getName().replace('.', '/');
        String classAsPath = packageAsPath + "/" + getClass().getSimpleName();
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/test-30",
                "-doclet", UMLDoclet.class.getName(),
                "src/test/java/" + classAsPath + ".java"
        );
        String uml = Testing.read(new File("target/test-30/" + packageAsPath + "/package.puml"));

        String name = getClass().getName();
        String simpleName = getClass().getSimpleName();

        // someProperty should have been replaced by referene:
        assertThat(uml, not(containsString("+getSomeProperty()")));
        assertThat(uml, not(containsString("+setSomeProperty")));
        assertThat(uml, either(containsString(simpleName + " --> " + simpleName + ": someProperty"))
                .or(containsString(name + " --> " + name + ": someProperty")));

        // someValue must not be replaced by reference:
        assertThat(uml, containsString("+getSomeValue(Boolean): " + simpleName));
        assertThat(uml, not(either(containsString(simpleName + " --> " + simpleName + ": someValue"))
                .or(containsString(name + " --> " + name + ": someValue"))));
    }

}
