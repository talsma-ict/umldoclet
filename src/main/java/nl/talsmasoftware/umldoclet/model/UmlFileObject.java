package nl.talsmasoftware.umldoclet.model;

import javax.tools.FileObject;
import java.io.*;
import java.net.URI;

public class UmlFileObject implements FileObject {

    @Override
    public URI toUri() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return null;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return null;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return null;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return null;
    }

    @Override
    public Writer openWriter() throws IOException {
        return null;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean delete() {
        return false;
    }
}
