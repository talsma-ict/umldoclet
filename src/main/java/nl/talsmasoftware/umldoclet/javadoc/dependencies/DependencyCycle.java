package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import java.util.Collection;
import java.util.List;

/**
 * A cycle of dependencies.
 *
 * <p>
 * Package Dependencies can form a cycle if the chain of package dependencies somehow 'return' to the
 * initial package. For example if you have three packages {@code a}, {@code b} and {@code c} and the following
 * dependencies: {@code a --> b}, {@code b --> c}, they will form a cycle if you somehow create a dependency back to
 * {@code a}, e.g. {@code b -> a} or {@code c --> a}.
 */
public class DependencyCycle {

    public static Collection<DependencyCycle> detect(List<Dependency> dependencies) {
        return null;
    }

}
