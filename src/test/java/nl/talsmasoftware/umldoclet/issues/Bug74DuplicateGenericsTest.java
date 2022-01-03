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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.function.Supplier;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * @author Sjoerd Talsma
 */
public class Bug74DuplicateGenericsTest {
    private static final String packageAsPath = Bug74DuplicateGenericsTest.class.getPackageName().replace('.', '/');
    private static final File outputDir = new File("target/issues/74");
    private static String classUml;
    private static String packageUml;

    public interface MySupplier<T> extends Supplier<T> {
    }

    @BeforeAll
    public static void createJavadoc() {
        String classAsPath = packageAsPath + '/' + Bug74DuplicateGenericsTest.class.getSimpleName();
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + Bug74DuplicateGenericsTest.class.getName().replace('.', '/') + ".java"
        ), is(0));
        classUml = TestUtil.read(new File(outputDir, classAsPath + ".MySupplier.puml"));
        packageUml = TestUtil.read(new File(outputDir, packageAsPath + "/package.puml"));
    }

    @Test
    public void testGenericsNotDuplicated() {
        assertThat(classUml, containsString("as java.util.function.Supplier<T>"));
        assertThat(classUml, containsString("<size:14>Supplier\\n"));
    }

}
