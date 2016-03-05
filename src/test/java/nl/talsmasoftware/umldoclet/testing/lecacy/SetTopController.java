package nl.talsmasoftware.umldoclet.testing.lecacy;

/**
 * @extends Controller
 * @extends EmbeddedAgent
 * @implements URLStreamHandler
 * @navassoc - - 1..* PowerManager
 * @note this is a note
 * over multiple lines <i>and *containing* markup</i>
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