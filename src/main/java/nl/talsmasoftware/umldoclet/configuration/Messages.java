package nl.talsmasoftware.umldoclet.configuration;

import nl.talsmasoftware.umldoclet.UMLDoclet;

import java.util.*;

import static java.util.ResourceBundle.getBundle;

/**
 * The resource messages used by the doclet.
 * <p>
 * The enumeration is chosen so we can easily test whether all messages
 * are contained by the resource bundle.
 *
 * @author Sjoerd Talsma
 */
public enum Messages {
    VERSION,
    DOCLET_INFO,
    PLANTUML_INFO,

    INFO_GENERATING_FILE,

    ERROR_UNANTICIPATED_ERROR_GENERATING_UML;

    private static final Map<String, ResourceBundle> BUNDLES = new HashMap<>();

    public String toString() {
        return toString(null);
    }

    public String toString(Locale locale) {
        String loc = Optional.ofNullable(locale).map(Object::toString).orElse("");
        ResourceBundle bundle = BUNDLES.get(loc);
        if (bundle == null) {
            bundle = loc.isEmpty() ? getBundle(UMLDoclet.class.getName()) : getBundle(UMLDoclet.class.getName(), locale);
            BUNDLES.put(loc, bundle);
        }
        return bundle.getString(name().toLowerCase().replace('_', '.'));
    }
}
