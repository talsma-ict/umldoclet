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
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.spi.ToolProvider;

public class Bug107DefaultPackageTest {

    @BeforeClass
    public static void createJavadoc() {
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/test-25",
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageDirectory", "images",
                "src/test/java/Foo.java"
        );
    }

    @Test
    public void testDefaultPackageDocumentation() {

    }

}
