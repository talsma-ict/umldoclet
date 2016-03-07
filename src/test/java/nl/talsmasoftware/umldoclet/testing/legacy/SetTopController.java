package nl.talsmasoftware.umldoclet.testing.legacy;

/**
 * @extends Controller      (undefined)
 * @extends EmbeddedAgent   (available in documented classes)
 * @implements URLStreamHandler
 * @navassoc - - 1..* PowerManager
 * @note this is a note
 * over multiple lines <i>and <b>containing</b> markup</i>
 */
public abstract class SetTopController implements URLStreamHandler {

    public String name;
    protected int authorizationLevel;

    public void startUp() {
    }

    public void shutDown() {
    }

    public void connect() {
    }
}
