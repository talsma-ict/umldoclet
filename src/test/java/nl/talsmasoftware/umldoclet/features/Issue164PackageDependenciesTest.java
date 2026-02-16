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
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.html.HtmlPostprocessor;
import nl.talsmasoftware.umldoclet.javadoc.DocletConfig;
import nl.talsmasoftware.umldoclet.javadoc.dependencies.DependenciesElementScanner;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;
import nl.talsmasoftware.umldoclet.rendering.writers.DelegatingWriter;
import nl.talsmasoftware.umldoclet.uml.UMLNode;
import nl.talsmasoftware.umldoclet.util.FileUtils;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.spi.ToolProvider;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class Issue164PackageDependenciesTest {
    private static List<String> packageNames = asList(
            UMLDoclet.class.getPackageName(),
            Configuration.class.getPackageName(),
            HtmlPostprocessor.class.getPackageName(),
            DocletConfig.class.getPackageName(),
            DependenciesElementScanner.class.getPackageName(),
            Logger.class.getPackageName(),
            Indentation.class.getPackageName(),
            DelegatingWriter.class.getPackageName(),
            UMLNode.class.getPackageName(),
            FileUtils.class.getPackageName()
    );
    private static final File outputdir = new File("target/issues/164");

    @BeforeAll
    public static void createJavaDoc() {
        List<String> args = new ArrayList<>(asList(
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "-sourcepath", "src/main/java"));
        args.addAll(packageNames);
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err, args.toArray(new String[0])), is(0));
    }

    @Test
    public void testPackageDependencies() {
        String packageDependencies = TestUtil.read(new File(outputdir, "package-dependencies.puml"));

        assertThat("Doclet superclass dependency", packageDependencies,
                containsString("nl.talsmasoftware.umldoclet --> jdk.javadoc.doclet"));
        assertThat("UML contains package-summary links", packageDependencies,
                containsString("\"nl.talsmasoftware.umldoclet\" [[nl/talsmasoftware/umldoclet/package-summary.html]]"));
    }

}
