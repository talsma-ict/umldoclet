package nl.talsmasoftware.umldoclet.testing.generics;

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by sjoerd on 02-03-16.
 */
public class GenericsTest {

    @Test
    public void testGeneratedGenerics() {
        String constantListUml = Testing.readFile("testing/generics/ConstantList.puml");
        assertThat(constantListUml, is(not(nullValue())));
        assertThat(constantListUml,
                containsString("class nl.talsmasoftware.umldoclet.testing.generics.ConstantList<T, X>"));
    }

}
