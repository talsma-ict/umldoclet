/*
 * Copyright 2016-2026 Talsma ICT
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
package nl.talsmasoftware.umldoclet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

/// @author Sjoerd Talsma
public final class TestUtil {

        /// Determine the newline for this OS.
    public static final String NEWLINE;

    static {
        Writer writer = new StringWriter();
        new PrintWriter(writer).println();
        NEWLINE = writer.toString();
    }

    public static void assertUnsupportedConstructor(Class<?> utilityClass) {
        assertThat("Class is final", utilityClass.getModifiers() & FINAL, is(FINAL));
        assertThat("Constructors", asList(utilityClass.getDeclaredConstructors()), hasSize(1));
        Constructor<?> constructor = utilityClass.getDeclaredConstructors()[0];
        assertThat("Constructor parameters", asList(constructor.getParameterTypes()), is(empty()));
        assertThat("Constructor is private", constructor.getModifiers() & PRIVATE, is(PRIVATE));
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("Exception expected");
        } catch (InvocationTargetException expected) {
            assertThat("Expected cause", expected.getCause(), is(instanceOf(UnsupportedOperationException.class)));
        } catch (ReflectiveOperationException roe) {
            throw new AssertionError(roe.getMessage(), roe);
        }
    }

        /// Reads a file with a relative path from the test-content "umldoclet" path.
    ///
    /// @param name the relative path to the file from the "umldoclet" directory.
    /// @return The content of the file (using UTF-8 encoding).
    /// @deprecated Test different directories too
    @Deprecated
    public static String readFile(String name) {
        return read(new File("target/test-content/nl/talsmasoftware/umldoclet", name));
    }

    public static String read(File file) {
        try (InputStream in = new FileInputStream(file)) {
            return readUml(in);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Cannot read from \"%s\": %s.", file, e.getMessage()), e);
        }
    }

    public static File write(File file, String content) {
        createDirectory(file.getParentFile());
        try (Writer writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write content to " + file + ": " + e.getMessage(), e);
        }
        return file;
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

    public static File deleteRecursive(File file) {
        if (file.isDirectory()) for (File child : file.listFiles()) deleteRecursive(child);
        if (file.exists() && !file.delete()) throw new IllegalStateException("Couldn't delete " + file);
        return file;
    }

    public static File createDirectory(File dir) {
        dir.mkdirs();
        if (!dir.isDirectory()) throw new IllegalStateException("Not a directory: " + dir);
        return dir;
    }

        /// Creates a new, empty file (if it doesn't already exist).
    ///
    /// @param file The file to create.
    public static void touch(File file) {
        write(file, "");
    }

    public static String randomString() {
        return "" + UUID.randomUUID();
    }
}
