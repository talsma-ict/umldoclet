/*
 * Copyright 2016-2018 Talsma ICT
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
import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;
import nl.talsmasoftware.umldoclet.uml.Visibility;
import nl.talsmasoftware.umldoclet.uml.configuration.Configuration;
import nl.talsmasoftware.umldoclet.uml.configuration.FieldConfig;
import nl.talsmasoftware.umldoclet.uml.configuration.ImageConfig;
import nl.talsmasoftware.umldoclet.uml.configuration.MethodConfig;
import nl.talsmasoftware.umldoclet.uml.configuration.TypeDisplay;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

public class DocletConfig implements Configuration {

    private final Doclet doclet;
    private final UMLOptions options;
    private volatile LocalizedReporter reporter;

    /**
     * Destination directory where documentation is generated.
     * <p>
     * Set by (Standard) doclet option {@code -d}, default is {@code ""} meaning the current directory.
     */
    String destDirName = "";

    /**
     * Whether the doclet should run more quite (errors must still be displayed).
     * <p>
     * Set by (Standard) doclet option {@code -quiet}, default is {@code false}.
     */
    boolean quiet = false;

    /**
     * When not quiet, should the doclet be extra verbose?
     * <p>
     * Set by (our own) doclet option {@code -verbose}, default is {@code false}.
     */
    boolean verbose = false;

    /**
     * Option to provide explicit encoding for the written PlantUML files.
     * <p>
     * Otherwise, the {@link #htmlCharset()} is used.
     */
    String umlencoding;

    /**
     * Option for Standard doclet's HTML encoding.
     * <p>
     * This takes precedence over the {@code -encoding} setting which is about the source files.
     */
    String docencoding;

    /**
     * Option for Standard doclet's source encoding.
     * <p>
     * This is here because the Standard doclet uses this for HTML output if no {@code -docencoding} is specified.
     */
    String encoding;

    final ImageCfg images = new ImageCfg();
    final FieldCfg fieldConfig = new FieldCfg();
    final MethodCfg methodConfig = new MethodCfg();

    List<String> excludedReferences = new ArrayList<>(asList(
            "java.lang.Object", "java.lang.Enum", "java.lang.annotation.Annotation"));

    // TODO decide whether we want to make this configurable at all.
    private Indentation indentation = Indentation.DEFAULT;

    public DocletConfig(UMLDoclet doclet) {
        this.doclet = requireNonNull(doclet, "UML Doclet is <null>.");
        this.options = new UMLOptions(this);
        this.reporter = new LocalizedReporter(this, null, null);
    }

    public void init(Locale locale, Reporter reporter) {
        this.reporter = new LocalizedReporter(this, reporter, locale);
    }

    public Set<Doclet.Option> mergeOptionsWith(Set<Doclet.Option> standardOptions) {
        return options.mergeWith(standardOptions);
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

    static final class ImageCfg implements ImageConfig {
        String directory = null;
        Collection<String> imageFormats = null;

        /**
         * Directory where UML images are generated.
         * <p>
         * Set by doclet option {@code -umlImageDirectory}, default is {@code empty} meaning relative to the generated
         * documentation itself.
         */
        @Override
        public Optional<String> directory() {
            return Optional.ofNullable(directory);
        }

        void addImageFormat(String imageFormat) {
            if (imageFormat != null) {
                if (imageFormats == null) imageFormats = new LinkedHashSet<>();
                Stream.of(imageFormat.split(",;"))
                        .map(String::trim)
                        .map(s -> s.replaceFirst("^\\.", ""))
                        .map(String::toUpperCase)
                        .filter(s -> !s.isEmpty())
                        .forEach(imageFormats::add);
            }
        }

        @Override
        public Collection<String> formats() {
            return Optional.ofNullable(imageFormats).orElseGet(() -> singleton("SVG"));
        }
    }

    static final class FieldCfg implements FieldConfig {

        TypeDisplay typeDisplay = TypeDisplay.SIMPLE;
        Set<Visibility> visibilities = EnumSet.of(Visibility.PROTECTED, Visibility.PUBLIC);

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
        Set<Visibility> visibilities = EnumSet.of(Visibility.PROTECTED, Visibility.PUBLIC);

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
    }
}
