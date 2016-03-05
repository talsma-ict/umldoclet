package nl.talsmasoftware.umldoclet.testing.legacy;

public interface URLStreamHandler {
    void OpenConnection();

    void parseURL();

    void setURL();

    void toExternalForm();
}