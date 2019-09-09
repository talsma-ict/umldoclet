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
import net.sourceforge.plantuml.version.Version;
import nl.talsmasoftware.umldoclet.html.HtmlPostprocessor;
import nl.talsmasoftware.umldoclet.javadoc.DocletConfig;
import nl.talsmasoftware.umldoclet.javadoc.UMLFactory;
import nl.talsmasoftware.umldoclet.javadoc.dependencies.DependenciesElementScanner;
import nl.talsmasoftware.umldoclet.javadoc.dependencies.PackageDependency;
import nl.talsmasoftware.umldoclet.javadoc.dependencies.PackageDependencyCycle;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.uml.DependencyDiagram;
import nl.talsmasoftware.umldoclet.uml.Diagram;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static nl.talsmasoftware.umldoclet.logging.Message.DOCLET_COPYRIGHT;
import static nl.talsmasoftware.umldoclet.logging.Message.DOCLET_VERSION;
import static nl.talsmasoftware.umldoclet.logging.Message.ERROR_UNANTICIPATED_ERROR_GENERATING_UML;
import static nl.talsmasoftware.umldoclet.logging.Message.ERROR_UNSUPPORTED_DELEGATE_DOCLET;
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
        this.config = new DocletConfig();
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
    public boolean run(DocletEnvironment docEnv) {
        config.logger().info(DOCLET_COPYRIGHT, DOCLET_VERSION);
        config.logger().info(PLANTUML_COPYRIGHT, Version.versionString());

        // First generate Standard HTML documentation

        String delegateDocletName = config.delegateDocletName().orElse(null);
        if (StandardDoclet.class.getName().equals(delegateDocletName)) {
            if (!super.run(docEnv)) return false;
        } else if (delegateDocletName != null) {
            config.logger().error(ERROR_UNSUPPORTED_DELEGATE_DOCLET, delegateDocletName);
            return false; // TODO for a later release (see e.g. issue #102)
        }

        try {

            generateDiagrams(docEnv).forEach(Diagram::render);
            return new HtmlPostprocessor(config).postProcessHtml();

        } catch (RuntimeException unanticipatedException) {
            config.logger().error(ERROR_UNANTICIPATED_ERROR_GENERATING_UML, unanticipatedException);
            return false;
        }
    }

    private Stream<Diagram> generateDiagrams(DocletEnvironment docEnv) {
        UMLFactory factory = new UMLFactory(config, docEnv);
        return Stream.concat(
                docEnv.getIncludedElements().stream()
                        .map(element -> generateDiagram(factory, element))
                        .filter(Objects::nonNull),
                Stream.of(generatePackageDependencyDiagram(docEnv)));
    }

    private Diagram generateDiagram(UMLFactory factory, Element element) {
        if (element instanceof PackageElement) {
            return factory.createPackageDiagram((PackageElement) element);
        } else if (element instanceof TypeElement && (element.getKind().isClass() || element.getKind().isInterface())) {
            return factory.createClassDiagram((TypeElement) element);
        }
        return null;
    }

    private DependencyDiagram generatePackageDependencyDiagram(DocletEnvironment docEnv) {
        Set<PackageDependency> packageDependencies = scanPackageDependencies(docEnv);
        detectPackageDependencyCycles(packageDependencies);
        DependencyDiagram dependencyDiagram = new DependencyDiagram(config, "package-dependencies.puml");
        packageDependencies.forEach(dep -> dependencyDiagram.addPackageDependency(dep.fromPackage, dep.toPackage));
        return dependencyDiagram;
    }

    private Set<PackageDependency> scanPackageDependencies(DocletEnvironment docEnv) {
        return new DependenciesElementScanner(docEnv, config).scan(docEnv.getIncludedElements(), null);
    }

    private Set<PackageDependencyCycle> detectPackageDependencyCycles(Set<PackageDependency> packageDependencies) {
        Set<PackageDependencyCycle> cycles = PackageDependencyCycle.detectCycles(packageDependencies);
        if (!cycles.isEmpty()) {
            String cyclesString = cycles.stream().map(cycle -> " - " + cycle).collect(joining("\n", "\n", ""));
            if (config.failOnCyclicPackageDependencies()) {
                config.logger().error(Message.WARNING_PACKAGE_DEPENDENCY_CYCLES, cyclesString);
            } else {
                config.logger().warn(Message.WARNING_PACKAGE_DEPENDENCY_CYCLES, cyclesString);
            }
        }
        return cycles;
    }
}
