/*
 * Copyright 2016-2025 Talsma ICT
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
import nl.talsmasoftware.umldoclet.features.beans.StandardJavaBean;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Test that properties can be rendered as fields with the option {@code -umlPropertiesAsFields true}.
 */
public class Issue124PropertiesAsFieldsTest {
    private static final File outputdir = new File("target/issues/124");

    @BeforeAll
    public static void generateBeansPackageJavadoc() {
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-sourcepath", "src/test/java",
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "-umlJavaBeanPropertiesAsFields",
                StandardJavaBean.class.getPackageName()
        ), is(0));
    }

    @Test
    public void testPropertiesAsFieldsForPublicClass() {
        String umlFileName = StandardJavaBean.class.getName().replace('.', '/') + ".puml";
        String uml = TestUtil.read(new File(outputdir, umlFileName));
        assertThat(uml, containsString("+stringValue: String"));
        assertThat(uml, containsString("+intValue: int"));
        assertThat(uml, containsString("+booleanValue: boolean"));
        assertThat(uml, containsString("+child: StandardJavaBean"));

        assertThat(uml, not(containsString("getStringValue(")));
        assertThat(uml, not(containsString("setStringValue(")));
        assertThat(uml, not(containsString("getIntValue(")));
        assertThat(uml, not(containsString("setIntValue(")));
        assertThat(uml, not(containsString("isBooleanValue(")));
        assertThat(uml, not(containsString("setBooleanValue(")));
        assertThat(uml, not(containsString("getChild(")));
        assertThat(uml, not(containsString("setChild(")));
    }

    @Test
    public void testPropertiesAsFieldsForPackageDiagram() {
        String umlFileName = StandardJavaBean.class.getPackageName().replace('.', '/') + "/package.puml";
        String nameInPackage = StandardJavaBean.class.getPackageName() + "::" + StandardJavaBean.class.getSimpleName();
        String uml = TestUtil.read(new File(outputdir, umlFileName));
        assertThat(uml, containsString("+stringValue: String"));
        assertThat(uml, containsString("+intValue: int"));
        assertThat(uml, containsString("+booleanValue: boolean"));
        assertThat(uml, containsString(nameInPackage + " --> " + nameInPackage + ": child"));

        assertThat(uml, not(containsString("getStringValue(")));
        assertThat(uml, not(containsString("setStringValue(")));
        assertThat(uml, not(containsString("getIntValue(")));
        assertThat(uml, not(containsString("setIntValue(")));
        assertThat(uml, not(containsString("isBooleanValue(")));
        assertThat(uml, not(containsString("setBooleanValue(")));
        assertThat(uml, not(containsString("getChild(")));
        assertThat(uml, not(containsString("setChild(")));
    }

}
