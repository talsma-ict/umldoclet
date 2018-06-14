/*
 * Copyright 2016-2018 Talsma ICT
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
import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

/**
 * @author Sjoerd Talsma
 */
@Ignore // Links are not built yet
public class Issue69LinksTest {
    static final File testoutput = new File("target/test-69");
    static final String packageAsPath = Issue69LinksTest.class.getPackageName().replace('.', '/');

    @Test
    public void testLink_sameDirectory() {
        File outputdir = new File(testoutput, "same-dir");
        File packageUml = new File(outputdir, packageAsPath + "/package.puml");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "src/test/java/" + packageAsPath + '/' + getClass().getSimpleName() + ".java"
        );

        String uml = Testing.read(packageUml);
        assertThat(uml, stringContainsInOrder(asList("Issue69LinksTest", "[[Issue69LinksTest.html]]")));
    }

    @Test
    public void testLink_otherDirectory() {
        File outputdir = new File(testoutput, "other-dir");
        File packageUml = new File(outputdir, packageAsPath + "/package.puml");

        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-umlImageDirectory", "images",
                "-doclet", UMLDoclet.class.getName(),
                "src/test/java/" + packageAsPath + '/' + getClass().getSimpleName() + ".java"
        );

        String uml = Testing.read(packageUml);
        assertThat(uml, stringContainsInOrder(asList("Issue69LinksTest", "[[../nl/talsmasoftware/umldoclet/issues/Issue69LinksTest.html]]")));
    }

}
