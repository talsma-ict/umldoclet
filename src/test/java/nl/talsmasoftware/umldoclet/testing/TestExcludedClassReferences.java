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
package nl.talsmasoftware.umldoclet.testing;

import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * Tests whether configured 'excluded class references' (such as normally java.lang.Object)
 * are in fact correctly excluded from the generated diagrams.
 *
 * @author Sjoerd Talsma
 */
@Ignore // Cannot create content javadoc yet..
public class TestExcludedClassReferences extends Object {

    final static String packageDiag = Testing.readFile("testing/package.puml");
    final static String classDiag = Testing.readFile("testing/" + TestExcludedClassReferences.class.getSimpleName() + ".puml");

    public enum Values {
        VALUE1, VALUE2;
    }

    @Test
    public void testAbsentObjectDeclaration() {
        assertThat("Package diagram", packageDiag, not(containsString("java.lang.Object")));
        assertThat("Type diagram", classDiag, not(containsString("java.lang.Object")));
    }

    @Test
    public void testAbsentObjectMethods() {
        assertThat("Package diagram", packageDiag, not(containsString("hashCode")));
        assertThat("Package diagram", packageDiag, not(containsString("equals")));
        assertThat("Package diagram", packageDiag, not(containsString("toString")));

        assertThat("Type diagram", classDiag, not(containsString("hashCode")));
        assertThat("Type diagram", classDiag, not(containsString("equals")));
        assertThat("Type diagram", classDiag, not(containsString("toString")));
    }

    @Test
    public void testAbsentEnumDeclaration() {
        assertThat("Package diagram", packageDiag, not(containsString("java.lang.Enum")));
        assertThat("Type diagram", classDiag, not(containsString("java.lang.Enum")));
    }

    @Test
    public void testAbsentImplicitStaticEnumMethods() {
        assertThat("Package diagram", packageDiag, not(containsString("values()")));
        assertThat("Package diagram", packageDiag, not(containsString("valueOf(String)")));
        assertThat("Type diagram", classDiag, not(containsString("values()")));
        assertThat("Type diagram", classDiag, not(containsString("valueOf(String)")));
    }

}
