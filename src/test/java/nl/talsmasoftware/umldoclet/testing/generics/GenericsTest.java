package nl.talsmasoftware.umldoclet.testing.generics;

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit test for generated generics information in the UML diagrams.
 */
public class GenericsTest {

    @Test
    public void testGeneratedGenerics() {
        String constantListUml = Testing.readFile("testing/generics/ConstantList.puml");
        assertThat(constantListUml, is(not(nullValue())));
        assertThat(constantListUml, containsString(
                "class nl.talsmasoftware.umldoclet.testing.generics.ConstantList<T, X>"));
        assertThat(constantListUml, containsString("+delegate: List<T>"));
        assertThat(constantListUml, containsString("+ConstantList(T)"));
        assertThat(constantListUml, containsString("+getX(): X"));
        assertThat(constantListUml, containsString("+get(int): T"));
        assertThat(constantListUml, containsString("#delegateCollection(): Collection<T>"));
    }

    @Test
    public void testGeneratedPackageDiagramGenerics() {
        String packageUml = Testing.readFile("testing/generics/package.puml");
        // First test whether the class details are rendered, as above:
        assertThat(packageUml, is(not(nullValue())));
        assertThat(packageUml, containsString(
                "class nl.talsmasoftware.umldoclet.testing.generics.ConstantList<T, X>"));
        assertThat(packageUml, containsString("+delegate: List<T>"));
        assertThat(packageUml, containsString("+ConstantList(T)"));
        assertThat(packageUml, containsString("+getX(): X"));
        assertThat(packageUml, containsString("+get(int): T"));
        assertThat(packageUml, containsString("#delegateCollection(): Collection<T>"));

        // Also test whether the references with generics were rendered:
        assertThat(packageUml, containsString("abstract class java.util.AbstractList<E> {"));
        assertThat(packageUml, containsString("{abstract} +get(int): E"));
    }

}
