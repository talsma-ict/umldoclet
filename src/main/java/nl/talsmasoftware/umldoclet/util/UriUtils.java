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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class for manipulating URI's
 *
 * @author Sjoerd Talsma
 */
public final class UriUtils {

    private UriUtils() {
        throw new UnsupportedOperationException();
    }

    public static URI addParam(URI uri, String name, String value) {
        if (uri == null || name == null || value == null) return uri;
        try {
            String scheme = uri.getScheme();
            String query = uri.getQuery();
            if (scheme != null && !"file".equals(scheme)) {
                if (query == null || query.isEmpty()) query = name + "=" + value;
                else query = query + "&" + name + "=" + value;
            }
            return new URI(scheme, uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), query, uri.getFragment());
        } catch (URISyntaxException use) {
            throw new IllegalStateException("Could not add path query parameter \"" + name + "=" + value + "\" to "
                    + uri + ": " + use.getMessage(), use);
        }
    }

}
