/*
 * Copyright 2016-2017 Talsma ICT
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
import nl.talsmasoftware.umldoclet.configuration.Configuration;

import javax.lang.model.SourceVersion;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Comparator.comparing;

/**
 * UML doclet that generates <a href="http://plantuml.com">PlantUML</a> class diagrams from your java code just as
 * easily as creating proper JavaDoc comments.<br>
 * It actually extends JavaDoc's {@link StandardDoclet} doclet to generate the 'regular' HTML documentation.
 *
 * @author Sjoerd Talsma
 */
public class UMLDoclet extends StandardDoclet {

    private final Configuration config;

    public UMLDoclet() {
        super();
        this.config = new Configuration(this);
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
        Set<Option> supportedOptions = new TreeSet<>(comparing(o -> o.getNames().get(0), String::compareTo));
        supportedOptions.addAll(super.getSupportedOptions());
        supportedOptions.addAll(config.getSupportedOptions());
        return supportedOptions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    @Override
    public boolean run(DocletEnvironment docEnv) {
        return super.run(docEnv);
    }

}
