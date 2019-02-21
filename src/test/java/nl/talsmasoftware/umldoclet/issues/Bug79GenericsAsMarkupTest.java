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
package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Optional;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class Bug79GenericsAsMarkupTest {
    private static final String packageAsPath = Bug79GenericsAsMarkupTest.class.getPackageName().replace('.', '/');
    private static final File outputdir = new File("target/issues/79");
    private static String classUml;

    public <U> Optional<U> underlineMarkup() {
        return Optional.empty();
    }

    public <I> Optional<I> italicMarkup() {
        return Optional.empty();
    }

    public <B> Optional<B> boldMarkup() {
        return Optional.empty();
    }

    @BeforeClass
    public static void createJavadoc() {
        String classAsPath = packageAsPath + '/' + Bug79GenericsAsMarkupTest.class.getSimpleName();
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + Bug79GenericsAsMarkupTest.class.getName().replace('.', '/') + ".java"
        ), is(0));
        classUml = Testing.read(new File(outputdir, classAsPath + ".puml"));
    }

    @Test
    public void testNoMarkup() {
        assertThat(classUml, not(containsString("Optional<U>")));
        assertThat(classUml, not(containsString("Optional<I>")));
        assertThat(classUml, not(containsString("Optional<B>")));

        String stripped = classUml.replace('\u200B', '?'); // Make zero-width-space 'visible' for test
        assertThat(stripped, containsString("Optional<?U>"));
        assertThat(stripped, containsString("Optional<?I>"));
        assertThat(stripped, containsString("Optional<?B>"));
    }

}
