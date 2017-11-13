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
import jdk.javadoc.doclet.Reporter;
import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

public class Configuration {

    final Doclet doclet;
    private final StandardConfigurationOptions standardConfig;
    private Reporter reporter = new SysoutReporter();

    public Configuration(UMLDoclet doclet) {
        this.doclet = requireNonNull(doclet, "UML Doclet is <null>.");
        this.standardConfig = new StandardConfigurationOptions(this);
    }

    public void init(Locale locale, Reporter reporter) {
        // this.locale = locale; // Wait until we need it.
        if (reporter != null) this.reporter = reporter;
    }

    /**
     * @return The reporter for the JavaDoc task.
     */
    public Reporter reporter() {
        return reporter;
    }

    public Set<Doclet.Option> getSupportedOptions() {
        Set<Doclet.Option> supportedOptions = new TreeSet<>(comparing(o -> o.getNames().get(0), String::compareTo));
        supportedOptions.addAll(standardConfig.getSupportedStandardOptions());
        // TODO add our own custom options.
        return supportedOptions;
    }

    public Indentation indentation() {
        return Indentation.DEFAULT; // TODO decide whether we want to make this configurable at all.
    }
}
