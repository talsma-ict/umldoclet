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
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Test fix for bug creating syntax error for children in annotations.
public class Bug276AnnotationSyntaxErrorTest {
    /// Test annotation with members.
    @Target(ElementType.TYPE)
    public @interface Generated {
        /// Default 'value' field of annotation.
        ///
        /// @return the values.
        String[] value();

        /// Extra `date` field of annotation.
        ///
        /// @return the date string.
        String date();

        /// Extra `comments` field of annotations.
        ///
        /// @return the comments string.
        String comments();
    }

    /// Set-up, generate Javadoc and UML Diagrams for this test.
    @BeforeAll
    static void generateUml() {
        int javadocResult = ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/issues/276",
                "-doclet", UMLDoclet.class.getName(),
                "-createPumlFiles",
                "src/test/java/nl/talsmasoftware/umldoclet/issues/Bug276AnnotationSyntaxErrorTest.java"
        );
        assertThat(javadocResult).as("Javadoc result").isZero();
    }

    /// Default constructor.
    Bug276AnnotationSyntaxErrorTest() {
        super();
    }

    /// Test that the generated UML diagram does not contain members in the annotations.
    @Test
    void testAnnotationDiagramHasNoSyntaxError() {
        String pkgPath = "target/issues/276/" + getClass().getPackageName().replace('.', '/');
        String svgFileData = TestUtil.read(new File(pkgPath + "/Bug276AnnotationSyntaxErrorTest.Generated.svg"));
        assertThat(svgFileData).as("Generated svg file").doesNotContain("Syntax Error");
    }

}
