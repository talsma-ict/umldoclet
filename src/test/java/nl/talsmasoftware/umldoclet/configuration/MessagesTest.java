package nl.talsmasoftware.umldoclet.configuration;

import org.junit.Test;

import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;

public class MessagesTest {

    @Test
    public void testAllMessageAvailability() {
        stream(Messages.values()).forEach(key -> assertThat(key, hasToString(notNullValue())));
    }

}
