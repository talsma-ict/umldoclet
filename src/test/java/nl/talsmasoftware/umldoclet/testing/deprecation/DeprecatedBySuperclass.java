package nl.talsmasoftware.umldoclet.testing.deprecation;

/**
 * Created by sjoerd on 03-03-16.
 */
@SuppressWarnings("deprecation")
public class DeprecatedBySuperclass extends DeprecatedByJavadocTagAbstractClass {

    protected String someSubclassField;

    @Override
    public void someMethod() {

    }

    public int someOtherMethod() {
        return 0;
    }

}