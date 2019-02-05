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
package nl.talsmasoftware.umldoclet;

import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.version.Version;
import nl.talsmasoftware.umldoclet.html.HtmlPostprocessor;
import nl.talsmasoftware.umldoclet.javadoc.DocletConfig;
import nl.talsmasoftware.umldoclet.javadoc.UMLFactory;
import nl.talsmasoftware.umldoclet.uml.Diagram;
import nl.talsmasoftware.umldoclet.uml.UMLRoot;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static nl.talsmasoftware.umldoclet.logging.Message.DOCLET_COPYRIGHT;
import static nl.talsmasoftware.umldoclet.logging.Message.DOCLET_VERSION;
import static nl.talsmasoftware.umldoclet.logging.Message.ERROR_UNANTICIPATED_ERROR_GENERATING_DIAGRAMS;
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
        config.logger().info(DOCLET_COPYRIGHT, DOCLET_VERSION);
        config.logger().info(PLANTUML_COPYRIGHT, Version.versionString());

        // First generate Standard HTML documentation
        if (!super.run(docEnv)) return false;

        try {
            Collection<Diagram> umlDiagrams = generateDiagrams(docEnv).collect(toList());

            // TODO: Fix hack by letting diagram 'just render' itself again.
            // Maybe keep the two phases for link validation?
            umlDiagrams.stream().reduce(new LinkedHashMap<String, Diagram>(), (map, diag) -> {
                String name = diag.getPumlFilename();
                if (!map.containsKey(name)) map.put(name, diag);
                return map;
            }, (a, b) -> {
                a.putAll(b);
                return a;
            }).values().forEach(Diagram::renderPlantuml);
            umlDiagrams.forEach(Diagram::render);


            return postProcessHtml(umlDiagrams);
        } catch (UMLDocletException docletException) {
            docletException.logTo(config.logger());
            return false;
        }
    }

    private Stream<Diagram> generateDiagrams(DocletEnvironment docEnv) {
        try {

            UMLFactory factory = new UMLFactory(config, docEnv);
            return docEnv.getIncludedElements().stream()
                    .map(element -> mapToDiagram(factory, element))
                    .filter(Optional::isPresent).map(Optional::get);

        } catch (RuntimeException rte) {
            throw new UMLDocletException(ERROR_UNANTICIPATED_ERROR_GENERATING_UML, rte);
        }
    }

    private boolean postProcessHtml(Collection<Diagram> diagrams) {
        try {

            return new HtmlPostprocessor(config, diagrams).postProcessHtml();

        } catch (IOException | RuntimeException ex) {
            config.logger().error(ERROR_UNANTICIPATED_ERROR_POSTPROCESSING_HTML, ex);
            ex.printStackTrace(System.err);
            return false;
        }
    }

    private Optional<Diagram> mapToDiagram(UMLFactory factory, Element element) {
        if (element instanceof PackageElement) {
            return Optional.of(factory.createPackageDiagram((PackageElement) element))
                    .map(uml -> new Diagram(uml, config.images().formats()));
        } else if (element instanceof TypeElement && (element.getKind().isClass() || element.getKind().isInterface())) {
            return Optional.of(factory.createClassDiagram((TypeElement) element))
                    .map(uml -> new Diagram(uml, config.images().formats()));
        }
        return Optional.empty();
    }

}
