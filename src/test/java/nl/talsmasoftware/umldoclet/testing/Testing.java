/*
 * Copyright 2016-2019 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.umldoclet.testing;

import java.io.*;

/**
 * Created by sjoerd on 02-03-16.
 */
public class Testing {

    /**
     * Determine the newline for this OS.
     */
    public static final String NEWLINE;

    static {
        Writer writer = new StringWriter();
        new PrintWriter(writer).println();
        NEWLINE = writer.toString();
    }


    /**
     * Reads a file with a relative path from the test-uml "umldoclet" path.
     *
     * @param name the relative path to the file from the "umldoclet" directory.
     * @return The content of the file (using UTF-8 encoding).
     */
    public static String readFile(String name) {
        try (InputStream in = new FileInputStream("target/test-uml/nl/talsmasoftware/umldoclet/" + name)) {
            return readUml(in);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Cannot read from \"%s\": %s.", name, e.getMessage()), e);
        }
    }

    public static String readClassUml(Class<?> type) {
        try (InputStream in = new FileInputStream("target/apidocs/" + type.getName().replace('.', '/') + ".puml")) {
            return readUml(in);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read .puml file of " + type, e);
        }
    }

    public static String readUml(InputStream inputStream) {
        try (Reader in = new InputStreamReader(inputStream, "UTF-8")) {
            StringWriter out = new StringWriter();
            char[] buf = new char[1024];
            for (int read = in.read(buf); read >= 0; read = in.read(buf)) {
                out.write(buf, 0, read);
            }
            return out.toString();
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot read from stream: " + ioe.getMessage(), ioe);
        }
    }

}
