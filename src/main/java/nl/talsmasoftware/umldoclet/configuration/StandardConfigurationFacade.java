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

import javax.tools.Diagnostic;
import java.lang.reflect.Field;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Class that serves as an 'anti-corruption' facade between our Doclet
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
final class StandardConfigurationFacade {
    private final Configuration config;
    private Optional<Object> standardConfiguration;

    StandardConfigurationFacade(Configuration config) {
        this.config = requireNonNull(config, "Configuration is <null>.");
        standardConfiguration = null; // Use lazy initialization, that way the reporter may be configured in time.
    }

    /**
     * Try to access the standard (internal) 'ConfigurationImpl' object using reflection, but anticipate exceptions.
     *
     * @return The standard configuration if found.
     */
    private Optional<Object> standardConfiguration() {
        if (standardConfiguration == null) try {
            Field htmlDoclet = config.doclet.getClass().getSuperclass().getDeclaredField("htmlDoclet");
            htmlDoclet.setAccessible(true);
            try {
                Object delegate = htmlDoclet.get(config.doclet);
                Field configuration = delegate.getClass().getDeclaredField("configuration");
                configuration.setAccessible(true);
                try {
                    standardConfiguration = Optional.of(configuration.get(delegate));
                } finally {
                    configuration.setAccessible(false);
                }
            } finally {
                htmlDoclet.setAccessible(false);
            }
        } catch (ReflectiveOperationException | LinkageError | RuntimeException e) {
            if (config.reporter != null) config.reporter.print(Diagnostic.Kind.WARNING,
                    "Skipping options from Standard doclet: Configuration no longer conforms to our expectations!");
            standardConfiguration = Optional.empty();
        }
        return standardConfiguration;
    }

}
