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
package nl.talsmasoftware.umldoclet.testing.generics;

import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit test for generated generics information in the UML diagrams.
 */
@Ignore // Cannot create content javadoc yet..
public class GenericsTest {

    //    private static final boolean QUALIFIED = new UMLDocletConfig(new String[0][], null).alwaysUseQualifiedClassnames();
    private static final boolean QUALIFIED = false;

    @Test
    public void testGeneratedGenerics() {
        String constantListUml = Testing.readFile("testing/generics/ConstantList.puml");
        assertThat(constantListUml, is(not(nullValue())));
        assertThat(constantListUml, containsString("class ConstantList<T, X>"));
        assertThat(constantListUml, containsString("+delegate: List<T>"));
        assertThat(constantListUml, containsString("+ConstantList(T[])"));
        assertThat(constantListUml, containsString("+getX(): X"));
        assertThat(constantListUml, containsString("+get(int): T"));
        assertThat(constantListUml, containsString("#delegateCollection(): Collection<T>"));
    }

    @Test
    public void testGeneratedPackageDiagramGenerics() {
        String packageUml = Testing.readFile("testing/generics/package.puml");
        // First test whether the class details are rendered, as above:
        assertThat(packageUml, is(not(nullValue())));
        String classdef = QUALIFIED
                ? "class nl.talsmasoftware.umldoclet.testing.generics.ConstantList<T, X>"
                : "class ConstantList<T, X>";
        assertThat(packageUml, containsString(classdef));
        assertThat(packageUml, containsString("+delegate: List<T>"));
        assertThat(packageUml, containsString("+ConstantList(T[])"));
        assertThat(packageUml, containsString("+getX(): X"));
        assertThat(packageUml, containsString("+get(int): T"));
        assertThat(packageUml, containsString("#delegateCollection(): Collection<T>"));

        // Also test whether the references with generics were rendered:
        assertThat(packageUml, containsString("abstract class java.util.AbstractList<E> {"));
        assertThat(packageUml, containsString("{abstract} +get(int): E"));
    }

}
