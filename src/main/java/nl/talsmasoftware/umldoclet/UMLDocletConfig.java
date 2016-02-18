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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

/**
 * Class containing all possible Doclet options for the UML doclet.
 * This configuration class is also responsible for providing suitable default values in the accessor-methods.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class UMLDocletConfig extends EnumMap<UMLDocletConfig.Setting, String[]> {

    enum Setting {
        UML_INDENTATION("-umlIndentation", 1),
        UML_FILE_EXTENSION("-umlFileExtension", 1),
        UML_FILE_ENCODING("-umlFileEncoding", 1),
        UML_CREATE_PACKAGES("-umlCreatePackages", 1);

        private final String optionName;
        private final int optionLength;

        Setting(String option, int optionLength) {
            this.optionName = option;
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

        void validate(String[] optionValue) {
            final int valueCount = optionValue.length - 1;
            if (optionLength != valueCount) {
                throw new IllegalArgumentException(String.format(
                        "Unexpected number of values provided for option \"%s\". Expected %s but received %s.",
                        optionName, optionLength, valueCount));
            } else if (!optionName.equalsIgnoreCase(optionValue[0])) {
                throw new IllegalArgumentException(String.format("Option mismatch. Expected \"%s\", but was \"%s\".",
                        optionName, optionValue[0]));
            }
        }
    }

    private final String basePath;

    public UMLDocletConfig(String[][] options, DocErrorReporter reporter) {
        super(Setting.class);
        for (String[] option : options) {
            final Setting setting = Setting.forOption(option);
            if (setting != null) {
                super.put(setting, option);
            }
        }
        String basePath = ".";
        try {
            basePath = new File(".").getCanonicalPath();
        } catch (IOException ioe) {
            reporter.printError("Could not determine base path: " + ioe.getMessage());
        }
        this.basePath = basePath;
    }

    String stringValue(Setting setting, String defaultValue) {
        final String[] option = super.get(setting);
        return Objects.toString(option == null || option.length < 2 ? null : option[1], defaultValue);
    }

    public String basePath() {
        return basePath;
    }

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
        return stringValue(Setting.UML_FILE_ENCODING, "UTF-8");
    }

    public boolean createPackages() {
        return Boolean.valueOf(stringValue(Setting.UML_CREATE_PACKAGES, "false"));
    }

    public static int optionLength(String option) {
        final Setting setting = Setting.forOption(option);
        return setting == null ? Standard.optionLength(option) : setting.optionLength;
    }

    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        boolean allValid = true;
        final List<String[]> standardOptions = new ArrayList<>();
        for (final String[] option : options) {
            try {
                final Setting setting = Setting.forOption(option);
                if (setting == null) {
                    standardOptions.add(option);
                } else {
                    setting.validate(option);
                }
            } catch (RuntimeException invalid) {
                reporter.printError(invalid.getMessage());
                allValid = false;
            }
        }
        if (!standardOptions.isEmpty()) {
            allValid &= Standard.validOptions(standardOptions.toArray(new String[standardOptions.size()][]), reporter);
        }
        return allValid;
    }

}
