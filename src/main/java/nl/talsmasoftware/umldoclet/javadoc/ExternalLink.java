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
package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.util.FileUtils.openReaderTo;
import static nl.talsmasoftware.umldoclet.util.UriUtils.addHttpParam;
import static nl.talsmasoftware.umldoclet.util.UriUtils.addPathComponent;

/**
 * Processes {@code -link} and {@code -linkoffline} javadoc options
 * and contains functionality to read a set of externally documented packages.
 * <p>
 * Since the {@code -link} option only has a single URI parameter,
 * this uri must be used as both {@code docUri} and {@code packageListUri}.
 *
 * @author Sjoerd Talsma
 */
final class ExternalLink {
    private final Configuration config;
    private final URI docUri, baseUri;
    private Map<String, Set<String>> modules;

    ExternalLink(Configuration config, String apidoc, String packageList) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.docUri = createUri(requireNonNull(apidoc, "External apidoc URI is <null>."));
        requireNonNull(packageList, "Location URI for \"package-list\" is <null>.");
        this.baseUri = createUri(packageList);
    }

    Optional<URI> resolveType(String packagename, String typeName) {
        return modules().entrySet().stream()
                .filter(entry -> entry.getValue().contains(packagename))
                .findFirst()
                .map(entry -> entry.getKey().isEmpty() ? docUri : addPathComponent(docUri, entry.getKey()))
                .map(uri -> addPathComponent(uri, packagename.replace('.', '/')))
                .map(uri -> addPathComponent(uri, typeName + ".html"))
                .map(this::makeAbsolute)
                .map(uri -> addHttpParam(uri, "is-external", "true"));
    }

    private Map<String, Set<String>> modules() {
        if (modules == null) {
            synchronized (this) {
                Map<String, Set<String>> moduleMap = tryReadModules();
                Set<String> packages = moduleMap == null ? tryReadPackages() : null;
                modules = moduleMap != null ? moduleMap
                        : packages != null ? singletonMap("", packages) // the 'unnamed' module
                        : emptyMap();
            }
        }
        return modules;
    }

    private Map<String, Set<String>> tryReadModules() {
        final URI elementListUri = addPathComponent(baseUri, "element-list");
        final Map<String, Set<String>> modules = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(
                openReaderTo(config.destinationDirectory(), elementListUri, "UTF-8"))) {
            String module = ""; // default to unnamed module
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (line.startsWith("module:")) {
                    module = line.substring("module:".length()).trim();
                } else if (!line.isEmpty()) {
                    if (!modules.containsKey(module)) modules.put(module, new LinkedHashSet<>());
                    modules.get(module).add(line);
                }
            }
        } catch (IOException | RuntimeException ex) {
            // TODO!
        }
        return modules.isEmpty() ? null : unmodifiableMap(modules);
    }

    private Set<String> tryReadPackages() {
        final URI packageListUri = addPathComponent(baseUri, "package-list");
        try {
            Set<String> pkglist = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(
                    openReaderTo(config.destinationDirectory(), packageListUri, "UTF-8"))) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    line = line.trim();
                    if (!line.isEmpty()) pkglist.add(line);
                }
            }
            return unmodifiableSet(pkglist);
        } catch (IOException | RuntimeException ex) {
            config.logger().warn(Message.WARNING_CANNOT_READ_PACKAGE_LIST, packageListUri, ex);
        }
        return null;
    }

    private URI makeAbsolute(URI uri) {
        if (uri != null && !uri.isAbsolute()) {
            uri = new File(config.destinationDirectory(), uri.toASCIIString()).toURI();
        }
        return uri;
    }

    private static URI createUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException use) {
            if (new File(uri).exists()) return new File(uri).toURI();
            throw new IllegalArgumentException(use.getMessage(), use);
        }
    }

}
