/*
 * Copyright 2016-2025 Talsma ICT
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
import net.sourceforge.plantuml.cli.GlobalConfig;
import net.sourceforge.plantuml.cli.GlobalConfigKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UMLOptionsTest {
    private UMLOptions umlOptions;

    @BeforeEach
    void setUpUmlOptions() {
        umlOptions = new UMLOptions(new DocletConfig());
    }

    private Doclet.Option docletOption(String name) {
        return umlOptions.mergeWith(emptySet())
                .stream()
                .filter(o -> o.getNames().contains(name))
                .findFirst()
                .orElseThrow(() -> new AssertionFailedError("Doclet option " + name + " not found!"));
    }

    @Test
    void testUmlTimeoutOption() {
        // prepare
        Doclet.Option umlTimeoutOption = docletOption("--uml-timeout");

        // execute
        umlTimeoutOption.process("--uml-timeout", singletonList("1800"));

        // verify
        assertThat(GlobalConfig.getInstance().value(GlobalConfigKey.TIMEOUT_MS), is(1000L * 1800));
    }

    @Test
    void testIllegalUmlTimeoutOption() {
        // prepare
        Doclet.Option umlTimeoutOption = docletOption("--uml-timeout");

        // execute
        IllegalArgumentException expected = assertThrows(IllegalArgumentException.class, () ->
                umlTimeoutOption.process("--uml-timeout", singletonList("30 minutes")));

        // verify
        assertThat(expected.getMessage(), containsString("timeout value"));
    }
}
