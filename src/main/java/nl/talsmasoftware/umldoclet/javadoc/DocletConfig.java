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
package nl.talsmasoftware.umldoclet.javadoc;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.FieldConfig;
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;
import nl.talsmasoftware.umldoclet.configuration.MethodConfig;
import nl.talsmasoftware.umldoclet.configuration.TypeDisplay;
import nl.talsmasoftware.umldoclet.configuration.Visibility;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static nl.talsmasoftware.umldoclet.configuration.ImageConfig.Format.SVG;
import static nl.talsmasoftware.umldoclet.configuration.Visibility.PACKAGE_PRIVATE;
import static nl.talsmasoftware.umldoclet.configuration.Visibility.PROTECTED;
import static nl.talsmasoftware.umldoclet.configuration.Visibility.PUBLIC;

/// Configuration for the UML doclet.
///
/// This class implements the [Configuration] interface and provides access to all settings
/// used by the doclet, which are populated from the command-line options.
public class DocletConfig implements Configuration {

    private final UMLOptions options;
    private volatile LocalizedReporter reporter;

    String plantumlServerUrl = null;

    /// The name of the delegate doclet to use for the main documentation task.
    ///
    /// Set to `StandardDoclet.class.getName()` by default.
    String delegateDoclet = "jdk.javadoc.doclet.StandardDoclet";

    /// Destination directory where documentation is generated.
    ///
    /// Set by (Standard) doclet option `-d`, default is `""` meaning the current directory.
    String destDirName = "";

    /// Whether or not to render PlantUML `.puml` files.
    ///
    /// Set by option `-createPumlFiles`, default is `false`.
    boolean renderPumlFile = false;

    /// Whether the doclet should run more quite (errors must still be displayed).
    ///
    /// Set by (Standard) doclet option `-quiet`, default is `false`.
    boolean quiet = false;

    /// When not quiet, should the doclet be extra verbose?
    ///
    /// Set by (our own) doclet option `-verbose`, default is `false`.
    boolean verbose = false;

    /// Option to provide explicit encoding for the written PlantUML files.
    ///
    /// Otherwise, the [#htmlCharset()] is used.
    String umlencoding;

    /// Option for Standard doclet's HTML encoding.
    ///
    /// This takes precedence over the `-encoding` setting which is about the source files.
    String docencoding;

    /// Option for Standard doclet's source encoding.
    ///
    /// This is here because the Standard doclet uses this for HTML output if no `-docencoding` is specified.
    String encoding;

    final ImageCfg images = new ImageCfg();
    final FieldCfg fieldConfig = new FieldCfg();
    final MethodCfg methodConfig = new MethodCfg();

    List<String> excludedReferences = new ArrayList<>(asList(
            "java.lang.Object", "java.lang.Enum", "java.lang.annotation.Annotation"));

    List<String> excludedPackageDependencies = new ArrayList<>(asList(
            "java", "javax"));

    boolean failOnCyclicPackageDependencies = false;

    List<ExternalLink> externalLinks = new ArrayList<>();

    List<String> customPlantumlDirectives = new ArrayList<>();

    private final Indentation indentation = Indentation.DEFAULT;

    /// Creates a new doclet configuration.
    public DocletConfig() {
        this.options = new UMLOptions(this);
        this.reporter = new LocalizedReporter(this, null, null);
    }

    /// Initializes the doclet configuration with the given locale and reporter.
    ///
    /// @param locale   The locale to use for localization.
    /// @param reporter The reporter to use for logging.
    public void init(Locale locale, Reporter reporter) {
        this.reporter = new LocalizedReporter(this, reporter, locale);
    }

    /// Merges the UML-specific options with the standard options.
    ///
    /// @param standardOptions The standard options from the standard doclet.
    /// @return The merged set of options.
    public Set<Doclet.Option> mergeOptionsWith(Set<? extends Doclet.Option> standardOptions) {
        return options.mergeWith(standardOptions);
    }

    @Override
    public Optional<String> plantumlServerUrl() {
        return Optional.ofNullable(plantumlServerUrl);
    }

    @Override
    public Optional<String> delegateDocletName() {
        return Optional.ofNullable(delegateDoclet).filter(name -> !"false".equalsIgnoreCase(name));
    }

    @Override
    public Logger logger() {
        return reporter;
    }

    @Override
    public Indentation indentation() {
        return indentation;
    }

    @Override
    public String destinationDirectory() {
        return destDirName;
    }

    @Override
    public boolean renderPumlFile() {
        return renderPumlFile || (!quiet && verbose);
    }

    @Override
    public ImageConfig images() {
        return images;
    }

    @Override
    public FieldConfig fields() {
        return fieldConfig;
    }

    @Override
    public MethodConfig methods() {
        return methodConfig;
    }

    @Override
    public List<String> excludedTypeReferences() {
        return excludedReferences;
    }

    @Override
    public List<String> excludedPackageDependencies() {
        return excludedPackageDependencies;
    }

    @Override
    public boolean failOnCyclicPackageDependencies() {
        return failOnCyclicPackageDependencies;
    }

    @Override
    public Optional<URI> resolveExternalLinkToType(String packageName, String type) {
        return externalLinks.stream()
                .map(link -> link.resolveType(packageName, type))
                .filter(Optional::isPresent).map(Optional::get)
                .findFirst();
    }

    @Override
    public List<String> customPlantumlDirectives() {
        List<String> customDirectives = new ArrayList<>(customPlantumlDirectives.size() + 1);
        if (customPlantumlDirectives.stream().noneMatch(directive -> directive.startsWith("!pragma layout"))) {
            customDirectives.add("!pragma layout smetana");
        }
        customDirectives.addAll(customPlantumlDirectives);
        return customDirectives;
    }

    @Override
    public Charset umlCharset() {
        return umlencoding != null ? Charset.forName(umlencoding)
                : htmlCharset();
    }

    @Override
    public Charset htmlCharset() {
        return docencoding != null ? Charset.forName(docencoding)
                : encoding != null ? Charset.forName(encoding)
                : Charset.defaultCharset();
    }

    private Set<Visibility> parseVisibility(String value) {
        if ("private".equals(value) || "all".equals(value)) return EnumSet.allOf(Visibility.class);
        else if ("package".equals(value)) return EnumSet.of(PACKAGE_PRIVATE, PROTECTED, PUBLIC);
        else if ("protected".equals(value)) return EnumSet.of(PUBLIC, PROTECTED);
        else if ("public".equals(value)) return EnumSet.of(PUBLIC);

        reporter.warn(Message.WARNING_UNKNOWN_VISIBILITY, value);
        return parseVisibility("protected"); // The default for javadoc
    }

    void showMembers(String value) {
        Set<Visibility> visibility = parseVisibility(value);
        fieldConfig.visibilities = visibility;
        methodConfig.visibilities = visibility;
    }

    final class ImageCfg implements ImageConfig {
        String directory = null;
        Collection<Format> formats = null;

        /// Directory where UML images are generated.
        ///
        /// Set by doclet option `-umlImageDirectory`, default is `empty` meaning relative to the generated
        /// documentation itself.
        @Override
        public Optional<String> directory() {
            return Optional.ofNullable(directory);
        }

        void addImageFormat(String imageFormat) {
            if (imageFormat != null) {
                if (formats == null) formats = new LinkedHashSet<>();
                Stream.of(imageFormat.split("[,;]"))
                        .map(String::trim).map(String::toUpperCase)
                        .map(s -> s.replaceFirst("^\\.", ""))
                        .filter(s -> !s.isEmpty() && !"NONE".equals(s))
                        .map(this::parseFormat).filter(Objects::nonNull)
                        .forEach(formats::add);
            }
        }

        private Format parseFormat(String format) {
            try {
                return Format.valueOf(format);
            } catch (IllegalArgumentException unrecognizedFormat) {
                logger().warn(Message.WARNING_UNRECOGNIZED_IMAGE_FORMAT, format);
                return null;
            }
        }

        @Override
        public Collection<Format> formats() {
            return Optional.ofNullable(formats).orElseGet(() -> singleton(SVG));
        }
    }

    static final class FieldCfg implements FieldConfig {

        TypeDisplay typeDisplay = TypeDisplay.SIMPLE;
        Set<Visibility> visibilities = EnumSet.of(PROTECTED, PUBLIC);

        @Override
        public TypeDisplay typeDisplay() {
            return typeDisplay;
        }

        @Override
        public boolean include(Visibility visibility) {
            return visibilities.contains(visibility);
        }
    }

    static final class MethodCfg implements MethodConfig {
        // ParamNames paramNames = ParamNames.BEFORE_TYPE;
        ParamNames paramNames = ParamNames.NONE;
        TypeDisplay paramTypes = TypeDisplay.SIMPLE;
        TypeDisplay returnType = TypeDisplay.SIMPLE;
        Set<Visibility> visibilities = EnumSet.of(PROTECTED, PUBLIC);
        boolean javaBeanPropertiesAsFields = false;

        @Override
        public ParamNames paramNames() {
            return paramNames;
        }

        @Override
        public TypeDisplay paramTypes() {
            return paramTypes;
        }

        @Override
        public TypeDisplay returnType() {
            return returnType;
        }

        @Override
        public boolean include(Visibility visibility) {
            return visibilities.contains(visibility);
        }

        @Override
        public boolean javaBeanPropertiesAsFields() {
            return javaBeanPropertiesAsFields;
        }
    }
}
