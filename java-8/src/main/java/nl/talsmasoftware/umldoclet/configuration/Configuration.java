/*
 * Copyright 2016-2019 Talsma ICT
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

import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

/**
 * Configuration that influences <em>how</em> UML should be rendered.
 *
 * @author Sjoerd Talsma
 */
public interface Configuration {

    /**
     * @return The name of the doclet to delegate main documentation to
     * or {@code Optional.empty()} if no delegation is wanted.
     */
    Optional<String> delegateDocletName();

    /**
     * Custom logger implementation that only supports fixed messages.
     * <p>
     * This allows us to write unit tests that verify resource bundle availability of all loggable text.
     *
     * @return The logger for this application
     */
    Logger logger();

    /**
     * @return The configured indentation within the generated UML files.
     */
    Indentation indentation();

    /**
     * @return The destination directory for the UML diagrams, or the empty string {@code ""} for the current directory.
     */
    String destinationDirectory();

    /**
     * @return Whether or not to render PlantUML {@code .puml} files.
     */
    boolean renderPumlFile();

    /**
     * @return The configuration for the images that are generated.
     */
    ImageConfig images();

    /**
     * @return The part of the configuration that determines how fields are rendered.
     */
    FieldConfig fields();

    /**
     * @return The part of the configuration that determines how methods are rendered.
     */
    MethodConfig methods();

    /**
     * @return The types (classes, interfaces) that are excluded as references.
     */
    List<String> excludedTypeReferences();

    /**
     * @return The packages (including subpackages) excluded from the package dependencies.
     */
    List<String> excludedPackageDependencies();

    /**
     * @return Whether or not to fail when cyclic package dependencies are detected.
     */
    boolean failOnCyclicPackageDependencies();

    /**
     * Resolves an external link to the specified type.
     *
     * @param packageName The package of the type.
     * @param type        The type name within the package.
     * @return The external link, if resolved
     */
    Optional<URI> resolveExternalLinkToType(String packageName, String type);

    /**
     * The UML character set can be explicitly configured with the {@code "-umlEncoding"} option.
     * <p>
     * If this is not explicitly set, the {@linkplain #htmlCharset()} will also be used
     * for the {@code PlantUML} source files.
     *
     * @return The charset to use for PlantUML files
     */
    Charset umlCharset();

    /**
     * The {@code HTML} character set is determined the same way the {@code Standard} doclet uses,
     * as we delegate the initial rendering to it:
     * <ol>
     * <li>use the {@code "-docencoding"} if set,</li>
     * <li>otherwise the source encoding ({@code "-encoding"})</li>
     * <li>finally, if no encodings are specified at all,
     * the {@code default platform encoding} is used as implicit fallback</li>
     * </ol>
     *
     * @return The charset used for Javadoc HTML files
     */
    Charset htmlCharset();

}
