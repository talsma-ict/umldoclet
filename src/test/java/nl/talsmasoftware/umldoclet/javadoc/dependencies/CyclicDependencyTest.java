package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import org.junit.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

public class CyclicDependencyTest {

    @Test
    public void testSimpleCycleDetection() {
        Dependency ab = new Dependency("a", "b");
        Dependency ba = new Dependency("b", "a");

        Collection<DependencyCycle> cycles = DependencyCycle.detect(asList(ab, ba));
        assertThat(cycles, not(empty()));
    }

}
