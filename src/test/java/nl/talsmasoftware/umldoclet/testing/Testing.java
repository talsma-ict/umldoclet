package nl.talsmasoftware.umldoclet.testing;

import java.io.*;

/**
 * Created by sjoerd on 02-03-16.
 */
public class Testing {

    /**
     * Reads a file with a relative path from the test-uml "umldoclet" path.
     *
     * @param name the relative path to the file from the "umldoclet" directory.
     * @return The content of the file (using UTF-8 encoding).
     */
    public static String readFile(String name) {
        try (Reader in = new InputStreamReader(new FileInputStream("target/test-uml/nl/talsmasoftware/umldoclet/" + name), "UTF-8")) {
            StringWriter out = new StringWriter();
            char[] buf = new char[1024];
            for (int read = in.read(buf); read >= 0; read = in.read(buf)) {
                out.write(buf, 0, read);
            }
            return out.toString();
        } catch (IOException ioe) {
            throw new IllegalStateException(String.format("Cannot read from \"%s\": %s.", name, ioe.getMessage()), ioe);
        }
    }

}
