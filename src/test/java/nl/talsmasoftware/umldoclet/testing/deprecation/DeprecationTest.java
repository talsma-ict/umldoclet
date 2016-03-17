package nl.talsmasoftware.umldoclet.testing.deprecation;

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by sjoerd on 03-03-16.
 */
@SuppressWarnings("deprecation")
public class DeprecationTest {

    @Test
    public void testClassWithDeprecatedItems() {
        String classUml = Testing.readFile("testing/deprecation/ClassWithDeprecatedItems.puml");
        assertThat(classUml, is(not(nullValue())));

        // Check fields.
        assertThat(classUml, containsString("+notDeprecatedField: int"));
        assertThat(classUml, not(containsString("deprecatedFieldByAnnotation")));
        assertThat(classUml, not(containsString("deprecatedFieldByJavadoc")));
        // Check methods.
        assertThat(classUml, containsString("+notDeprecatedMethod(): int"));
        assertThat(classUml, not(containsString("deprecatedMethodByAnnotation")));
        assertThat(classUml, not(containsString("deprecatedMethodByJavadoc")));
    }

    @Test
    public void testClassDeprecatedByAnnotation() {
        final String className = DeprecatedByAnnotationClass.class.getName();
        String classUml = Testing.readFile("testing/deprecation/DeprecatedByAnnotationClass.puml");
        assertThat(classUml, is(not(nullValue())));

        // Class should have 'deprecated' stereotype.
        assertThat(classUml, containsString("class " + className + " <<deprecated>>"));
        // Field should have strike through font.
        assertThat(classUml, containsString("+ --someField-- : String"));
        // Method should have strike through font.
        assertThat(classUml, containsString("+ --someMethod-- (): String"));
    }

    @Test
    public void testClassDeprecatedByJavadocTag() {
        final String className = DeprecatedByJavadocTagAbstractClass.class.getName();
        String classUml = Testing.readFile("testing/deprecation/DeprecatedByJavadocTagAbstractClass.puml");
        assertThat(classUml, is(not(nullValue())));

        // Class should have 'deprecated' stereotype.
        assertThat(classUml, containsString("class " + className + " <<deprecated>>"));
        // Field should have strike through font.
        assertThat(classUml, containsString("# --someField-- : String"));
        // Method should have strike through font.
        assertThat(classUml, containsString("+ --someMethod-- (): void"));
    }

    @Test
    public void testDeprecationBySuperclass() {
        final String className = DeprecatedBySuperclass.class.getName();
        String classUml = Testing.readFile("testing/deprecation/DeprecatedBySuperclass.puml");
        assertThat(classUml, is(not(nullValue())));

        // Class should have 'deprecated' stereotype.
        assertThat(classUml, containsString("class " + className + " <<deprecated>>"));
        // Non-deprecated declared field should be deprecated as well due to the superclass.
        assertThat(classUml, containsString("# --someSubclassField-- : String"));
        // Overridden method should still be deprecated.
        assertThat(classUml, containsString("+ --someMethod-- (): void"));
        // Non-deprecated declared field should be deprecated as well due to the superclass.
        assertThat(classUml, containsString("+ --someOtherMethod-- (): int"));
    }

    @Test
    public void testDeprecationInPackageDiagram() {
        String packageUml = Testing.readFile("testing/deprecation/package.puml");
        assertThat(packageUml, is(not(nullValue())));

        // Deprecated classes should be omitted from the package diagram.
        assertThat(packageUml, not(containsString(DeprecatedByAnnotationClass.class.getName())));
        assertThat(packageUml, not(containsString(DeprecatedByJavadocTagAbstractClass.class.getName())));
        assertThat(packageUml, not(containsString(DeprecatedBySuperclass.class.getName())));

        // Non-deprecated classes and members should be rendered.
        assertThat(packageUml, containsString("class nl.talsmasoftware.umldoclet.testing.deprecation.ClassWithDeprecatedItems"));
        assertThat(packageUml, containsString("+notDeprecatedField: int"));
        assertThat(packageUml, containsString("+notDeprecatedMethod(): int"));

        // Deprecated members should not be rendered.
        assertThat(packageUml, not(containsString("deprecatedFieldByAnnotation")));
        assertThat(packageUml, not(containsString("deprecatedFieldByJavadoc")));
        assertThat(packageUml, not(containsString("deprecatedMethodByAnnotation")));
        assertThat(packageUml, not(containsString("deprecatedMethodByJavadoc")));
    }
}
