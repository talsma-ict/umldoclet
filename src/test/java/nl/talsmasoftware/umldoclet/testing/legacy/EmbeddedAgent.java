package nl.talsmasoftware.umldoclet.testing.legacy;

import java.util.EventObject;

/**
 * Created by sjoerd on 07-03-16.
 *
 * @implements java.lang.AutoCloseable    (defined somewhere in classpath)
 * @implements com.unavailable.api.Agent  (unavailable on classpath)
 */
public abstract class EmbeddedAgent {

    protected void process(EventObject event) {
        // no-op
    }

}
