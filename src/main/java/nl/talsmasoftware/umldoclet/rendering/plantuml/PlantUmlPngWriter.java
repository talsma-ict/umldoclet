package nl.talsmasoftware.umldoclet.rendering.plantuml;

import java.io.*;

/**
 * Created by talsma.s on 03-05-2016.
 */
public class PlantumlPngWriter extends DelegatingWriter {

    private final File directory;
    private final String baseName;

    public PlantumlPngWriter(Writer delegate, File directory, String baseName) {
        super(new StringWriter(), delegate);
        this.directory = directory;
        this.baseName = baseName;
    }

    protected StringBuffer getBuffer() {
        return ((StringWriter) super.delegates[0]).getBuffer();
    }

    @Override
    public void close() throws IOException {
        super.close();
        try (OutputStream pngOutput = new BufferedOutputStream(new FileOutputStream(new File(directory, baseName + ".png")))) {
            PlantumlSupport.writePngImage(getBuffer().toString(), pngOutput);
        }
    }

}
