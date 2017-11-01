package nl.talsmasoftware.umldoclet.configuration;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class Configuration {

    public final Doclet doclet;
    public Locale locale;
    public Reporter reporter;

    public Configuration(Doclet doclet) {
        this.doclet = requireNonNull(doclet, "Doclet is <null>.");
    }

}
