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
package nl.talsmasoftware.umldoclet.issues.bug146;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

class Bug146SkipSuperclassTest {
    static final String packageAsPath = Bug146SkipSuperclassTest.class.getPackageName().replace('.', '/');
    static final File outputdir = new File("target/issues/146");
    static String classUml;
    static String packageUml;

    @BeforeAll
    static void generateJavadoc() {
        String classAsPath = packageAsPath + '/' + PublicTestClass.class.getSimpleName();
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + classAsPath + ".java"
        );
        classUml = TestUtil.read(new File(outputdir, classAsPath + ".puml"));
        packageUml = TestUtil.read(new File(outputdir, packageAsPath + "/package.puml"));
    }

    @Test
    void testPackageProtectedSuperclassShouldBeSkipped() {
        assertThat(packageUml)
                .contains("java.util::AbstractList <|-- " + getClass().getPackageName() + "::PublicTestClass")
                .doesNotContain("PackageProtectedSuperclass");

        assertThat(classUml)
                .contains("java.util.AbstractList <|-- nl.talsmasoftware.umldoclet.issues.bug146.PublicTestClass")
                .doesNotContain("nl.talsmasoftware.umldoclet.issues.bug146.PackageProtectedSuperclass " +
                        "<|-- nl.talsmasoftware.umldoclet.issues.bug146.PublicTestClass");
    }

}
