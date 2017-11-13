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
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

public class Configuration {

    final Doclet doclet;
    private final StandardConfigurationOptions standardConfig;
    private Locale locale;
    private Reporter reporter = new SysoutReporter();
    private DocletEnvironment env = null;

    public Configuration(UMLDoclet doclet) {
        this.doclet = requireNonNull(doclet, "UML Doclet is <null>.");
        this.standardConfig = new StandardConfigurationOptions(this);
    }

    public void init(Locale locale, Reporter reporter) {
        if (locale != null) this.locale = locale;
        if (reporter != null) this.reporter = reporter;
    }

    public ResourceBundle resources() {
        try {
            return ResourceBundle.getBundle(UMLDoclet.class.getName(), locale == null ? Locale.getDefault() : locale);
        } catch (MissingResourceException mre) {
            throw new IllegalStateException("Missing resourcebundle for UMLDoclet.", mre);
        }
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
        return supportedOptions;
    }

    public Indentation indentation() {
        return Indentation.DEFAULT; // TODO decide whether we want to make this configurable at all.
    }
}
