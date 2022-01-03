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
package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

/**
 * @author Sjoerd Talsma
 */
public class Issue69LinksTest {
    static final File testoutput = new File("target/issues/69");
    static final String packageAsPath = Issue69LinksTest.class.getPackageName().replace('.', '/');

    public static class InnerClass {
    }

    @Test
    public void testLinkSameDirectory() {
        File outputdir = new File(testoutput, "same-dir");
        File packageUml = new File(outputdir, packageAsPath + "/package.puml");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + packageAsPath + '/' + getClass().getSimpleName() + ".java"
        );

        String uml = TestUtil.read(packageUml);
        // Check link to test class
        assertThat(uml, stringContainsInOrder(asList("Issue69LinksTest", "[[Issue69LinksTest.html]]")));
        // Check link to inner class
        assertThat(uml, stringContainsInOrder(asList("InnerClass", "[[Issue69LinksTest.InnerClass.html]]")));
    }

}
