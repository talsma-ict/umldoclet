/*
 * Copyright 2016-2017 Talsma ICT
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
package nl.talsmasoftware.umldoclet.configuration;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Doclet.Option.Kind;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * Type that serves as an 'anti-corruption' facade between our Doclet
 * and the {@code internal} configuration of the StandardDoclet.
 * <p>
 * Although we want to apply the standard configuration options that make
 * sense to the UML doclet, we don't want to re-implement all standard options.
 * <p>
 * However, we also don't want to have the UML Doclet blow up in our
 * face at runtime when the standard configuration implementation changes.
 *
 * @author Sjoerd Talsma
 */
final class UMLOptions {
    private final Configuration config;
    private final Set<Doclet.Option> standardOptions;
    private final Set<Doclet.Option> options;

    UMLOptions(Configuration config) {
        this(config, null);
    }

    private UMLOptions(Configuration config, Set<Doclet.Option> standardOptions) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        this.standardOptions = standardOptions;
        this.options = new TreeSet<Doclet.Option>(comparing(o -> o.getNames().get(0), String::compareTo)) {{
            // Options from Standard doclet that we also support
            add(new Option("-d", 1, null, (args) -> config.destDirName = args.get(0)));
            add(new Option("-quiet", 0, Kind.OTHER, (args) -> config.quiet = true));
        }};
    }

    Set<Doclet.Option> mergeWith(final Set<Doclet.Option> standardOptions) {
        if (standardOptions == null || standardOptions.isEmpty()) return this.options;
        Set<Doclet.Option> copy = new UMLOptions(config, standardOptions).options;
        copy.addAll(standardOptions);
        return copy;
    }

    private class Option implements Doclet.Option {
        private final Consumer<List<String>> processor;
        private final String[] names;
        private final String parameters;
        private final String description;
        private final int argCount;
        private final Kind kind;

        protected Option(String name, int argCount, Kind kind, Consumer<List<String>> processor) {
            this.processor = processor;
            this.names = name.trim().split("\\s+");
            String desc = resourceMsg(names[0] + ".description");
            if (desc.isEmpty()) {
                this.description = "<MISSING KEY>";
                this.parameters = "<MISSING KEY>";
            } else {
                this.description = desc;
                this.parameters = resourceMsg(names[0] + ".parameters");
            }
            this.argCount = argCount;
            this.kind = Optional.ofNullable(kind).orElse(Kind.STANDARD);
        }

        private String resourceMsg(String key) {
            try {
                return config.resources().getString(key); // TODO: Bundle seems to be missing!
            } catch (RuntimeException ignore) {
                return "";
            }
        }

        @Override
        public String getDescription() {
            return findDelegate().map(Doclet.Option::getDescription).orElse(description);
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
