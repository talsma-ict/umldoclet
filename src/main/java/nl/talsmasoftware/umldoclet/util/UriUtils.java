/*
 * Copyright 2016-2021 Talsma ICT
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Utility class for manipulating URI's
 *
 * @author Sjoerd Talsma
 */
public final class UriUtils {
    /**
     * For simple roll-our-own hex encoding.
     */
    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private UriUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method adds a 'component' to the path of an URI.
     *
     * @param uri       The URI to add a path component to
     * @param component The component to add to the end of the uri path, separated by a slash ({@code '/'}) character
     * @return The new URI
     */
    public static URI addPathComponent(URI uri, String component) {
        if (uri != null && component != null) try {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    Optional.ofNullable(uri.getPath()).map(path -> join(path, component, '/')).orElse(component),
                    uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException("Could not add path component \"" + component + "\" to " + uri + ": "
                    + use.getMessage(), use);
        }
        return uri;
    }

    /**
     * This method adds a query parameter to an existing URI and takes care of proper encoding etc.
     * <p>
     * Since query parameters are scheme-specific, this method only applies to URI's with the following schemes:
     * <ol>
     * <li>{@code "http"}</li>
     * <li>{@code "https"}</li>
     * </ol>
     *
     * @param uri   The URI to add an HTTP parameter to
     * @param name  The name of the parameter to add
     * @param value The value of the parameter to add
     * @return The URI to which a parameter may have been added
     */
    public static URI addHttpParam(URI uri, String name, String value) {
        if (uri != null && name != null && value != null && ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
            final String base = uri.toASCIIString();
            final int queryIdx = base.indexOf('?');
            final int fragmentIdx = base.indexOf('#', queryIdx < 0 ? 0 : queryIdx);
            StringBuilder newUri = new StringBuilder(fragmentIdx >= 0 ? base.substring(0, fragmentIdx) : base);
            newUri.append(queryIdx < 0 ? '?' : '&');
            appendEncoded(newUri, name);
            newUri.append('=');
            appendEncoded(newUri, value);
            if (fragmentIdx >= 0) newUri.append(base, fragmentIdx, base.length());
            return URI.create(newUri.toString());
        }
        return uri;
    }

    // TODO use UTF-8 before escaping, this only works for ascii (which is all we currently need)
    private static void appendEncoded(StringBuilder sb, String value) {
        for (char ch : value.toCharArray()) {
            if (isUnreserved(ch)) sb.append(ch);
            else appendEscapedByte(sb, (byte) ch);
        }
    }

    private static String join(String left, String right, char separator) {
        if (left.isEmpty()) return right;
        else if (right.isEmpty()) return left;
        String sep = "" + separator;
        return left.endsWith(sep) || right.startsWith(sep) ? left + right : left + separator + right;
    }

    /**
     * In the <a href="http://tools.ietf.org/html/rfc3986">URI specification (RFC-3986)</a>,
     * unreserved characters are defined as {@code ALPHA / DIGIT / "-" / "." / "_" / "~"}.
     *
     * @param ch The character to examine.
     * @return Whether the character is an unreserved
     */
    private static boolean isUnreserved(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '-' || ch == '.' || ch == '_' || ch == '~';
    }

    /**
     * Appends the byte as percent-encoded hex value (three characters).
     *
     * @param builder The builder to append to
     * @param value   the type to be appended as percent-encoded
     */
    private static void appendEscapedByte(StringBuilder builder, byte value) {
        builder.append('%');
        builder.append(HEX[(value >> 4) & 0x0f]);
        builder.append(HEX[value & 0x0f]);
    }

}
