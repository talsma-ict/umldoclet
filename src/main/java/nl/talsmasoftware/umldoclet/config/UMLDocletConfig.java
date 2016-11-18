/*
 * Copyright (C) 2016 Talsma ICT
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
 *
 */
package nl.talsmasoftware.umldoclet.config;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;

import static nl.talsmasoftware.umldoclet.config.UMLDocletConfig.Setting.*;
import static nl.talsmasoftware.umldoclet.logging.LogSupport.*;
import static nl.talsmasoftware.umldoclet.model.Model.isDeprecated;
import static nl.talsmasoftware.umldoclet.rendering.indent.Indentation.spaces;
import static nl.talsmasoftware.umldoclet.rendering.indent.Indentation.tabs;

/**
 * Class containing all possible Doclet options for the UML doclet.
 * This configuration class is also responsible for providing suitable default values in a central location.
 *
 * @author Sjoerd Talsma
 */
public class UMLDocletConfig extends EnumMap<UMLDocletConfig.Setting, Object> {

    public enum Setting {
        UML_LOGLEVEL("umlLogLevel", "INFO"),
        UML_INDENTATION("umlIndentation", "-1"),
        UML_BASE_PATH("umlBasePath", "."),
        UML_FILE_EXTENSION("umlFileExtension", ".puml"),
        UML_FILE_ENCODING("umlFileEncoding", null),
        UML_SKIP_STANDARD_DOCLET("umlSkipStandardDoclet", false),
        UML_INCLUDE_PRIVATE_FIELDS("umlIncludePrivateFields", false),
        UML_INCLUDE_PACKAGE_PRIVATE_FIELDS("umlIncludePackagePrivateFields", false),
        UML_INCLUDE_PROTECTED_FIELDS("umlIncludeProtectedFields", true),
        UML_INCLUDE_PUBLIC_FIELDS("umlIncludePublicFields", true),
        UML_INCLUDE_DEPRECATED_FIELDS("umlIncludeDeprecatedFields", false),
        UML_INCLUDE_FIELD_TYPES("umlIncludeFieldTypes", true),
        UML_INCLUDE_METHOD_PARAM_NAMES("umlIncludeMethodParamNames", false),
        UML_INCLUDE_METHOD_PARAM_TYPES("umlIncludeMethodParamTypes", true),
        UML_INCLUDE_METHOD_RETURNTYPES("umlIncludeMethodReturntypes", true),
        UML_INCLUDE_CONSTRUCTORS("umlIncludeConstructors", true),
        UML_INCLUDE_DEFAULT_CONSTRUCTORS("umlIncludeDefaultConstructors", false),
        UML_INCLUDE_PRIVATE_METHODS("umlIncludePrivateMethods", false),
        UML_INCLUDE_PACKAGE_PRIVATE_METHODS("umlIncludePackagePrivateMethods", false),
        UML_INCLUDE_PROTECTED_METHODS("umlIncludeProtectedMethods", true),
        UML_INCLUDE_PUBLIC_METHODS("umlIncludePublicMethods", true),
        UML_INCLUDE_DEPRECATED_METHODS("umlIncludeDeprecatedMethods", false),
        UML_INCLUDE_ABSTRACT_SUPERCLASS_METHODS("umlIncludeAbstractSuperclassMethods", true),
        UML_INCLUDE_PRIVATE_CLASSES("umlIncludePrivateClasses", false),
        UML_INCLUDE_PACKAGE_PRIVATE_CLASSES("umlIncludePackagePrivateClasses", true),
        UML_INCLUDE_PROTECTED_CLASSES("umlIncludeProtectedClasses", true),
        UML_INCLUDE_DEPRECATED_CLASSES("umlIncludeDeprecatedClasses", false),
        UML_INCLUDE_PRIVATE_INNERCLASSES("umlIncludePrivateInnerClasses", false),
        UML_INCLUDE_PACKAGE_PRIVATE_INNERCLASSES("umlIncludePackagePrivateInnerClasses", false),
        UML_INCLUDE_PROTECTED_INNERCLASSES("umlIncludeProtectedInnerClasses", false),
        UML_EXCLUDED_REFERENCES(new ListSetting("umlExcludedReferences", "java.lang.Object", "java.lang.Enum")),
        UML_INCLUDE_OVERRIDES_FROM_EXCLUDED_REFERENCES("umlIncludeOverridesFromExcludedReferences", false),
        UML_PACKAGE_DEPENDENCIES("umlPackageDependencies", true),
        UML_COMMAND(new ListSetting("umlCommand")),
        UML_ALWAYS_USE_QUALIFIED_CLASSNAMES("umlAlwaysUseQualifiedClassnames", false),
        UML_IMAGE_FORMAT(new ListSetting("umlImageFormat")),
        UML_IMAGE_DIRECTORY("umlImageDirectory", null);

        private final AbstractSetting<?> delegate;

        Setting(String name, String defaultValue) {
            this(new StringSetting(name, defaultValue));
        }

        Setting(String name, boolean defaultValue) {
            this(new BooleanSetting(name, defaultValue));
        }

        Setting(String name, int defaultValue) {
            this(new IntegerSetting(name, defaultValue));
        }

        Setting(AbstractSetting delegate) {
            this.delegate = delegate;
        }

        private static Setting forOption(String... option) {
            if (option != null && option.length > 0) {
                for (Setting setting : values()) {
                    if (setting.delegate.matches(option[0])) {
                        return setting;
                    }
                }
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        <T> T value(UMLDocletConfig config) {
            return (T) delegate.value(config.get(this));
        }
    }

    private final boolean valid;
    private final String[][] standardOptions;
    private final Properties properties;

    public UMLDocletConfig(String[][] options, DocErrorReporter reporter) {
        super(Setting.class);
        boolean allOptionsValid = true;
        LogSupport.setReporter(reporter);
        List<String[]> stdOpts = new ArrayList<>();
        for (String[] option : options) {
            final Setting setting = Setting.forOption(option);
            if (setting == null) {
                stdOpts.add(option);
            } else if (setting.delegate.validate(option)) {
                this.put(setting, setting.delegate.parse(option, get(setting)));
            } else {
                allOptionsValid = false;
            }
        }
        this.valid = allOptionsValid;
        this.standardOptions = stdOpts.toArray(new String[stdOpts.size()][]);
        LogSupport.setLevel(UML_LOGLEVEL.value(this));
        try {
            String basePath = UML_BASE_PATH.value(this);
            trace("Configured UML base path: \"{0}\".", basePath);
            if (basePath.startsWith("file:")) {
                basePath = basePath.substring("file:".length());
                if (!"/".equals(File.separator)) {
                    basePath = basePath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                }
                trace("Translated UML base path: \"{0}\".", basePath);
            }
            this.put(UML_BASE_PATH, new File(basePath).getCanonicalPath());
        } catch (IOException ioe) {
            warn("Error converting base path \"{0}\" to a canonical path: {1}",
                    UML_BASE_PATH.value(this), ioe);
        }
        this.properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/META-INF/umldoclet.properties")) {
            properties.load(in);
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O exception loading UML Doclet properties: " + ioe.getMessage(), ioe);
        }
    }

    public static int optionLength(String option) {
        return Setting.forOption(option) == null ? 0 : 2;
    }

    /**
     * @return Whether all provided options for this Doclet contained valid values.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return The version of the doclet, so it can be printed as a notice.
     */
    public String version() {
        return properties.getProperty("version", "<unknown version>");
    }

    /**
     * @return The base path where the documentation should be created.
     */
    public String basePath() {
        return UML_BASE_PATH.value(this);
    }

    /**
     * @return The indentation (in number of spaces) to use for generated UML files
     * (defaults to {@code -1} which leaves the indentation unspecified).
     */
    public Indentation indentation() {
        final String value = UML_INDENTATION.value(this);
        try {
            return "tabs".equalsIgnoreCase(value) ? tabs(0) : spaces(Integer.valueOf(value), 0);
        } catch (NumberFormatException badInteger) {
            trace("Invalid integer option \"{0}\": {1}", value, badInteger);
            error("Expected boolean value, but got \"{0}\" for option \"{1}\".",
                    value, UML_INDENTATION.delegate.name);
            return Indentation.DEFAULT;
        }
    }

    /**
     * @return The file extension for the PlantUML files (defaults to {@code ".puml"}).
     */
    public String umlFileExtension() {
        final String extension = UML_FILE_EXTENSION.value(this);
        return extension.startsWith(".") ? extension : "." + extension;
    }

    /**
     * @return The file character encoding for the PlantUML files (defaults to {@code "UTF-8"}).
     */
    public String umlFileEncoding() {
        // TODO: look for default setting "-docEncoding" as fallback..
        String encoding = UML_FILE_ENCODING.value(this);
        if (encoding == null) {
            for (String[] stdOption : standardOptions) {
                if (stdOption.length > 1 && "-docEncoding".equalsIgnoreCase(stdOption[0])) {
                    encoding = stdOption[1];
                    debug("Setting UML file encoding to \"{0}\" from standard Doclet option \"{1}\", because \"{2}\" was not specified.",
                            encoding, "-docEncoding", "-" + UML_FILE_ENCODING.delegate.name);
                    break;
                }
            }
            if (encoding == null) {
                encoding = "UTF-8";
                debug("Setting UML file encoding to \"{0}\" by default.", encoding);
            }
            this.put(UML_FILE_ENCODING, encoding);
        }
        return encoding;
    }

    public boolean skipStandardDoclet() {
        return UML_SKIP_STANDARD_DOCLET.value(this);
    }

    /**
     * @return Whether or not to include private fields in the UML diagrams (defaults to {@code false}).
     */
    public boolean includePrivateFields() {
        return UML_INCLUDE_PRIVATE_FIELDS.value(this);
    }

    /**
     * @return Whether or not to include package-private fields in the UML diagrams (defaults to {@code false}).
     */
    public boolean includePackagePrivateFields() {
        return UML_INCLUDE_PACKAGE_PRIVATE_FIELDS.value(this);
    }

    /**
     * @return Whether or not to include private fields in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeProtectedFields() {
        return UML_INCLUDE_PROTECTED_FIELDS.value(this);
    }

    /**
     * @return Whether or not to include public fields in the UML diagrams (defaults to {@code true}).
     */
    public boolean includePublicFields() {
        return UML_INCLUDE_PUBLIC_FIELDS.value(this);
    }

    /**
     * @return Whether or not to include deprecated fields in the UML diagrams (defaults to {@code false}).
     */
    public boolean includeDeprecatedFields() {
        return UML_INCLUDE_DEPRECATED_FIELDS.value(this);
    }

    /**
     * @return Whether or not to include field type details in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeFieldTypes() {
        return UML_INCLUDE_FIELD_TYPES.value(this);
    }

    /**
     * This configuration delegate cannot be directly provided via a single option.
     * This is a combination of the {@code "-umlIncludeMethodParamNames"} OR {@code "-umlIncludeMethodParamTypes"}.
     *
     * @return Whether or not to include method parameters in the UML diagrams (either by name, type or both).
     */
    public boolean includeMethodParams() {
        return includeMethodParamNames() || includeMethodParamTypes();
    }

    /**
     * @return Whether or not to include method parameter names in the UML diagrams (defaults to {@code false}).
     */
    public boolean includeMethodParamNames() {
        return UML_INCLUDE_METHOD_PARAM_NAMES.value(this);
    }

    /**
     * @return Whether or not to include method parameter types in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeMethodParamTypes() {
        return UML_INCLUDE_METHOD_PARAM_TYPES.value(this);
    }

    public boolean includeMethodReturntypes() {
        return UML_INCLUDE_METHOD_RETURNTYPES.value(this);
    }

    /**
     * Please note that even when constructors are included, they are either rendered or not, based on the various
     * method visibility settings such as {@code "-includePrivateMethods", "-includePackagePrivateMethods",
     * "-includeProtectedMethods"} and {@code "-includePublicMethods"}.
     *
     * @return Whether or not to include any constructors in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeConstructors() {
        return UML_INCLUDE_CONSTRUCTORS.value(this);
    }

    public boolean includeDefaultConstructors() {
        return UML_INCLUDE_DEFAULT_CONSTRUCTORS.value(this);
    }

    /**
     * @return Whether or not to include private methods in the UML diagrams (defaults to {@code false}).
     */
    public boolean includePrivateMethods() {
        return UML_INCLUDE_PRIVATE_METHODS.value(this);
    }

    /**
     * @return Whether or not to include package-private methods in the UML diagrams (defaults to {@code false}).
     */
    public boolean includePackagePrivateMethods() {
        return UML_INCLUDE_PACKAGE_PRIVATE_METHODS.value(this);
    }

    /**
     * @return Whether or not to include private methods in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeProtectedMethods() {
        return UML_INCLUDE_PROTECTED_METHODS.value(this);
    }

    /**
     * @return Whether or not to include public methods in the UML diagrams (defaults to {@code true}).
     */
    public boolean includePublicMethods() {
        return UML_INCLUDE_PUBLIC_METHODS.value(this);
    }

    /**
     * @return Whether or not to include deprecated methods in the UML diagrams (defaults to {@code false}).
     */
    public boolean includeDeprecatedMethods() {
        return UML_INCLUDE_DEPRECATED_METHODS.value(this);
    }

    /**
     * @return Whether or not to include abstract methods from interfaces and abstract classes
     * (from referenced external packages) in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeAbstractSuperclassMethods() {
        return UML_INCLUDE_ABSTRACT_SUPERCLASS_METHODS.value(this);
    }

    private boolean includePrivateClasses() {
        return UML_INCLUDE_PRIVATE_CLASSES.value(this);
    }

    private boolean includePackagePrivateClasses() {
        return UML_INCLUDE_PACKAGE_PRIVATE_CLASSES.value(this);
    }

    private boolean includeProtectedClasses() {
        return UML_INCLUDE_PROTECTED_CLASSES.value(this);
    }

    private boolean includeDeprecatedClasses() {
        return UML_INCLUDE_DEPRECATED_CLASSES.value(this);
    }

    private boolean includePrivateInnerclasses() {
        return UML_INCLUDE_PRIVATE_INNERCLASSES.value(this);
    }

    private boolean includePackagePrivateInnerclasses() {
        return UML_INCLUDE_PACKAGE_PRIVATE_INNERCLASSES.value(this);
    }

    private boolean includeProtectedInnerclasses() {
        return UML_INCLUDE_PROTECTED_INNERCLASSES.value(this);
    }

    public boolean includeClass(ClassDoc classDoc) {
        if (classDoc == null) {
            warn("Encountered <null> class documentation!");
            return false;
        }
        boolean included = true;
        final boolean isInnerclass = classDoc.containingClass() != null;
        if (classDoc.isPrivate() && (!includePrivateClasses() || (isInnerclass && !includePrivateInnerclasses()))) {
            trace("Not including private class \"{0}\".", classDoc.qualifiedName());
            included = false;
        } else if (classDoc.isPackagePrivate()
                && (!includePackagePrivateClasses() || isInnerclass && !includePackagePrivateInnerclasses())) {
            debug("Not including package-private class \"{0}\".", classDoc.qualifiedName());
            included = false;
        } else if (classDoc.isProtected()
                && (!includeProtectedClasses() || isInnerclass && !includeProtectedInnerclasses())) {
            debug("Not including protected class \"{0}\".", classDoc.qualifiedName());
            included = false;
        } else if (isDeprecated(classDoc) && !includeDeprecatedClasses()) {
            debug("Not including deprecated class \"{0}\".", classDoc.qualifiedName());
            included = false;
        }

        trace("{0} class \"{1}\".", included ? "Including" : "Not including", classDoc.qualifiedName());
        return included;
    }

    /**
     * @return The excluded references which should not be rendered.
     */
    public Collection<String> excludedReferences() {
        return UML_EXCLUDED_REFERENCES.value(this);
    }

    /**
     * @return Whether or not to include overridden methods declared by excluded references
     * (i.e. include java.lang.Object methods?), defaults to {@code false}.
     */
    public boolean includeOverridesFromExcludedReferences() {
        return UML_INCLUDE_OVERRIDES_FROM_EXCLUDED_REFERENCES.value(this);
    }

    public boolean alwaysUseQualifiedClassnames() {
        return UML_ALWAYS_USE_QUALIFIED_CLASSNAMES.value(this);
    }

    public List<String> umlCommands() {
        return UML_COMMAND.value(this);
    }

    public boolean supportLegacyTags() {
        return true;
    }

    /**
     * Internal setting that can be used for troubleshooting fields that are replaced by certain references.
     * In case references are added, the corresponding fields are 'disabled' (commented out in the class diagram).
     * This is unnecessary, and these fields should be omitted by default,
     * however it might be useful for troubleshooting to be able to at least include them commented the .puml output.
     *
     * @return Whether 'disabled' fields should be included in the class diagrams (although commented out).
     */
    public boolean includeDisabledFields() {
        return false;
    }

    /**
     * @return The configured image formats to be generated when PlantUmL is detected (e.g. "PNG", "SVG", etc).
     */
    public String[] imageFormats() {
        final List<String> imageFormats = UML_IMAGE_FORMAT.value(this);
        return imageFormats.toArray(new String[imageFormats.size()]);
    }

    /**
     * @return The configured image directory or <code>null</code> if none is configured.
     */
    public String imageDirectory() {
        return UML_IMAGE_DIRECTORY.value(this);
    }

    public boolean usePackageDependencies() {
        return UML_PACKAGE_DEPENDENCIES.value(this);
    }

    /**
     * @return The options that were specified to the UML Doclet, but were not recognized by it.
     */
    public String[][] standardOptions() {
        return standardOptions;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getClass().getSimpleName()).append(super.toString())
                .append(",StandardOptions{");
        String sep = "";
        for (String[] option : standardOptions) {
            if (option.length > 0) {
                result.append(sep).append(option[0]);
                if (option.length > 1) result.append(":").append(option[1]);
                for (int i = 2; i < option.length; i++) result.append(' ').append(option[i]);
                sep = ", ";
            }
        }
        return result.append('}').toString();
    }

}
