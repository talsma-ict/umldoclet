/*
 * Copyright 2016-2018 Talsma ICT
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

/**
 * Contains static utility methods for files.
 *
 * @author Sjoerd Talsma
 */
public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the relative path from one file to another.
     *
     * @param from The source file.
     * @param to   The target file.
     * @return The relative path from the source to the target file.
     */
    public static String relativePath(File from, File to) {
        if (from == null || to == null) return null;
        try {
            if (from.isFile()) from = from.getParentFile();
            if (!from.isDirectory()) throw new IllegalArgumentException("Not a directory: " + from);

            final String[] fromParts = from.getCanonicalPath().split(Pattern.quote(File.separator));
            List<String> toParts = new ArrayList<>(asList(to.getCanonicalPath().split(Pattern.quote(File.separator))));

            int skip = 0; // Skip the common base path
            while (skip < fromParts.length && skip < toParts.size() && fromParts[skip].equals(toParts.get(skip))) {
                skip++;
            }
            if (skip > 0) toParts = toParts.subList(skip, toParts.size());

            // Replace each remaining directory in 'from' by a preceding "../"
            for (int i = fromParts.length; i > skip; i--) toParts.add(0, "..");

            // Return the resulting path, joined by seprators.
            return toParts.stream().collect(joining("/"));
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O exception calculating relative path from \""
                    + from + "\" to \"" + to + "\": " + ioe.getMessage(), ioe);
        }
    }

    /**
     * Ensure that the parent directory exists for the specified file.
     * <p>
     * This will attempt to create the parent directory if it does not exist yet.
     *
     * @param file The file verify directory existence for.
     * @return The specified file.
     * @throws IllegalStateException in case the parent directory did not yet exist and could not be created either.
     */
    public static File ensureParentDir(File file) {
        if (file != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IllegalStateException("Can't create directory \"" + file.getParent() + "\".");
        }
        return file;
    }

    /**
     * Shortcut implementation that determines the substring after the last Windows or *nix
     * path separator.
     *
     * @param path The path to return filename of.
     * @return The part of the specified part after the last slash or backslash.
     */
    public static String fileNameOf(String path) {
        return path.substring(Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\')) + 1);
    }

    public static String withoutExtension(String path) {
        if (path != null) {
            int lastDot = path.lastIndexOf('.');
            if (lastDot > 0) path = path.substring(0, lastDot);
        }
        return path;
    }

    public static Reader openReaderTo(URI uri, String charsetName) throws IOException {
        if ("file".equals(uri.getScheme())) {
            return new InputStreamReader(new FileInputStream(new File(uri)), charsetName);
        } else try {
            return new InputStreamReader(uri.toURL().openStream(), charsetName);
        } catch (MalformedURLException murle) {
            File uriAsFile = new File(uri.toASCIIString());
            if (uriAsFile.canRead()) return new InputStreamReader(new FileInputStream(uriAsFile), charsetName);
            throw murle;
        }
    }

    public static boolean hasExtension(Object file, String extension) {
        if (file == null || extension == null) return false;
        if (!extension.startsWith(".")) extension = '.' + extension;
        return file.toString().toLowerCase().endsWith(extension.toLowerCase());
    }
}
