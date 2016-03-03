package nl.talsmasoftware.umldoclet.testing.deprecation;

/**
 * Deprecated by the {@code java.lang.Deprecated} annotation and not the javadoc tag.
 */
@Deprecated
public class DeprecatedByAnnotationClass {

    public String someField;

    public String someMethod() {
        return someField;
    }

}
