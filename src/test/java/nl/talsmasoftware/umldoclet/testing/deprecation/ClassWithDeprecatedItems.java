package nl.talsmasoftware.umldoclet.testing.deprecation;

/**
 * This class itself is not deprecated, but contains several items that are (in various ways).
 */
public class ClassWithDeprecatedItems {

    public int notDeprecatedField;

    @Deprecated
    public String deprecatedFieldByAnnotation;

    /**
     * @deprecated Javadoc deprecation
     */
    public Object deprecatedFieldByJavadoc;

    public int notDeprecatedMethod() {
        return notDeprecatedField;
    }

    @Deprecated
    public String deprecatedMethodByAnnotation() {
        return deprecatedFieldByAnnotation;
    }

    /**
     * @deprecated Again, method deprecation by using javadoc.
     */
    public Object deprecatedMethodByJavadoc() {
        return deprecatedFieldByJavadoc;
    }


}
