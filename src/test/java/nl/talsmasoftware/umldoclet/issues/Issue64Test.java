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
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.spi.ToolProvider;

import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * Test that any generic {@code EmptySet<T>} doesn't get rendered in UML as
 * {@code EmptySet<T extends Object>}.
 */
public class Issue64Test {
    ToolProvider javadoc = ToolProvider.findFirst("javadoc").get();

    public static class EmptySet<T> extends AbstractSet<T> {
        @Override
        @SuppressWarnings("unchecked")
        public Iterator<T> iterator() {
            return (Iterator<T>) emptySet().iterator();
        }

        @Override
        public int size() {
            return 0;
        }
    }

    @Test
    public void testIssue64_TextendsObject() {
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-sourcepath", "src/test/java",
                "-d", "target/test-64",
                "-doclet", UMLDoclet.class.getName(),
                getClass().getPackageName()
        );
        String emptySetUml = readEmptySetUml();
        assertThat(emptySetUml, not(containsString("EmptySet<T extends Object>")));
        assertThat(emptySetUml, containsString("EmptySet<T>"));
    }

    private static String readEmptySetUml() {
        try {
            return Testing.readUml(new FileInputStream("target/test-64/nl/talsmasoftware/umldoclet/issues/Issue64Test.EmptySet.puml"));
        } catch (IOException ioe) {
            throw new IllegalStateException("Couldn't open class UML.", ioe);
        }
    }
}
