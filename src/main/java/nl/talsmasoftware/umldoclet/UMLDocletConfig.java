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
 */
package nl.talsmasoftware.umldoclet;

import com.sun.javadoc.DocErrorReporter;
import com.sun.tools.doclets.standard.Standard;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * Class containing all possible Doclet options for the UML doclet.
 * This configuration class is also responsible for providing suitable default values in the accessor-methods.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class UMLDocletConfig extends EnumMap<UMLDocletConfig.Setting, String[]> implements Closeable {
    private static final String UML_ROOTLOGGER_NAME = UMLDoclet.class.getPackage().getName();
    private static final Logger LOGGER = Logger.getLogger(UMLDocletConfig.class.getName());

    enum Setting {
        UML_LOGLEVEL("-umlLogLevel", String.class),
        UML_INDENTATION("-umlIndentation", Integer.class),
        UML_BASE_PATH("-umlBasePath", String.class),
        UML_FILE_EXTENSION("-umlFileExtension", String.class),
        UML_FILE_ENCODING("-umlFileEncoding", String.class),
        UML_CREATE_PACKAGES("-umlCreatePackages", Boolean.class),
        UML_INCLUDE_PRIVATE_FIELDS("-umlIncludePrivateFields", Boolean.class),
        UML_INCLUDE_PACKAGE_PRIVATE_FIELDS("-umlIncludePackagePrivateFields", Boolean.class),
        UML_INCLUDE_PROTECTED_FIELDS("-umlIncludeProtectedFields", Boolean.class),
        UML_INCLUDE_PUBLIC_FIELDS("-umlIncludePublicFields", Boolean.class),
        UML_INCLUDE_FIELD_TYPES("-umlIncludeFieldTypes", Boolean.class),
        UML_INCLUDE_METHOD_PARAM_NAMES("-umlIncludeMethodParamNames", Boolean.class),
        UML_INCLUDE_METHOD_PARAM_TYPES("-umlIncludeMethodParamTypes", Boolean.class),
        UML_INCLUDE_PRIVATE_METHODS("-umlIncludePrivateMethods", Boolean.class),
        UML_INCLUDE_PACKAGE_PRIVATE_METHODS("-umlIncludePackagePrivateMethods", Boolean.class),
        UML_INCLUDE_PROTECTED_METHODS("-umlIncludeProtectedMethods", Boolean.class),
        UML_INCLUDE_PUBLIC_METHODS("-umlIncludePublicMethods", Boolean.class);

        private final String optionName;
        private final Class<?> optionType;
        private final int optionLength;

        Setting(String option, Class<?> type) {
            this(option, type, 2); // By default, declare one option and one parameter string.
        }

        Setting(String option, Class<?> type, int optionLength) {
            this.optionName = option;
            this.optionType = type;
            this.optionLength = optionLength;
        }

        private static Setting forOption(String... option) {
            if (option != null && option.length > 0) {
                for (Setting setting : values()) {
                    if (setting.optionName.equalsIgnoreCase(option[0].trim())) {
                        return setting;
                    }
                }
            }
            return null;
        }

        String[] validate(String[] optionValue) {
            if (optionLength != optionValue.length) {
                throw new IllegalArgumentException(String.format(
                        "Expected %s but received %s: %s.",
                        optionLength, optionValue.length, Arrays.toString(optionValue)));
            }
            final String value = optionLength > 1 ? optionValue[1].trim() : null;
            if (Boolean.class.equals(optionType)
                    && !"true".equalsIgnoreCase(value)
                    && !"false".equalsIgnoreCase(value)) {
                throw new IllegalArgumentException(
                        String.format("Expected \"true\" or \"false\", but received \"%s\".", value));
            } else if (Integer.class.equals(optionType) && !value.isEmpty() && !value.matches("\\d+")) {
                throw new IllegalArgumentException(
                        String.format("Expected a numerical value, but received \"%s\".", value));
            }
            return optionValue;
        }
    }

    private final String defaultBasePath;
    private final String[][] invalidOptions;
    private final String[][] standardOptions;
    private Properties properties;
    private Handler umlLogHandler;

    public UMLDocletConfig(String[][] options, DocErrorReporter reporter) {
        super(Setting.class);
        String basePath = ".";
        try {
            basePath = new File(".").getCanonicalPath();
        } catch (IOException ioe) {
            reporter.printError("Could not determine base path: " + ioe.getMessage());
        }
        this.defaultBasePath = basePath;
        List<String[]> stdOpts = new ArrayList<>(), invalidOpts = new ArrayList<>();
        for (String[] option : options) {
            try {
                final Setting setting = Setting.forOption(option);
                if (setting == null) {
                    stdOpts.add(option);
                } else {
                    super.put(setting, setting.validate(option));
                }
            } catch (RuntimeException invalid) {
                reporter.printError(String.format("Invalid option \"%s\". %s", option[0], invalid.getMessage()));
                invalidOpts.add(option);
            }
        }
        this.standardOptions = stdOpts.toArray(new String[stdOpts.size()][]);
        this.invalidOptions = invalidOpts.toArray(new String[invalidOpts.size()][]);
        initializeUmlLogging();
    }

    private Properties properties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream in = getClass().getResourceAsStream("/META-INF/umldoclet.properties")) {
                properties.load(in);
            } catch (IOException ioe) {
                throw new IllegalStateException("I/O exception loading properties: " + ioe.getMessage(), ioe);
            }
        }
        return properties;
    }

    String stringValue(Setting setting, String defaultValue, String... standardOpts) {
        final String[] option = super.get(setting);
        String value = Objects.toString(option == null || option.length < 2 ? null : option[1], null);
        for (int i = 0; value == null && i < standardOpts.length; i++) {
            for (int j = 0; j < standardOptions.length; j++) {
                if (standardOptions[j].length > 1
                        && standardOpts[i].equalsIgnoreCase(standardOptions[j][0])) {
                    value = standardOptions[j][1];
                    LOGGER.log(Level.FINEST, "Using standard option \"{0}\" for setting \"{1}\": \"{2}\".",
                            new Object[] {standardOpts[i], setting, value});
                    break;
                }
            }
        }
        return Objects.toString(value, defaultValue);
    }

    public String version() {
        return properties().getProperty("version");
    }

    public Level umlLogLevel() {
        final String level = stringValue(Setting.UML_LOGLEVEL, "INFO").toUpperCase(Locale.ENGLISH);
        switch (level) {
            case "TRACE":
                return Level.FINEST;
            case "DEBUG":
                return Level.FINE;
            case "WARN":
                return Level.WARNING;
            case "ERROR":
            case "FATAL":
                return Level.SEVERE;
            default:
                return Level.parse(level);
        }
    }

    /**
     * @return The base path where the documentation should be created.
     */
    public String basePath() {
        return stringValue(Setting.UML_BASE_PATH, defaultBasePath);
    }

    /**
     * @return The indentation (in number of spaces) to use for generated UML files
     * (defaults to {@code -1} which leaves the indentation unspecified).
     */
    public int indentation() {
        return Integer.valueOf(stringValue(Setting.UML_INDENTATION, "-1"));
    }

    /**
     * @return The file extension for the PlantUML files (defaults to {@code ".puml"}).
     */
    public String umlFileExtension() {
        final String extension = stringValue(Setting.UML_FILE_EXTENSION, ".puml");
        return extension.startsWith(".") ? extension : "." + extension;
    }

    /**
     * @return The file character encoding for the PlantUML files (defaults to {@code "UTF-8"}).
     */
    public String umlFileEncoding() {
        return stringValue(Setting.UML_FILE_ENCODING, "UTF-8", "-docEncoding");
    }

    /**
     * @return Whether or not to include private fields in the UML diagrams (defaults to {@code false}).
     */
    public boolean includePrivateFields() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_PRIVATE_FIELDS, "false"));
    }

    /**
     * @return Whether or not to include package-private fields in the UML diagrams (defaults to {@code false}).
     */
    public boolean includePackagePrivateFields() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_PACKAGE_PRIVATE_FIELDS, "false"));
    }

    /**
     * @return Whether or not to include private fields in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeProtectedFields() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_PROTECTED_FIELDS, "true"));
    }

    /**
     * @return Whether or not to include public fields in the UML diagrams (defaults to {@code true}).
     */
    public boolean includePublicFields() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_PUBLIC_FIELDS, "true"));
    }

    /**
     * @return Whether or not to include field type details in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeFieldTypes() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_FIELD_TYPES, "true"));
    }

    /**
     * This configuration setting cannot be directly provided via a single option.
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
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_METHOD_PARAM_NAMES, "false"));
    }

    /**
     * @return Whether or not to include method parameter types in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeMethodParamTypes() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_METHOD_PARAM_TYPES, "true"));
    }

    /**
     * @return Whether or not to include private methods in the UML diagrams (defaults to {@code false}).
     */
    public boolean includePrivateMethods() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_PRIVATE_METHODS, "false"));
    }

    /**
     * @return Whether or not to include package-private methods in the UML diagrams (defaults to {@code false}).
     */
    public boolean includePackagePrivateMethods() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_PACKAGE_PRIVATE_METHODS, "false"));
    }

    /**
     * @return Whether or not to include private methods in the UML diagrams (defaults to {@code true}).
     */
    public boolean includeProtectedMethods() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_PROTECTED_METHODS, "true"));
    }

    /**
     * @return Whether or not to include public methods in the UML diagrams (defaults to {@code true}).
     */
    public boolean includePublicMethods() {
        return Boolean.valueOf(stringValue(Setting.UML_INCLUDE_PUBLIC_METHODS, "true"));
    }

    public boolean createPackages() {
        return Boolean.valueOf(stringValue(Setting.UML_CREATE_PACKAGES, "false"));
    }

    public static int optionLength(String option) {
        final Setting setting = Setting.forOption(option);
        return setting == null ? Standard.optionLength(option) : setting.optionLength;
    }

    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        try (UMLDocletConfig config = new UMLDocletConfig(options, reporter)) {
            return Standard.validOptions(config.standardOptions, reporter)
                    && config.invalidOptions.length == 0;
        }
    }

    private void initializeUmlLogging() {
        // Clear levels on any previously instantiated sub-loggers.
        for (Enumeration<String> en = LogManager.getLogManager().getLoggerNames(); en.hasMoreElements(); ) {
            String loggerName = en.nextElement();
            if (loggerName.startsWith(UML_ROOTLOGGER_NAME)) {
                Logger.getLogger(loggerName).setLevel(null);
            }
        }
        // Configure the umldoclet root logger.
        this.umlLogHandler = new UmlLogHandler();
        Logger.getLogger(UML_ROOTLOGGER_NAME).setLevel(this.umlLogLevel());
        Logger.getLogger(UML_ROOTLOGGER_NAME).addHandler(this.umlLogHandler);
    }

    @Override
    public synchronized void close() {
        if (umlLogHandler != null) {
            Logger.getLogger(UML_ROOTLOGGER_NAME).removeHandler(umlLogHandler);
            umlLogHandler = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getClass().getSimpleName()).append('{');
        String sep = "";
        for (String[] option : super.values()) {
            if (option.length > 0) {
                result.append(sep).append(option[0]);
                if (option.length > 1) result.append(":").append(option[1]);
                for (int i = 2; i < option.length; i++) result.append(' ').append(option[i]);
                sep = ", ";
            }
        }
        result.append("},StandardOptions{");
        sep = "";
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

    private static class UmlLogHandler extends ConsoleHandler {
        private UmlLogHandler() {
            super.setLevel(Level.ALL);
            super.setOutputStream(System.out);
            super.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format("%s%n", super.formatMessage(record));
                }
            });
        }
    }
}
