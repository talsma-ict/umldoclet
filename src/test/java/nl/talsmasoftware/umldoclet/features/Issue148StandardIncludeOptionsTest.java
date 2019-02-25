package nl.talsmasoftware.umldoclet.features;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import org.junit.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test the include options by the Standard doclet.
 *
 * <p>
 * Verify that the visibility is interpreted the same by the UML doclet.
 *
 * <p>
 * The options to be supported:
 * <ul>
 * <li>{@code -package}
 * <li>{@code -private}
 * <li>{@code -protected}
 * <li>{@code -public}
 * <li>{@code --show-module-contents}
 * <li>{@code --show-packages}
 * <li>{@code --show-types}
 * <li>{@code --show-types}
 * <li>{@code --show-members}
 * </ul>
 *
 * @author Sjoerd Talsma
 */
public class Issue148StandardIncludeOptionsTest {
    private static final File outputdir = new File("target/issues/148");

    private static File createJavadoc(String option) {
        File dir = new File(outputdir, option);
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", dir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                option,
                "-sourcepath", "src/test/java", Issue148StandardIncludeOptionsTest.class.getPackageName()
        ), is(0));
        return dir;
    }

    @Test
    public void testOptionPrivate() {
        File dir = createJavadoc("-private");

    }

}
