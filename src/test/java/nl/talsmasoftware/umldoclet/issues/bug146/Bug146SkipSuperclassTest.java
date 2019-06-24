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
package nl.talsmasoftware.umldoclet.issues.bug146;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class Bug146SkipSuperclassTest {
    private static final String packageAsPath = Bug146SkipSuperclassTest.class.getPackageName().replace('.', '/');
    private static final File outputdir = new File("target/issues/146");
    private static String classUml, packageUml;

    @BeforeClass
    public static void generateJavadoc() {
        String classAsPath = packageAsPath + '/' + PublicTestClass.class.getSimpleName();
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + classAsPath + ".java"
        );
        classUml = Testing.read(new File(outputdir, classAsPath + ".puml"));
        packageUml = Testing.read(new File(outputdir, packageAsPath + "/package.puml"));
    }

    @Test
    public void testPackageProtectedSuperclassShouldBeSkipped() {
        assertThat(packageUml, allOf(
                containsString("java.util.AbstractList <|-- PublicTestClass"),
                not(containsString("PackageProtectedSuperclass <|-- PublicTestClass"))
        ));
        assertThat(classUml, allOf(
                containsString(
                        "java.util.AbstractList " +
                                "<|-- nl.talsmasoftware.umldoclet.issues.bug146.PublicTestClass"),
                not(containsString(
                        "nl.talsmasoftware.umldoclet.issues.bug146.PackageProtectedSuperclass " +
                                "<|-- nl.talsmasoftware.umldoclet.issues.bug146.PublicTestClass")
                )));
    }


}
