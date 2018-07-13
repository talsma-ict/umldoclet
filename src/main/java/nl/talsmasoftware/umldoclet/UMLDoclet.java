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
package nl.talsmasoftware.umldoclet;

import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;
import net.sourceforge.plantuml.version.Version;
import nl.talsmasoftware.umldoclet.html.HtmlPostprocessor;
import nl.talsmasoftware.umldoclet.javadoc.DocletConfig;
import nl.talsmasoftware.umldoclet.javadoc.UMLFactory;
import nl.talsmasoftware.umldoclet.uml.UMLDiagram;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static nl.talsmasoftware.umldoclet.logging.Message.DOCLET_COPYRIGHT;
import static nl.talsmasoftware.umldoclet.logging.Message.DOCLET_VERSION;
import static nl.talsmasoftware.umldoclet.logging.Message.ERROR_UNANTICIPATED_ERROR_GENERATING_UML;
import static nl.talsmasoftware.umldoclet.logging.Message.ERROR_UNANTICIPATED_ERROR_POSTPROCESSING_HTML;
import static nl.talsmasoftware.umldoclet.logging.Message.PLANTUML_COPYRIGHT;

/**
 * UML doclet that generates <a href="http://plantuml.com">PlantUML</a> class diagrams from your java code just as
 * easily as creating proper JavaDoc comments.<br>
 * It actually extends JavaDoc's {@link StandardDoclet} doclet to generate the regular HTML documentation.
 *
 * @author Sjoerd Talsma
 */
public class UMLDoclet extends StandardDoclet {

    private final DocletConfig config;

    public UMLDoclet() {
        super();
        this.config = new DocletConfig(this);
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        config.init(locale, reporter);
        super.init(locale, reporter);
    }

    @Override
    public String getName() {
        return "UML";
    }

    @Override
    public Set<Option> getSupportedOptions() {
        return config.mergeOptionsWith(super.getSupportedOptions());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    @Override
    public boolean run(DocletEnvironment docEnv) {
        return super.run(docEnv)
                && generateUMLDiagrams(docEnv)
                && postProcessHtml();
    }

    private boolean generateUMLDiagrams(DocletEnvironment docEnv) {
        try {
            config.logger().info(DOCLET_COPYRIGHT, DOCLET_VERSION);
            config.logger().info(PLANTUML_COPYRIGHT, Version.versionString());

            UMLFactory factory = new UMLFactory(config, docEnv);
            return streamIncludedElements(docEnv.getIncludedElements())
                    .map(element -> mapToDiagram(factory, element))
                    .filter(Optional::isPresent).map(Optional::get)
                    .map(UMLDiagram::render)
                    .reduce(Boolean.TRUE, (a, b) -> a & b);

        } catch (RuntimeException rte) {
            config.logger().error(ERROR_UNANTICIPATED_ERROR_GENERATING_UML, rte);
            rte.printStackTrace(System.err);
            return false;
        }
    }

    private boolean postProcessHtml() {
        try {

            return new HtmlPostprocessor(config).postProcessHtml();

        } catch (IOException | RuntimeException ex) {
            config.logger().error(ERROR_UNANTICIPATED_ERROR_POSTPROCESSING_HTML, ex);
            ex.printStackTrace(System.err);
            return false;
        }
    }

    private Optional<UMLDiagram> mapToDiagram(UMLFactory factory, Element element) {
        if (element instanceof PackageElement) {
            return Optional.of(factory.createPackageDiagram((PackageElement) element));
        } else if (element instanceof TypeElement && (element.getKind().isClass() || element.getKind().isInterface())) {
            return Optional.of(factory.createClassDiagram((TypeElement) element));
        }
        return Optional.empty();
    }

    /**
     * Orders included elements where types are rendered first before packages.
     * This helps detecting broken links to classes that are not included.
     *
     * @param elements The elements to be ordered, types-first
     * @return The ordered elements
     */
    private static Stream<? extends Element> streamIncludedElements(Collection<? extends Element> elements) {
        final List<Element> types = new ArrayList<>();
        final List<Element> other = new ArrayList<>();
        elements.forEach(elem -> (elem instanceof TypeElement ? types : other).add(elem));
        return Stream.concat(types.stream(), other.stream());
    }

}
