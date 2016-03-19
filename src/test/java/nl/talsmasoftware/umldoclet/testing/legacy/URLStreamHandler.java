package nl.talsmasoftware.umldoclet.testing.legacy;

public interface URLStreamHandler {
    void openConnection();

    void parseURL();

    void setURL();

    void toExternalForm();
}