package nl.talsmasoftware.umldoclet.testing.lecacy;

/**
 * @extends Controller
 * @extends EmbeddedAgent
 * @navassoc - - 1..* PowerManager
 * @note this is a note
 */
public abstract class SetTopController implements URLStreamHandler {
    public String name;

    int authorizationLevel;

    void startUp() {
    }

    void shutDown() {
    }

    void connect() {
    }
}