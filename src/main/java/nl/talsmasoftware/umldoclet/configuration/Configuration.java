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

import com.sun.source.util.DocTreePath;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Configuration {

    final Doclet doclet;
    private final UMLOptions options;
    private final ReporterImpl reporter;
    private Locale locale;
    private DocletEnvironment env = null;

    /**
     * Destination directory name, in which doclet will generate the entire
     * documentation. Default is current directory.
     */
    public String destDirName = "";

    public boolean quiet = false;

    public Configuration(UMLDoclet doclet) {
        this.doclet = requireNonNull(doclet, "UML Doclet is <null>.");
        this.options = new UMLOptions(this);
        this.reporter = new ReporterImpl();
    }

    public void init(Locale locale, Reporter reporter) {
        this.locale = locale;
        this.reporter.delegate = reporter;
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

    public Set<Doclet.Option> mergeOptionsWith(Set<Doclet.Option> standardOptions) {
        return options.mergeWith(standardOptions);
    }

    public Indentation indentation() {
        return Indentation.DEFAULT; // TODO decide whether we want to make this configurable at all.
    }

    private class ReporterImpl implements Reporter {
        private Reporter delegate = null;

        private boolean mustPrint(Diagnostic.Kind kind) {
            Diagnostic.Kind threshold = quiet ? Diagnostic.Kind.WARNING : Diagnostic.Kind.NOTE;
            return kind != null && kind.compareTo(threshold) <= 0;
        }

        @Override
        public void print(Diagnostic.Kind kind, String msg) {
            if (mustPrint(kind)) {
                if (delegate == null) System.out.println(msg);
                else delegate.print(kind, msg);
            }
        }

        @Override
        public void print(Diagnostic.Kind kind, DocTreePath path, String msg) {
            if (mustPrint(kind)) {
                if (delegate == null) System.out.println(msg);
                else delegate.print(kind, path, msg);
            }
        }

        @Override
        public void print(Diagnostic.Kind kind, Element e, String msg) {
            if (mustPrint(kind)) {
                if (delegate == null) System.out.println(msg);
                else delegate.print(kind, e, msg);
            }
        }
    }

}
