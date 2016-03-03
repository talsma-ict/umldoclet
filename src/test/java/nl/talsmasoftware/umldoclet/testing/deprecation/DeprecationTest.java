package nl.talsmasoftware.umldoclet.testing.deprecation;

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by sjoerd on 03-03-16.
 */
public class DeprecationTest {

    @Test
    public void testClassWithDeprecatedItems() {
        String classUml = Testing.readFile("testing/deprecation/ClassWithDeprecatedItems.puml");
        assertThat(classUml, is(not(nullValue())));
    }

}
