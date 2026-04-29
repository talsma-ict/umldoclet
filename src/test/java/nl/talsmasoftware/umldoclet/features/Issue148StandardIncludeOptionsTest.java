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
import java.util.ArrayList;
import java.util.List;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/// Test the include options by the Standard doclet.
///
///
/// Verify that the visibility is interpreted the same by the UML doclet.
///
///
/// The options to be supported:
/// <ul>
/// <li>`-package`
/// <li>`-private`
/// <li>`-protected`
/// <li>`-public`
/// <li>`--show-types [private|protected|package|all]`
/// <li>`--show-members [private|protected|package|all]`
/// </ul>
///
/// @author Sjoerd Talsma
class Issue148StandardIncludeOptionsTest {
    /// Name of the java package being tested.
    static final String PACKAGE_NAME = Issue148StandardIncludeOptionsTest.class.getPackageName();
    /// Output directory to write Javadoc and UML Diagrams to.
    static final File OUTPUT_DIRECTORY = new File("target/issues/148");

    /// Utility method to create Javadoc with custom options.
    ///
    /// @param options Custom options to use for Javadoc creation.
    /// @return the directory containing the Javadoc and UML Diagrams.
    static File createJavadoc(String... options) {
        File dir = new File(OUTPUT_DIRECTORY, String.join("-", options));
        List<String> args = new ArrayList<>(asList(
                "-d", dir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles"
        ));
        args.addAll(asList(options));
        args.addAll(asList(
                "-sourcepath", "src/test/java",
                PACKAGE_NAME
        ));
        assertThat(ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                args.toArray(new String[0])
        )).as("Javadoc result").isZero();

        File result = new File(dir, PACKAGE_NAME.replace('.', '/'));
        Stream.of("Access.PrivateClass",
                        "PackageProtectedClass",
                        "Access.ProtectedClass",
                        "PublicClass")
                .forEach(className -> assertThat(new File(result, className + ".puml").exists())
                        .as("File " + className + ".uml exists?")
                        .isEqualTo(new File(result, className + ".html").exists()));
        return result;
    }

    /// Default constructor.
    Issue148StandardIncludeOptionsTest() {
        super();
    }

    /// Test the `-private` option.
    @Test
    void testOptionPrivate() {
        File dir = createJavadoc("-private");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package UML")
                .contains("[[Access.PrivateClass.html]]",
                        "[[PackageProtectedClass.html]]",
                        "[[Access.ProtectedClass.html]]",
                        "[[PublicClass.html]]")
                .contains("-privateField", "~packageProtectedField", "#protectedField", "+publicField")
                .contains("-getPrivateValue()", "~getPackageProtectedValue()", "#getProtectedValue()", "+getPublicValue()");

        String privateClassUml = TestUtil.read(new File(dir, "Access.PrivateClass.puml"));
        assertThat(privateClassUml).as("Private class UML")
                .contains("-privateField", "~packageProtectedField", "#protectedField", "+publicField")
                .contains("-getPrivateValue()", "~getPackageProtectedValue()", "#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `-package` option.
    @Test
    void testOptionPackage() {
        File dir = createJavadoc("-package");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]")
                .contains("[[PackageProtectedClass.html]]", "[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .doesNotContain("-privateField")
                .contains("~packageProtectedField", "#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()")
                .contains("~getPackageProtectedValue()", "#getProtectedValue()", "+getPublicValue()");

        String packageProtectedClassUml = TestUtil.read(new File(dir, "PackageProtectedClass.puml"));
        assertThat(packageProtectedClassUml).as("Package Protected class UML")
                .doesNotContain("-privateField")
                .contains("~packageProtectedField", "#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()")
                .contains("~getPackageProtectedValue()", "#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `-protected` option.
    @Test
    void testOptionProtected() {
        File dir = createJavadoc("-protected");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]")
                .contains("[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");

        String protectedClassUml = TestUtil.read(new File(dir, "Access.ProtectedClass.puml"));
        assertThat(protectedClassUml).as("Protected class UML")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `-public` option.
    @Test
    void testOptionPublic() {
        File dir = createJavadoc("-public");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]", "[[Access.ProtectedClass.html]]")
                .contains("[[PublicClass.html]]")
                .doesNotContain("-privateField", "~packageProtectedField", "#protectedField")
                .contains("+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()", "#getProtectedValue()")
                .contains("+getPublicValue()");

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml).as("Class diagram UML")
                .doesNotContain("-privateField", "~packageProtectedField", "#protectedField")
                .contains("+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()", "#getProtectedValue()")
                .contains("+getPublicValue()");
    }

    /// Test the `--show-types private` option.
    @Test
    void testOptionShowTypesPrivate() {
        File dir = createJavadoc("--show-types", "private");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .contains("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]", "[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");

        String privateClassUml = TestUtil.read(new File(dir, "Access.PrivateClass.puml"));
        assertThat(privateClassUml).as("Class diagram UML")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `--show-types package` option.
    @Test
    void testOptionShowTypesPackage() {
        File dir = createJavadoc("--show-types", "package");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]")
                .contains("[[PackageProtectedClass.html]]", "[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");

        String packageProtectedClassUml = TestUtil.read(new File(dir, "PackageProtectedClass.puml"));
        assertThat(packageProtectedClassUml).as("Class diagram UML")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `--show-types protected` option.
    @Test
    void testOptionShowTypesProtected() {
        File dir = createJavadoc("--show-types", "protected");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]")
                .contains("[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");

        String protectedClassUml = TestUtil.read(new File(dir, "Access.ProtectedClass.puml"));
        assertThat(protectedClassUml).as("Class diagram UML")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `--show-types public` option.
    @Test
    void testOptionShowTypesPublic() {
        File dir = createJavadoc("--show-types", "public");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]", "[[Access.ProtectedClass.html]]")
                .contains("[[PublicClass.html]]")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml).as("Class diagram UML")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `--show-members private` option.
    @Test
    void testOptionShowMembersPrivate() {
        File dir = createJavadoc("--show-members", "private");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]")
                .contains("[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .contains("-privateField", "~packageProtectedField", "#protectedField", "+publicField")
                .contains("-getPrivateValue()", "~getPackageProtectedValue()", "#getProtectedValue()", "+getPublicValue()");

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml).as("Class diagram UML")
                .contains("-privateField", "~packageProtectedField", "#protectedField", "+publicField")
                .contains("-getPrivateValue()", "~getPackageProtectedValue()", "#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `--show-members package` option.
    @Test
    void testOptionShowMembersPackage() {
        File dir = createJavadoc("--show-members", "package");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]")
                .contains("[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .doesNotContain("-privateField")
                .contains("~packageProtectedField", "#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()")
                .contains("~getPackageProtectedValue()", "#getProtectedValue()", "+getPublicValue()");

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml).as("Class diagram UML")
                .doesNotContain("-privateField")
                .contains("~packageProtectedField", "#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()")
                .contains("~getPackageProtectedValue()", "#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `--show-members protected` option.
    @Test
    void testOptionShowMembersProtected() {
        File dir = createJavadoc("--show-members", "protected");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]")
                .contains("[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");

        String protectedClassUml = TestUtil.read(new File(dir, "Access.ProtectedClass.puml"));
        assertThat(protectedClassUml).as("Class diagram UML")
                .doesNotContain("-privateField", "~packageProtectedField")
                .contains("#protectedField", "+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()")
                .contains("#getProtectedValue()", "+getPublicValue()");
    }

    /// Test the `--show-members public` option.
    @Test
    void testOptionShowMembersPublic() {
        File dir = createJavadoc("--show-members", "public");
        String packageUml = TestUtil.read(new File(dir, "package.puml"));
        assertThat(packageUml).as("Package diagram UML")
                .doesNotContain("[[Access.PrivateClass.html]]", "[[PackageProtectedClass.html]]")
                .contains("[[Access.ProtectedClass.html]]", "[[PublicClass.html]]")
                .doesNotContain("-privateField", "~packageProtectedField", "#protectedField")
                .contains("+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()", "#getProtectedValue()")
                .contains("+getPublicValue()");

        String publicClassUml = TestUtil.read(new File(dir, "PublicClass.puml"));
        assertThat(publicClassUml).as("Class diagram UML")
                .doesNotContain("-privateField", "~packageProtectedField", "#protectedField")
                .contains("+publicField")
                .doesNotContain("-getPrivateValue()", "~getPackageProtectedValue()", "#getProtectedValue()")
                .contains("+getPublicValue()");
    }

}
