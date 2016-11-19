package nl.talsmasoftware.umldoclet.testing.legacy;

/**
 * Test illegal association:
 *
 * @assoc - - some.package.other.TypeDefinition
 */
public interface URLStreamHandler {
    void openConnection();

    void parseURL();

    void setURL();

    void toExternalForm();
}