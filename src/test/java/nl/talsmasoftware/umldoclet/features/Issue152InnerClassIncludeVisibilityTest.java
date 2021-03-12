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
package nl.talsmasoftware.umldoclet.features;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class Issue152InnerClassIncludeVisibilityTest {
    private static final String packageName = Issue152InnerClassIncludeVisibilityTest.class.getPackageName();
    private static final File outputdir = new File("target/issues/152");

    @Test
    public void testPublicInnerClassVisibility() {
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "--show-types", "public",
                "-sourcepath", "src/test/java",
                packageName
        ), is(0));
        File dir = new File(outputdir, packageName.replace('.', '/'));

        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat("Package uml", packageUml, allOf(
                containsString("PublicClass.PublicInnerClass"),
                not(containsString("PublicClass.ProtectedInnerClass")),
                not(containsString("PublicClass.PackageProtectedInnerClass")),
                not(containsString("PublicClass.PrivateInnerClass"))
        ));
        String classUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat("Class uml", classUml, allOf(
                containsString("PublicClass.PublicInnerClass"),
                not(containsString("PublicClass.ProtectedInnerClass")),
                not(containsString("PublicClass.PackageProtectedInnerClass")),
                not(containsString("PublicClass.PrivateInnerClass"))
        ));
        Stream.of(".html", ".puml", ".svg").forEach(extension -> {
            assertThat("public innerclass " + extension, new File(dir, "PublicClass.PublicInnerClass" + extension).exists(), is(true));
            assertThat("protected innerclass " + extension, new File(dir, "PublicClass.ProtectedInnerClass" + extension).exists(), is(false));
            assertThat("package innerclass " + extension, new File(dir, "PublicClass.PackageProtectedInnerClass" + extension).exists(), is(false));
            assertThat("private innerclass " + extension, new File(dir, "PublicClass.PrivateInnerClass" + extension).exists(), is(false));
        });
    }
}
