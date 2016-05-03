package nl.talsmasoftware.umldoclet.rendering.plantuml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by talsma.s on 03-05-2016.
 */
public class DelegatingWriter extends Writer {

    protected final Writer[] delegates;

    public DelegatingWriter(Writer... delegates) {
        this.delegates = delegates;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (Writer delegate : delegates) {
            delegate.write(cbuf, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (Writer delegate : delegates) {
            delegate.flush();
        }
    }

    @Override
    public void close() throws IOException {
        List<Exception> closeExceptions = new ArrayList<>();
        for (Writer delegate : delegates) {
            try {
                delegate.close();
            } catch (IOException | RuntimeException closeException) {
                closeExceptions.add(closeException);
            }
        }
        if (!closeExceptions.isEmpty()) {
            throw mergeExceptions(closeExceptions);
        }
    }

    private IOException mergeExceptions(Collection<Exception> exceptions) {
        if (exceptions.size() == 1) {
            Exception exception = exceptions.iterator().next();
            return exception instanceof IOException ? (IOException) exception
                    : new IOException("Error closing delegate writer!", exception);
        }
        IOException ioe = new IOException("Error closing " + exceptions.size() + " delegate writers!");
        for (Exception suppressed : exceptions) {
            ioe.addSuppressed(suppressed);
        }
        return ioe;
    }
}
