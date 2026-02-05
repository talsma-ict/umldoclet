/*
 * Copyright 2016-2026 Talsma ICT
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

/// The resource messages used by the doclet.
///
/// The enumeration is chosen so we can easily test whether all messages
/// are contained by the resource bundle.
///
/// @author Sjoerd Talsma
public enum Message {
    /// UMLDoclet version.
    DOCLET_VERSION,
    /// UMLDoclet copyright statement.
    DOCLET_COPYRIGHT,
    /// Footer to include in generated UML.
    DOCLET_UML_FOOTER,
    /// PlantUml copyright statement.
    PLANTUML_COPYRIGHT,
    /// Configured image formats debug statement.
    DEBUG_CONFIGURED_IMAGE_FORMATS,
    /// Skipping file X debug statement.
    DEBUG_SKIPPING_FILE,
    /// Replacing X by Y debug statement.
    DEBUG_REPLACING_BY,
    /// Cannot read element list debug statement.
    DEBUG_CANNOT_READ_ELEMENT_LIST,
    /// Live package URL not found debug statement.
    DEBUG_LIVE_PACKAGE_URL_NOT_FOUND,
    /// Package visited but undocumented debug statement.
    DEBUG_PACKAGE_VISITED_BUT_UNDOCUMENTED,
    /// Generating file X info statement.
    INFO_GENERATING_FILE,
    /// Adding diagram to file X info statement.
    INFO_ADD_DIAGRAM_TO_FILE,
    /// Warning about unrecognized image format.
    WARNING_UNRECOGNIZED_IMAGE_FORMAT,
    /// Warning that package list could not be read.
    WARNING_CANNOT_READ_PACKAGE_LIST,
    /// Warning about unknown visibility for elements.
    WARNING_UNKNOWN_VISIBILITY,
    /// Warning about dependency cycles.
    WARNING_PACKAGE_DEPENDENCY_CYCLES,
    /// Error that the specified delegate doclet is not supported.
    ERROR_UNSUPPORTED_DELEGATE_DOCLET,
    /// Message for unhandled/unanticipated error while generating the UML.
    ERROR_UNANTICIPATED_ERROR_GENERATING_UML,
    /// Message for unanticipated error generating the Diagrams.
    ERROR_UNANTICIPATED_ERROR_GENERATING_DIAGRAMS,
    /// Message for unanticipated error injecting the diagrams in the HTML output.
    ERROR_UNANTICIPATED_ERROR_POSTPROCESSING_HTML;

    private final String key = name().toLowerCase(ENGLISH).replace('_', '.');

    /// Returns the localized message for the default locale.
    ///
    /// @return The localized message.
    @Override
    public String toString() {
        return toString(null);
    }

    /// Returns the localized message for the given locale.
    ///
    /// @param locale The locale to localize the message for.
    /// @return The localized message.
    public String toString(Locale locale) {
        final String bundleName = UMLDoclet.class.getName();
        final ResourceBundle bundle = getBundle(bundleName, locale == null ? Locale.getDefault() : locale);
        return bundle.getString(key);
    }
}
