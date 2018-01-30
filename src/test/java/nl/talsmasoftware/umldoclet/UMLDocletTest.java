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
package nl.talsmasoftware.umldoclet;

import nl.talsmasoftware.umldoclet.javadoc.DocletConfig;
import nl.talsmasoftware.umldoclet.uml.UMLDiagram;
import org.junit.Test;

import java.util.spi.ToolProvider;

public class UMLDocletTest {

    ToolProvider javadoc = ToolProvider.findFirst("javadoc").get();

    @Test
    public void testDoclet() {
        this.javadoc.run(System.out, System.err,
                "-sourcepath", "src/main/java",
                "-d", "target/doclet-test",
                "-doclet", UMLDoclet.class.getName(),
//                Configuration.class.getPackageName(),
                UMLDoclet.class.getPackageName(),
                DocletConfig.class.getPackageName(),
                UMLDiagram.class.getPackageName()
        );
    }

}
