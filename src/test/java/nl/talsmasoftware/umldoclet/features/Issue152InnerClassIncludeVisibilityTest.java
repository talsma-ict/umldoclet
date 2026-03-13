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
package nl.talsmasoftware.umldoclet.features;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Issue152InnerClassIncludeVisibilityTest {
    static final String PACKAGE_NAME = Issue152InnerClassIncludeVisibilityTest.class.getPackageName();
    static final File OUTPUT_DIRECTORY = new File("target/issues/152");

    @Test
    void testPublicInnerClassVisibility() {
        assertThat(ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", OUTPUT_DIRECTORY.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "--show-types", "public",
                "-sourcepath", "src/test/java",
                PACKAGE_NAME
        )).as("Javadoc result").isZero();
        File dir = new File(OUTPUT_DIRECTORY, PACKAGE_NAME.replace('.', '/'));

        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package uml")
                .contains("PublicClass.PublicInnerClass")
                .doesNotContain("PublicClass.ProtectedInnerClass",
                        "PublicClass.PackageProtectedInnerClass",
                        "PublicClass.PrivateInnerClass");
        String classUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(classUml).as("Class diagram uml")
                .contains("PublicClass.PublicInnerClass")
                .doesNotContain("PublicClass.ProtectedInnerClass",
                        "PublicClass.PackageProtectedInnerClass",
                        "PublicClass.PrivateInnerClass");

        Stream.of(".html", ".puml", ".svg").forEach(extension -> {
            assertThat(new File(dir, "PublicClass.PublicInnerClass" + extension)).exists();
            assertThat(new File(dir, "PublicClass.ProtectedInnerClass" + extension)).doesNotExist();
            assertThat(new File(dir, "PublicClass.PackageProtectedInnerClass" + extension)).doesNotExist();
            assertThat(new File(dir, "PublicClass.PrivateInnerClass" + extension)).doesNotExist();
        });
    }
}
