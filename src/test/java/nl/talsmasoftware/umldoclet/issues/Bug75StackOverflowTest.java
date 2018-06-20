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
import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Comparator;
import java.util.function.Function;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * @author Sjoerd Talsma
 */
public class Bug75StackOverflowTest {
    private static final String packageAsPath = Bug75StackOverflowTest.class.getPackageName().replace('.', '/');
    private static final File outputdir = new File("target/test-75");
    private static String classUml, packageUml;

//    public interface Comparable<T> {
//        <U extends Comparable<? super U>> Comparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor);
//    }

    @BeforeClass
    public static void createJavadoc() {
        String classAsPath = packageAsPath + '/' + Bug75StackOverflowTest.class.getSimpleName();
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageDirectory", "images",
                "src/test/java/" + Bug75StackOverflowTest.class.getName().replace('.', '/') + ".java"
        ) ,is(0));
        classUml = Testing.read(new File(outputdir, classAsPath + ".puml"));
        packageUml = Testing.read(new File(outputdir, packageAsPath + "/package.puml"));
    }

    @Test
    public void testDefaultConstructorShouldBeHidden() {
        assertThat(packageUml, not(containsString("? extends Comparable<? super Comparable<? super Comparable")));
    }

}
