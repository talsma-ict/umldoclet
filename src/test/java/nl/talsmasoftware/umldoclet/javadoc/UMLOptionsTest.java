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

import net.sourceforge.plantuml.cli.GlobalConfig;
import net.sourceforge.plantuml.cli.GlobalConfigKey;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.List;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UMLOptionsTest {
    DocletConfig config;
    UMLOptions umlOptions;

    @BeforeEach
    void setUpUmlOptions() {
        config = new DocletConfig();
        umlOptions = new UMLOptions(config);
    }

    boolean processOption(String name, List<String> arguments) {
        return umlOptions.mergeWith(emptySet()).stream()
                .filter(o -> o.getNames().contains(name)).findFirst()
                .orElseThrow(() -> new AssertionFailedError("Doclet option " + name + " not found!"))
                .process(name, arguments);
    }

    @Test
    void testUmlTimeoutOption() {
        // execute
        processOption("--uml-timeout", singletonList("1800"));

        // verify
        assertThat(GlobalConfig.getInstance().value(GlobalConfigKey.TIMEOUT_MS)).isEqualTo(1000L * 1800);
    }

    @Test
    void testIllegalUmlTimeoutOption() {
        List<String> arguments = List.of("30 minutes");
        assertThatThrownBy(() -> processOption("--uml-timeout", arguments))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("timeout value");
    }

    @Test
    void testExcludePackageDependencies() {
        processOption("--uml-exclude-package-dependencies", singletonList("true"));
        assertThat(config.excludePackageDependencies()).isTrue();
    }

    @Test
    void testExcludePackageDiagrams() {
        processOption("--uml-exclude-package-diagrams", singletonList("true"));
        assertThat(config.excludePackageDiagrams()).isTrue();
    }

    @Test
    void testExcludeClassDiagrams() {
        processOption("--uml-exclude-class-diagrams", singletonList("true"));
        assertThat(config.excludeClassDiagrams()).isTrue();
    }

    @Test
    void testFailOnCyclicPackageDependencies() {
        // when
        processOption("--fail-on-cyclic-package-dependencies", singletonList("true"));

        // then
        assertThat(config.onCyclicPackageDependencies()).isEqualTo(Configuration.Action.ERROR);
    }

    @Test
    void testFailOnCyclicPackageDependenciesDoesNotOverrrideConfiguredAction() {
        processOption("--uml-cyclic-package-dependencies", singletonList("warn"));
        processOption("--fail-on-cyclic-package-dependencies", singletonList("true"));

        assertThat(config.onCyclicPackageDependencies()).isEqualTo(Configuration.Action.WARN);
    }

    @Test
    void testUmlCyclicPackageDependenciesOverridesFailConfiguration() {
        processOption("--fail-on-cyclic-package-dependencies", singletonList("false"));
        assertThat(config.onCyclicPackageDependencies()).isEqualTo(Configuration.Action.WARN);
        processOption("--uml-cyclic-package-dependencies", singletonList("ignore"));
        assertThat(config.onCyclicPackageDependencies()).isEqualTo(Configuration.Action.IGNORE);
    }

    @Test
    void testIgnoreCyclicPackageDependencies() {
        processOption("--uml-cyclic-package-dependencies", singletonList("ignore"));
        assertThat(config.onCyclicPackageDependencies()).isEqualTo(Configuration.Action.IGNORE);
    }

    @Test
    void testWarnCyclicPackageDependencies() {
        processOption("--uml-cyclic-package-dependencies", singletonList("warn"));
        assertThat(config.onCyclicPackageDependencies()).isEqualTo(Configuration.Action.WARN);
    }

    @Test
    void testFailCyclicPackageDependencies() {
        processOption("--uml-cyclic-package-dependencies", singletonList("failure"));
        assertThat(config.onCyclicPackageDependencies()).isEqualTo(Configuration.Action.ERROR);
    }
}
