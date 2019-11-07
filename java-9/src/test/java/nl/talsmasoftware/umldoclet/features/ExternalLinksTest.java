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
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.Serializable;
import java.util.spi.ToolProvider;

import static java.util.Arrays.asList;
import static nl.talsmasoftware.umldoclet.util.FileUtils.relativePath;
import static nl.talsmasoftware.umldoclet.util.TestUtil.createDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

/**
 * Tests the 'external links' feature
 *
 * <p>
 * This feature is tracked by <a href="https://github.com/talsma-ict/umldoclet/issues/96">issue 96 on github</a>
 *
 * <p>
 * For later reference, some JDK links:
 * <dl>
 * <dt>JDK 9</dt><dd>{@code <link>https://docs.oracle.com/javase/9/docs/api</link>}</dd>
 * <dt>JDK 10</dt><dd>{@code <link>https://docs.oracle.com/javase/10/docs/api</link>}</dd>
 * <dt>JDK 11</dt><dd>{@code <link>https://docs.oracle.com/en/java/javase/11/docs/api</link>}</dd>
 * </dl>
 *
 * @author Sjoerd Talsma
 */
public class ExternalLinksTest {

    static final File testoutput = TestUtil.deleteRecursive(new File("target/issues/96"));
    static final String packageAsPath = ExternalLinksTest.class.getPackageName().replace('.', '/');

    @SuppressWarnings("unused")
    public static class TestClass implements Serializable {
    }

    @Test
    public void testRelativeExternalLink() {
        File externalDir = createDirectory(new File(testoutput, "externalApidocs"));
        TestUtil.write(new File(externalDir, "package-list"), Serializable.class.getPackageName());
        TestUtil.write(new File(externalDir, "java/io/package-summary.html"), "<html></html>");
        File outputdir = createDirectory(new File(testoutput, "link-relative"));

        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "-link", relativePath(outputdir, externalDir),
                "src/test/java/" + packageAsPath + '/' + getClass().getSimpleName() + ".java"
        );

        File packageUml = new File(outputdir, packageAsPath + "/package.puml");
        String uml = TestUtil.read(packageUml);

        // Check link to Serializable javadoc
        assertThat(uml, stringContainsInOrder(asList("interface", "Serializable",
                "[[" + relativePath(packageUml, externalDir) + "/java/io/Serializable.html]]")));
    }

    @Test
    public void testOnlineExternalLink() {
        File outputdir = createDirectory(new File(testoutput, "link-online"));

        File packageUml = new File(outputdir, packageAsPath + "/package.puml");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "-link", "https://docs.oracle.com/javase/9/docs/api",
                "src/test/java/" + packageAsPath + '/' + getClass().getSimpleName() + ".java"
        );

        String uml = TestUtil.read(packageUml);
        // Check link to Serializable javadoc
        assertThat(uml, stringContainsInOrder(asList("interface", "Serializable",
                "[[https://docs.oracle.com/javase/9/docs/api/java/io/Serializable.html?is-external=true]]")));
    }

    @Test
    public void testOfflineExternalLink() {
        File externalDir = createDirectory(new File(testoutput, "externalApidocs"));
        File packageList = TestUtil.write(new File(externalDir, "package-list"), Serializable.class.getPackageName());
        File outputdir = createDirectory(new File(testoutput, "link-offline"));

        File packageUml = new File(outputdir, packageAsPath + "/package.puml");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "-linkoffline", "https://docs.oracle.com/javase/9/docs/api", packageList.getParent(),
                "src/test/java/" + packageAsPath + '/' + getClass().getSimpleName() + ".java"
        );

        String uml = TestUtil.read(packageUml);
        // Check link to Serializable javadoc
        assertThat(uml, stringContainsInOrder(asList("interface", "Serializable",
                "[[https://docs.oracle.com/javase/9/docs/api/java/io/Serializable.html?is-external=true]]")));
    }

}
