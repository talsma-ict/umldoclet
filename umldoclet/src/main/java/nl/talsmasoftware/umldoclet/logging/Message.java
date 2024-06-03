/*
 * Copyright 2016-2024 Talsma ICT
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
package nl.talsmasoftware.umldoclet.logging;

import nl.talsmasoftware.umldoclet.UMLDoclet;

import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Locale.ENGLISH;
import static java.util.ResourceBundle.getBundle;

/**
 * The resource messages used by the doclet.
 * <p>
 * The enumeration is chosen so we can easily test whether all messages
 * are contained by the resource bundle.
 *
 * @author Sjoerd Talsma
 */
public enum Message {
    DOCLET_VERSION,
    DOCLET_COPYRIGHT,
    DOCLET_UML_FOOTER,
    PLANTUML_COPYRIGHT,
    DEBUG_CONFIGURED_IMAGE_FORMATS,
    DEBUG_SKIPPING_FILE,
    DEBUG_REPLACING_BY,
    DEBUG_CANNOT_READ_ELEMENT_LIST,
    DEBUG_LIVE_PACKAGE_URL_NOT_FOUND,
    DEBUG_PACKAGE_VISITED_BUT_UNDOCUMENTED,
    INFO_GENERATING_FILE,
    INFO_ADD_DIAGRAM_TO_FILE,
    WARNING_UNRECOGNIZED_IMAGE_FORMAT,
    WARNING_CANNOT_READ_PACKAGE_LIST,
    WARNING_UNKNOWN_VISIBILITY,
    WARNING_PACKAGE_DEPENDENCY_CYCLES,
    ERROR_UNSUPPORTED_DELEGATE_DOCLET,
    ERROR_UNANTICIPATED_ERROR_GENERATING_UML,
    ERROR_UNANTICIPATED_ERROR_GENERATING_DIAGRAMS,
    ERROR_UNANTICIPATED_ERROR_POSTPROCESSING_HTML;

    private final String key = name().toLowerCase(ENGLISH).replace('_', '.');

    public String toString() {
        return toString(null);
    }

    public String toString(Locale locale) {
        final String bundleName = UMLDoclet.class.getName();
        final ResourceBundle bundle = getBundle(bundleName, locale == null ? Locale.getDefault() : locale);
        return bundle.getString(key);
    }
}
