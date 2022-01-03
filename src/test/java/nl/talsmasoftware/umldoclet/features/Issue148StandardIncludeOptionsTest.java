/*
 * Copyright 2016-2022 Talsma ICT
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
import java.util.ArrayList;
import java.util.List;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
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

        File result = new File(dir, packageName.replace('.', '/'));
        Stream.of("Access.PrivateClass",
                "PackageProtectedClass",
                "Access.ProtectedClass",
                "PublicClass")
                .forEach(className -> assertThat("File " + className + ".uml exists?",
                        new File(result, className + ".puml").exists(),
                        is(new File(result, className + ".html").exists())));
        return result;
    }

    @Test
    public void testOptionPrivate() {
        File dir = createJavadoc("-private");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                containsString("[[Access.PrivateClass.html]]"),
                containsString("[[PackageProtectedClass.html]]"),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, containsString("-privateField"));
        assertThat(packageUml, containsString("~packageProtectedField"));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, containsString("-getPrivateValue()"));
        assertThat(packageUml, containsString("~getPackageProtectedValue()"));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String privateClassUml = TestUtil.read(new File(dir, "Access.PrivateClass.puml"));
        assertThat(privateClassUml, containsString("-privateField"));
        assertThat(privateClassUml, containsString("~packageProtectedField"));
        assertThat(privateClassUml, containsString("#protectedField"));
        assertThat(privateClassUml, containsString("+publicField"));

        assertThat(privateClassUml, containsString("-getPrivateValue()"));
        assertThat(privateClassUml, containsString("~getPackageProtectedValue()"));
        assertThat(privateClassUml, containsString("#getProtectedValue()"));
        assertThat(privateClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionPackage() {
        File dir = createJavadoc("-package");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                containsString("[[PackageProtectedClass.html]]"),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, containsString("~packageProtectedField"));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, containsString("~getPackageProtectedValue()"));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String packageProtectedClassUml = TestUtil.read(new File(dir, "PackageProtectedClass.puml"));
        assertThat(packageProtectedClassUml, not(containsString("-privateField")));
        assertThat(packageProtectedClassUml, containsString("~packageProtectedField"));
        assertThat(packageProtectedClassUml, containsString("#protectedField"));
        assertThat(packageProtectedClassUml, containsString("+publicField"));

        assertThat(packageProtectedClassUml, not(containsString("-getPrivateValue()")));
        assertThat(packageProtectedClassUml, containsString("~getPackageProtectedValue()"));
        assertThat(packageProtectedClassUml, containsString("#getProtectedValue()"));
        assertThat(packageProtectedClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionProtected() {
        File dir = createJavadoc("-protected");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                not(containsString("[[PackageProtectedClass.html]]")),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, not(containsString("~packageProtectedField")));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String protectedClassUml = TestUtil.read(new File(dir, "Access.ProtectedClass.puml"));
        assertThat(protectedClassUml, not(containsString("-privateField")));
        assertThat(protectedClassUml, not(containsString("~packageProtectedField")));
        assertThat(protectedClassUml, containsString("#protectedField"));
        assertThat(protectedClassUml, containsString("+publicField"));

        assertThat(protectedClassUml, not(containsString("-getPrivateValue()")));
        assertThat(protectedClassUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(protectedClassUml, containsString("#getProtectedValue()"));
        assertThat(protectedClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionPublic() {
        File dir = createJavadoc("-public");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                not(containsString("[[PackageProtectedClass.html]]")),
                not(containsString("[[Access.ProtectedClass.html]]")),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, not(containsString("~packageProtectedField")));
        assertThat(packageUml, not(containsString("#protectedField")));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageUml, not(containsString("#getProtectedValue()")));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml, not(containsString("-privateField")));
        assertThat(publicClassUml, not(containsString("~packageProtectedField")));
        assertThat(publicClassUml, not(containsString("#protectedField")));
        assertThat(publicClassUml, containsString("+publicField"));

        assertThat(publicClassUml, not(containsString("-getPrivateValue()")));
        assertThat(publicClassUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(publicClassUml, not(containsString("#getProtectedValue()")));
        assertThat(publicClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionShowTypesPrivate() {
        File dir = createJavadoc("--show-types", "private");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                containsString("[[Access.PrivateClass.html]]"),
                containsString("[[PackageProtectedClass.html]]"),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, not(containsString("~packageProtectedField")));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String privateClassUml = TestUtil.read(new File(dir, "Access.PrivateClass.puml"));
        assertThat(privateClassUml, not(containsString("-privateField")));
        assertThat(privateClassUml, not(containsString("~packageProtectedField")));
        assertThat(privateClassUml, containsString("#protectedField"));
        assertThat(privateClassUml, containsString("+publicField"));

        assertThat(privateClassUml, not(containsString("-getPrivateValue()")));
        assertThat(privateClassUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(privateClassUml, containsString("#getProtectedValue()"));
        assertThat(privateClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionShowTypesPackage() {
        File dir = createJavadoc("--show-types", "package");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                containsString("[[PackageProtectedClass.html]]"),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, not(containsString("~packageProtectedField")));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String packageProtectedClassUml = TestUtil.read(new File(dir, "PackageProtectedClass.puml"));
        assertThat(packageProtectedClassUml, not(containsString("-privateField")));
        assertThat(packageProtectedClassUml, not(containsString("~packageProtectedField")));
        assertThat(packageProtectedClassUml, containsString("#protectedField"));
        assertThat(packageProtectedClassUml, containsString("+publicField"));

        assertThat(packageProtectedClassUml, not(containsString("-getPrivateValue()")));
        assertThat(packageProtectedClassUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageProtectedClassUml, containsString("#getProtectedValue()"));
        assertThat(packageProtectedClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionShowTypesProtected() {
        File dir = createJavadoc("--show-types", "protected");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                not(containsString("[[PackageProtectedClass.html]]")),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, not(containsString("~packageProtectedField")));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String protectedClassUml = TestUtil.read(new File(dir, "Access.ProtectedClass.puml"));
        assertThat(protectedClassUml, not(containsString("-privateField")));
        assertThat(protectedClassUml, not(containsString("~packageProtectedField")));
        assertThat(protectedClassUml, containsString("#protectedField"));
        assertThat(protectedClassUml, containsString("+publicField"));

        assertThat(protectedClassUml, not(containsString("-getPrivateValue()")));
        assertThat(protectedClassUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(protectedClassUml, containsString("#getProtectedValue()"));
        assertThat(protectedClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionShowTypesPublic() {
        File dir = createJavadoc("--show-types", "public");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                not(containsString("[[PackageProtectedClass.html]]")),
                not(containsString("[[Access.ProtectedClass.html]]")),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, not(containsString("~packageProtectedField")));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml, not(containsString("-privateField")));
        assertThat(publicClassUml, not(containsString("~packageProtectedField")));
        assertThat(publicClassUml, containsString("#protectedField"));
        assertThat(publicClassUml, containsString("+publicField"));

        assertThat(publicClassUml, not(containsString("-getPrivateValue()")));
        assertThat(publicClassUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(publicClassUml, containsString("#getProtectedValue()"));
        assertThat(publicClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionShowMembersPrivate() {
        File dir = createJavadoc("--show-members", "private");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                not(containsString("[[PackageProtectedClass.html]]")),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, containsString("-privateField"));
        assertThat(packageUml, containsString("~packageProtectedField"));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, containsString("-getPrivateValue()"));
        assertThat(packageUml, containsString("~getPackageProtectedValue()"));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml, containsString("-privateField"));
        assertThat(publicClassUml, containsString("~packageProtectedField"));
        assertThat(publicClassUml, containsString("#protectedField"));
        assertThat(publicClassUml, containsString("+publicField"));

        assertThat(publicClassUml, containsString("-getPrivateValue()"));
        assertThat(publicClassUml, containsString("~getPackageProtectedValue()"));
        assertThat(publicClassUml, containsString("#getProtectedValue()"));
        assertThat(publicClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionShowMembersPackage() {
        File dir = createJavadoc("--show-members", "package");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                not(containsString("[[PackageProtectedClass.html]]")),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, containsString("~packageProtectedField"));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, containsString("~getPackageProtectedValue()"));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml, not(containsString("-privateField")));
        assertThat(publicClassUml, containsString("~packageProtectedField"));
        assertThat(publicClassUml, containsString("#protectedField"));
        assertThat(publicClassUml, containsString("+publicField"));

        assertThat(publicClassUml, not(containsString("-getPrivateValue()")));
        assertThat(publicClassUml, containsString("~getPackageProtectedValue()"));
        assertThat(publicClassUml, containsString("#getProtectedValue()"));
        assertThat(publicClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionShowMembersProtected() {
        File dir = createJavadoc("--show-members", "protected");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                not(containsString("[[PackageProtectedClass.html]]")),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, not(containsString("~packageProtectedField")));
        assertThat(packageUml, containsString("#protectedField"));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageUml, containsString("#getProtectedValue()"));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String protectedClassUml = TestUtil.read(new File(dir, "Access.ProtectedClass.puml"));
        assertThat(protectedClassUml, not(containsString("-privateField")));
        assertThat(protectedClassUml, not(containsString("~packageProtectedField")));
        assertThat(protectedClassUml, containsString("#protectedField"));
        assertThat(protectedClassUml, containsString("+publicField"));

        assertThat(protectedClassUml, not(containsString("-getPrivateValue()")));
        assertThat(protectedClassUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(protectedClassUml, containsString("#getProtectedValue()"));
        assertThat(protectedClassUml, containsString("+getPublicValue()"));
    }

    @Test
    public void testOptionShowMembersPublic() {
        File dir = createJavadoc("--show-members", "public");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml, allOf(
                not(containsString("[[Access.PrivateClass.html]]")),
                not(containsString("[[PackageProtectedClass.html]]")),
                containsString("[[Access.ProtectedClass.html]]"),
                containsString("[[PublicClass.html]]")
        ));
        assertThat(packageUml, not(containsString("-privateField")));
        assertThat(packageUml, not(containsString("~packageProtectedField")));
        assertThat(packageUml, not(containsString("#protectedField")));
        assertThat(packageUml, containsString("+publicField"));

        assertThat(packageUml, not(containsString("-getPrivateValue()")));
        assertThat(packageUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(packageUml, not(containsString("#getProtectedValue()")));
        assertThat(packageUml, containsString("+getPublicValue()"));

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml, not(containsString("-privateField")));
        assertThat(publicClassUml, not(containsString("~packageProtectedField")));
        assertThat(publicClassUml, not(containsString("#protectedField")));
        assertThat(publicClassUml, containsString("+publicField"));

        assertThat(publicClassUml, not(containsString("-getPrivateValue()")));
        assertThat(publicClassUml, not(containsString("~getPackageProtectedValue()")));
        assertThat(publicClassUml, not(containsString("#getProtectedValue()")));
        assertThat(publicClassUml, containsString("+getPublicValue()"));
    }

}
