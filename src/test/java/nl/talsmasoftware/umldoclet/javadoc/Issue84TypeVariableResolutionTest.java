package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.uml.TypeMember;
import nl.talsmasoftware.umldoclet.util.Testing;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.spi.ToolProvider;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

public class Issue84TypeVariableResolutionTest {

    @Test
    public void doTest() throws FileNotFoundException {
        String typeMemberPath = TypeMember.class.getName().replace('.', '/');
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/test-84",
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                "-createPumlFiles",
                "src/main/java/" + typeMemberPath + ".java"
        );

        assertThat(Testing.readUml(new FileInputStream("target/test-84/" + typeMemberPath + ".puml")),
                stringContainsInOrder(asList(
                        "java.lang.Comparable<TypeMember>",
                        "{abstract} +compareTo(TypeMember): int")));
    }

}
