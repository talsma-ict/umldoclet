package nl.talsmasoftware.umldoclet.model;

import org.junit.Test;

import static nl.talsmasoftware.umldoclet.model.Reference.Side.from;
import static nl.talsmasoftware.umldoclet.model.Reference.Side.to;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Sjoerd Talsma
 */
public class ReferenceTest {

    @Test
    public void testSelfReference() {
        Reference ref = new Reference(from(getClass().getName()), "-->", to(getClass().getName()));
        assertThat(ref.isSelfReference(), is(true));
    }

}
