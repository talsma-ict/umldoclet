/*
 * Copyright 2016-2022 Talsma ICT
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
     * The base URL of the <a href="https://www.plantuml.com/plantuml">PlantUML server</a> to generate diagrams with.
     * <p>
     * Please note that it is not recommended to use the public, central PlantUML server at
     * <a href="https://www.plantuml.com/plantuml">https://www.plantuml.com/plantuml</a>.
     * Although not strictly forbidden by the author of PlantUML, using the central server to generate your
     * javadoc diagrams is causing additional load on the central server and is a lot slower than running your own
     * local server.
     * <p>
     * Using docker to run a local PlantUML server can be a simple as:
     * <pre>{@code
     * docker run -d -p 8080:8080 plantuml/plantuml-server:latest
     * }</pre>
     * After that, you can run the UMLDoclet with {@code plantumlServerUrl = "http://localhost:8080/"}
     *
     * @return The base URL of the PlantUML online server to use.
     */
    Optional<String> plantumlServerUrl();

    /**
     * The name of the doclet to delegate main documentation to
     * or {@link Optional#empty} if no delegation is wanted.
     *
     * @return The name of the doclet to delegate main documentation to
     * or {@code Optional.empty()} if no delegation is wanted.
     * @deprecated Delegation to another Doclet does not work at te moment unfortunately.
     */
    @Deprecated(since = "2.0.21", forRemoval = true)
    Optional<String> delegateDocletName();

    /**
     * Configured logger for this doclet.
     * <p>
     * This is a simple, custom logging implementation so we do not have to introduce an external dependency.
     *
     * @return The logger for this application
     */
    Logger logger();

    /**
     * The indentation configuration for generated PlantUML source files.
     *
     * @return The indentation configuration.
     */
    Indentation indentation();

    /**
     * Destination directory for JavaDoc and UML diagrams, or the empty string {@code ""} to use the current directory.
     *
     * @return Destination directory for JavaDoc and UML diagrams, or the empty string {@code ""} for the current directory.
     */
    String destinationDirectory();

    /**
     * Whether PlantUML source files are generated.
     * <p>
     * PlantUML source files have the {@code .puml} filename extension.
     *
     * @return {@code true} if PlantUML source files must be generated, otherwise {@code false}.
     */
    boolean renderPumlFile();

    /**
     * Configuration for generated images.
     *
     * @return Configuration for generated images.
     */
    ImageConfig images();

    /**
     * Configuration for generated UML fields.
     *
     * @return UML field configuration.
     */
    FieldConfig fields();

    /**
     * Configuration for generated UML methods.
     *
     * @return UML method configuration.
     */
    MethodConfig methods();

    /**
     * Names of types that are excluded as reference.
     * <p>
     * Types can be any java type, such as classes and interfaces.
     * <p>
     * Names should match exactly with the fully quallified type names.
     *
     * @return The types (classes, interfaces) that are excluded as references.
     */
    List<String> excludedTypeReferences();

    /**
     * Names of packages that are excluded as package dependencies.
     * <p>
     * The specified package names <em>and any subpackages</em> will be excluded from package dependency diagrams.
     *
     * @return The packages (including subpackages) excluded from the package dependencies.
     */
    List<String> excludedPackageDependencies();

    /**
     * Whether a detected package dependency cycle must result in an error (instead of a warning).
     *
     * @return {@code true} if a detected package dependency cycle must be considered as an error,
     * or {@code false} if it should be reported as merely a warning.
     */
    boolean failOnCyclicPackageDependencies();

    /**
     * Resolves an external link to the specified type.
     *
     * @param packageName The package of the type.
     * @param type        The type name within the package.
     * @return The external link, if resolved.
     */
    Optional<URI> resolveExternalLinkToType(String packageName, String type);

    /**
     * Custom directives to include in rendered PlantUML diagram sources.
     * <p>
     * Custom directives are rendered as-is at the top of each PlantUML diagram.
     * For example, to use jdot for rendering,
     * use the {@code "!pragma graphviz_dot jdot"} custom directive.
     *
     * @return Any custom PlantUML directives.
     */
    List<String> customPlantumlDirectives();

    /**
     * The UML character set can be explicitly configured with the {@code "-umlEncoding"} option.
     * <p>
     * If this is not explicitly set, the {@linkplain #htmlCharset()} will also be used
     * for the {@code PlantUML} source files.
     * <p>
     * This encoding is irrelevant if {@link #renderPumlFile()} is set to {@code false}.<br>
     * Also, diagram files are rendered as binary files, so no explicit encoding is used for them.
     *
     * @return The charset to use for PlantUML source files ({@code ".puml"} files).
     */
    Charset umlCharset();

    /**
     * The {@code HTML} character set is determined the same way the {@code Standard} doclet uses,
     * as we delegate the initial rendering to it:
     * <ol>
     * <li>use the {@code "-docencoding"} if set,</li>
     * <li>otherwise the source encoding ({@code "-encoding"})</li>
     * <li>finally, if no encodings are specified at all,
     * the {@code default platform encoding} is used as implicit fallback.</li>
     * </ol>
     *
     * @return The charset used for Javadoc HTML files.
     */
    Charset htmlCharset();

}
