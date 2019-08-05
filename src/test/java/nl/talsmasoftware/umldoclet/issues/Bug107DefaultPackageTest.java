/*
 * Copyright 2016-2019 Talsma ICT
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
import org.junit.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class Bug107DefaultPackageTest {

    @Test
    public void testDefaultPackageDocumentation() {
        assertThat(ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/issues/107",
                "-doclet", UMLDoclet.class.getName(),
                "-createPumlFiles",
                "src/test/java/Foo.java"
        ), is(0));

        String uml = TestUtil.read(new File("target/issues/107/package.puml"));
        assertThat(uml, containsString("namespace unnamed"));
        assertThat(uml, containsString("class Foo"));
    }

}
