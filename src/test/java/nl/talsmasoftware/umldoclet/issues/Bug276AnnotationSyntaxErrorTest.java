/*
 * Copyright 2016-2021 Talsma ICT
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
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class Bug276AnnotationSyntaxErrorTest {

    @Target(ElementType.TYPE)
    public @interface Generated {
        String[] value();

        String date();

        String comments();
    }

    @BeforeAll
    static void generateUml() {
        assertThat(ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/issues/276",
                "-doclet", UMLDoclet.class.getName(),
                "-createPumlFiles",
                "src/test/java/nl/talsmasoftware/umldoclet/issues/Bug276AnnotationSyntaxErrorTest.java"
        ), is(0));
    }

    @Test
    void testAnnotationDiagramHasNoSyntaxError() {
        String pkgPath = "target/issues/276/" + getClass().getPackageName().replace('.', '/');
        assertThat(TestUtil.read(new File(pkgPath + "/Bug276AnnotationSyntaxErrorTest.Generated.svg")),
                not(containsString("Syntax Error")));
    }

}
