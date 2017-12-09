package nl.talsmasoftware.umldoclet;

import org.junit.Test;

import java.util.spi.ToolProvider;

public class UMLDocletTest {

    ToolProvider javadoc = ToolProvider.findFirst("javadoc").get();

    @Test
    public void testDoclet() {
        this.javadoc.run(System.out, System.err,
                "-sourcepath", "src/main/java",
                "-d", "target/doclet-test",
                "-doclet", UMLDoclet.class.getName(),
                UMLDoclet.class.getPackage().getName()
        );
    }

}
