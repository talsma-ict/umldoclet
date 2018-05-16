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
import nl.talsmasoftware.umldoclet.javadoc.DocletConfig;
import nl.talsmasoftware.umldoclet.javadoc.UMLFactory;
import nl.talsmasoftware.umldoclet.uml.UMLDiagram;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static nl.talsmasoftware.umldoclet.logging.Message.*;

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
        try {
            config.getLogger().info(DOCLET_COPYRIGHT, DOCLET_VERSION);
            config.getLogger().info(PLANTUML_COPYRIGHT, Version.versionString());

            return generateUMLDiagrams(docEnv) && super.run(docEnv);

        } catch (RuntimeException rte) {
            config.getLogger().error(ERROR_UNANTICIPATED_ERROR_GENERATING_UML, rte);
            rte.printStackTrace(System.err);
            return false;
        }
    }

    private boolean generateUMLDiagrams(DocletEnvironment docEnv) {
        UMLFactory factory = new UMLFactory(config, docEnv);
        return docEnv.getIncludedElements().stream()
                .map(element -> mapToDiagram(factory, element))
                .filter(Optional::isPresent).map(Optional::get)
                .map(UMLDiagram::render)
                .reduce(Boolean.TRUE, (a, b) -> a & b);
    }

    private Optional<UMLDiagram> mapToDiagram(UMLFactory factory, Element element) {
        if (element instanceof PackageElement) {
            return Optional.of(factory.createPackageDiagram((PackageElement) element));
        } else if (element instanceof TypeElement && (element.getKind().isClass() || element.getKind().isInterface())) {
            return Optional.of(factory.createClassDiagram((TypeElement) element));
        }
        return Optional.empty();
    }

}
