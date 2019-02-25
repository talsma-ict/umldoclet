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
package nl.talsmasoftware.umldoclet.features;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.spi.ToolProvider;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Test the include options by the Standard doclet.
 *
 * <p>
 * Verify that the visibility is interpreted the same by the UML doclet.
 *
 * <p>
 * The options to be supported:
 * <ul>
 * <li>{@code -package}
 * <li>{@code -private}
 * <li>{@code -protected}
 * <li>{@code -public}
 * <li>{@code --show-module-contents [api|all]}
 * <li>{@code --show-packages [exported|all]}
 * <li>{@code --show-types [private|protected|package|all]}
 * <li>{@code --show-members [private|protected|package|all]}
 * </ul>
 *
 * @author Sjoerd Talsma
 */
public class Issue148StandardIncludeOptionsTest {
    private static final String packageName = Issue148StandardIncludeOptionsTest.class.getPackageName();
    private static final File outputdir = new File("target/issues/148");

    private static File createJavadoc(String... options) {
        File dir = new File(outputdir, String.join("-", options));
        List<String> args = new ArrayList<>(asList(
                "-d", dir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles"
        ));
        args.addAll(asList(options));
        args.addAll(asList(
                "-sourcepath", "src/test/java",
                packageName
        ));
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                args.toArray(new String[0])
        ), is(0));
        return new File(dir, packageName.replace('.', '/'));
    }

    @Test
    public void testOptionPrivate() {
        File dir = createJavadoc("-private");
        String packageUml = Testing.read(new File(dir, "package.puml"));
        String privateClassUml = Testing.read(new File(dir, "Access.PrivateClass.puml"));

        assertThat(privateClassUml, containsString("+publicField"));
        assertThat(privateClassUml, containsString("#protectedField"));
        assertThat(privateClassUml, containsString("~packageProtectedField"));
        assertThat(privateClassUml, containsString("-privateField"));

        assertThat(privateClassUml, containsString("+getPublicValue()"));
        assertThat(privateClassUml, containsString("#getProtectedValue()"));
        assertThat(privateClassUml, containsString("~getPackageProtectedValue()"));
        assertThat(privateClassUml, containsString("-getPrivateValue()"));

        assertThat(packageUml, containsString(
                "class nl.talsmasoftware.umldoclet.features.Access.PrivateClass [[Access.PrivateClass.html]]"
        ));
        assertThat(packageUml, containsString("+publicField"));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("~packageProtectedField"));
        assertThat(packageUml, containsString("-privateField"));
        assertThat(packageUml, containsString("+getPublicValue()"));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("~getPackageProtectedValue()"));
        assertThat(packageUml, containsString("-getPrivateValue()"));
    }

    @Test
    public void testOptionProtected() {
        File dir = createJavadoc("-protected");
    }

    @Test
    public void testOptionPackage() {
        File dir = createJavadoc("-package");
    }

    @Test
    public void testOptionPublic() {
        File dir = createJavadoc("-public");
    }

    @Test
    public void testOptionShowModuleContentsApi() {
        File dir = createJavadoc("--show-module-contents", "api");
    }

    @Test
    public void testOptionShowModuleContentsAll() {
        File dir = createJavadoc("--show-module-contents", "all");
    }

    @Test
    public void testOptionShowPackagesExported() {
        File dir = createJavadoc("--show-packages", "exported");
    }

    @Test
    public void testOptionShowPackagesAll() {
        File dir = createJavadoc("--show-packages", "all");
    }

    @Test
    public void testOptionShowTypesPrivate() {
        File dir = createJavadoc("--show-types", "private");
    }

    @Test
    public void testOptionShowTypesProtected() {
        File dir = createJavadoc("--show-types", "protected");
    }

    @Test
    public void testOptionShowTypesPackage() {
        File dir = createJavadoc("--show-types", "package");
    }

    @Test
    public void testOptionShowTypesPublic() {
        File dir = createJavadoc("--show-types", "public");
    }

    @Test
    public void testOptionShowMembersPrivate() {
        File dir = createJavadoc("--show-members", "private");
        String packageUml = Testing.read(new File(dir, "package.puml"));
        assertThat(new File(dir, "Access.PrivateClass.puml").exists(), is(false));

        assertThat(packageUml, not(containsString("Access.PrivateClass")));
        assertThat(packageUml, containsString("+publicField"));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("~packageProtectedField"));
        assertThat(packageUml, containsString("-privateField"));
        assertThat(packageUml, containsString("+getPublicValue()"));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("~getPackageProtectedValue()"));
        assertThat(packageUml, containsString("-getPrivateValue()"));
    }

    @Test
    public void testOptionShowMembersProtected() {
        File dir = createJavadoc("--show-members", "protected");
    }

    @Test
    public void testOptionShowMembersPackage() {
        File dir = createJavadoc("--show-members", "package");
    }

    @Test
    public void testOptionShowMembersPublic() {
        File dir = createJavadoc("--show-members", "public");
    }

}
