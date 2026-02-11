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
package nl.talsmasoftware.umldoclet.javadoc;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Doclet.Option.Kind;
import net.sourceforge.plantuml.cli.GlobalConfig;
import net.sourceforge.plantuml.cli.GlobalConfigKey;
import nl.talsmasoftware.umldoclet.UMLDoclet;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/// Type that serves as an 'anti-corruption' facade between our Doclet
/// and the `internal` configuration of the StandardDoclet.
///
/// Although we want to apply the standard configuration options that make
/// sense to the UML doclet, we don't want to re-implement all standard options.
///
/// However, we also don't want to have the UML Doclet blow up in our
/// face at runtime when the standard configuration implementation changes.
///
/// @author Sjoerd Talsma
/// Supported options for the UML doclet.
final class UMLOptions {
    private final DocletConfig config;
    private final Set<Doclet.Option> standardOptions;
    private final Set<Doclet.Option> options;

    /// Creates new UML options.
    ///
    /// @param config The doclet configuration to use.
    UMLOptions(DocletConfig config) {
        this(config, null);
    }

    private UMLOptions(DocletConfig config, Set<? extends Doclet.Option> standardOptions) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.standardOptions = standardOptions == null ? null : new LinkedHashSet<>(standardOptions);
        this.options = new TreeSet<>(comparing(o -> o.getNames().get(0), String::compareTo));

        // Options from Standard doclet that we also support
        this.options.add(new Option("-quiet", 0, Kind.OTHER, args -> config.quiet = true));
        this.options.add(new Option("-verbose", 0, Kind.OTHER, args -> config.verbose = true));
        this.options.add(new Option("-docencoding", 1, Kind.OTHER, args -> config.docencoding = args.get(0)));
        this.options.add(new Option("-encoding", 1, Kind.OTHER, args -> config.encoding = args.get(0)));
        this.options.add(new Option("-link", 1, Kind.OTHER,
                args -> config.externalLinks.add(new ExternalLink(config, args.get(0), args.get(0)))));
        this.options.add(new Option("-linkoffline", 2, Kind.OTHER,
                args -> config.externalLinks.add(new ExternalLink(config, args.get(0), args.get(1)))));
        this.options.add(new Option("-private", 0, Kind.OTHER, args -> config.showMembers("private")));
        this.options.add(new Option("-package", 0, Kind.OTHER, args -> config.showMembers("package")));
        this.options.add(new Option("-protected", 0, Kind.OTHER, args -> config.showMembers("protected")));
        this.options.add(new Option("-public", 0, Kind.OTHER, args -> config.showMembers("public")));
        this.options.add(new Option("--show-members", 1, Kind.OTHER, args -> config.showMembers(args.get(0))));
        this.options.add(new Option("-d", 1, Kind.OTHER, args -> config.destDirName = args.get(0)));

        // Our own options
        this.options.add(new Option("--plantuml-server-url -plantumlServerUrl", 1, Kind.STANDARD,
                args -> config.plantumlServerUrl = args.get(0)));
        this.options.add(new Option("--delegate-doclet -delegateDoclet", 1, Kind.STANDARD,
                args -> config.delegateDoclet = args.get(0)));
        this.options.add(new Option("--create-puml-files -createPumlFiles", 0, Kind.STANDARD, args -> config.renderPumlFile = true));
        this.options.add(new Option("--uml-image-directory -umlImageDirectory", 1, Kind.STANDARD, args -> config.images.directory = args.get(0)));
        this.options.add(new Option("--uml-image-format -umlImageFormat", 1, Kind.STANDARD, args -> config.images.addImageFormat(args.get(0))));
        this.options.add(new Option("--uml-encoding -umlEncoding", 1, Kind.STANDARD, args -> config.umlencoding = args.get(0)));
        this.options.add(new Option("--uml-excluded-type-references -umlExcludedTypeReferences", 1, Kind.STANDARD,
                args -> config.excludedReferences = splitToList(args.get(0))));
        this.options.add(new Option("--uml-excluded-package-dependencies -umlExcludedPackageDependencies", 1, Kind.STANDARD,
                args -> config.excludedPackageDependencies = splitToList(args.get(0))));
        this.options.add(new Option("--uml-custom-directive -umlCustomDirective", 1, Kind.STANDARD,
                args -> config.customPlantumlDirectives.add(args.get(0))));
        this.options.add(new Option("--fail-on-cyclic-package-dependencies -failOnCyclicPackageDependencies", 1, Kind.STANDARD,
                args -> config.failOnCyclicPackageDependencies = asBoolean(args.get(0))));
        this.options.add(new Option("--uml-java-bean-properties-as-fields -umlJavaBeanPropertiesAsFields", 0, Kind.STANDARD,
                args -> config.methodConfig.javaBeanPropertiesAsFields = true));
        this.options.add(new Option("--uml-timeout -umlTimeout", 1, Kind.STANDARD, this::setTimeout));
        this.options.add(new Option("--uml-exclude-package-diagram -umlExcludePackageDiagram", 1, Kind.STANDARD,
           args -> config.excludePackageDiagram = asBoolean(args.get(0))));
        this.options.add(new Option("--uml-exclude-package-dependencies -umlExcludePackageDependencies", 1, Kind.STANDARD,
           args -> config.excludePackageDependencies = asBoolean(args.get(0))));
        this.options.add(new Option("--uml-exclude-class-diagrams -umlExcludeClassDiagrams", 1, Kind.STANDARD,
           args -> config.excludeClassDiagrams = asBoolean(args.get(0))));
    }

    /// Merges the UML options with the given standard options.
    ///
    /// @param standardOptions The standard options to merge with.
    /// @return The merged set of options.
    Set<Doclet.Option> mergeWith(final Set<? extends Doclet.Option> standardOptions) {
        if (standardOptions == null || standardOptions.isEmpty()) return this.options;
        Set<Doclet.Option> copy = new UMLOptions(config, standardOptions).options;
        copy.addAll(standardOptions);
        return copy;
    }

    /// Split a value on comma and semicolon (`','` and `';'`) and trim each value,
    /// then collect each non-empty value into a list.
    ///
    /// @param value The value to split into a list.
    /// @return The split value as a list.
    private static List<String> splitToList(String value) {
        return value == null || value.isEmpty() ? emptyList()
                : Stream.of(value.split("[,;]")).map(String::trim).filter(s -> !s.isEmpty()).collect(toList());
    }

    private static boolean asBoolean(String value) {
        return "true".equalsIgnoreCase(value);
    }

    private void setTimeout(List<String> timeout) {
        try {
            int timeoutSeconds = Integer.parseInt(timeout.get(0));
            GlobalConfig.getInstance().put(GlobalConfigKey.TIMEOUT_MS, timeoutSeconds * 1000L);
        } catch (RuntimeException rte) {
            throw new IllegalArgumentException("Unrecognized timeout value: seconds expected, received: " + timeout, rte);
        }
    }

    private class Option implements Doclet.Option {
        private static final String MISSING_KEY = "<MISSING KEY>";
        private final Consumer<List<String>> processor;
        private final String[] names;
        private final String parameters;
        private final String description;
        private final int argCount;
        private final Kind kind;

        protected Option(String name, int argCount, Kind kind, Consumer<List<String>> processor) {
            this.processor = processor;
            this.names = name.trim().split("\\s+");
            this.description = resourceMsg(names[0] + ".description");
            this.parameters = resourceMsg(names[0] + ".parameters");
            this.argCount = argCount;
            this.kind = kind;
        }

        private String resourceMsg(String key) {
            try {
                String resourceKey = "doclet.usage." + key.toLowerCase(ENGLISH).replaceFirst("^-+", "");
                return ResourceBundle.getBundle(UMLDoclet.class.getName()).getString(resourceKey);
            } catch (MissingResourceException mre) {
                return MISSING_KEY;
            }
        }

        @Override
        public String getDescription() {
            return findDelegate().map(Doclet.Option::getDescription)
                    .filter(s -> !s.isEmpty() && !MISSING_KEY.equals(s))
                    .orElse(description);
        }

        @Override
        public Kind getKind() {
            return kind;
        }

        @Override
        public List<String> getNames() {
            return Arrays.asList(names);
        }

        @Override
        public String getParameters() {
            return parameters;
        }

        @Override
        public int getArgumentCount() {
            return argCount;
        }

        private boolean matches(Doclet.Option other) {
            List<String> otherNames = Optional.ofNullable(other).map(Doclet.Option::getNames).orElse(emptyList());
            return !otherNames.isEmpty() && names[0].equalsIgnoreCase(otherNames.get(0));
        }

        private Optional<Doclet.Option> findDelegate() {
            return Optional.ofNullable(standardOptions).flatMap(set -> set.stream().filter(this::matches).findFirst());
        }

        @Override
        public boolean process(String option, List<String> arguments) {
            processor.accept(arguments);
            return findDelegate().map(delegate -> delegate.process(option, arguments)).orElse(true);
        }

        @Override
        public String toString() {
            return Arrays.toString(names);
        }
    }
}
