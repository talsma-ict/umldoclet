package nl.talsmasoftware.umldoclet.testing.lecacy;

public interface URLStreamHandler {
    void OpenConnection();

    void parseURL();

    void setURL();

    void toExternalForm();
}