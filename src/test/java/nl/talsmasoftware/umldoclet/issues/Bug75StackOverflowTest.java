/*
 * Copyright 2016-2021 Talsma ICT
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
import java.util.Comparator;
import java.util.function.Function;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class Bug75StackOverflowTest {
    private static final String packageAsPath = Bug75StackOverflowTest.class.getPackageName().replace('.', '/');
    private static final File outputDir = new File("target/issues/75");

    public interface Comparable<T> {
        <U extends Comparable<? super U>> Comparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor);
    }

    @Test
    public void testInifiniteRecursionIsBounded() {
        String classAsPath = packageAsPath + '/' + Bug75StackOverflowTest.class.getSimpleName();
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/test/java/" + Bug75StackOverflowTest.class.getName().replace('.', '/') + ".java"
        ), is(0));
        TestUtil.read(new File(outputDir, classAsPath + ".puml"));
        String packageUml = TestUtil.read(new File(outputDir, packageAsPath + "/package.puml"));

        assertThat(packageUml, not(containsString("? extends Comparable<? super Comparable<? super Comparable")));
    }

}
