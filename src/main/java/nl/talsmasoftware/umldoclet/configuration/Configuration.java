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

import javax.tools.Diagnostic;
import java.util.Locale;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Configuration {

    private final Doclet doclet;
    private final UMLOptions options;
    private volatile LocalizedReporter reporter;

    /**
     * Destination directory where documentation is generated. Default is the current directory.
     */
    public String destDirName = "";

    public boolean quiet = false;

    public Indentation indentation = Indentation.DEFAULT;

    public Configuration(UMLDoclet doclet) {
        this.doclet = requireNonNull(doclet, "UML Doclet is <null>.");
        this.options = new UMLOptions(this);
        this.reporter = new LocalizedReporter(this, null, null);
    }

    public void init(Locale locale, Reporter reporter) {
        this.reporter = new LocalizedReporter(this, reporter, locale);
    }

    public Set<Doclet.Option> mergeOptionsWith(Set<Doclet.Option> standardOptions) {
        return options.mergeWith(standardOptions);
    }

    public Indentation indentation() {
        return Indentation.DEFAULT; // TODO decide whether we want to make this configurable at all.
    }

    public void debug(Message key, Object... args) {
        reporter.log(Diagnostic.Kind.OTHER, null, null, key, args);
    }

    public void info(Message key, Object... args) {
        reporter.log(Diagnostic.Kind.NOTE, null, null, key, args);
    }

    public void warn(Message key, Object... args) {
        reporter.log(Diagnostic.Kind.WARNING, null, null, key, args);
    }

    public void error(Message key, Object... args) {
        reporter.log(Diagnostic.Kind.ERROR, null, null, key, args);
    }

}
