/*
 * Copyright 2016-2021 Talsma ICT
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
package nl.talsmasoftware.umldoclet.uml;

import net.sourceforge.plantuml.version.Version;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.logging.TestLogger;
import nl.talsmasoftware.umldoclet.rendering.indent.Indentation;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static nl.talsmasoftware.umldoclet.configuration.ImageConfig.Format.PNG;
import static nl.talsmasoftware.umldoclet.configuration.ImageConfig.Format.SVG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DiagramTest {
    private Configuration config;
    private ImageConfig imageconfig;
    private TestLogger logger;
    private Collection<ImageConfig.Format> formats = new ArrayList<>(singleton(SVG));

    @BeforeEach
    public void setUp() {
        config = mock(Configuration.class);
        imageconfig = mock(ImageConfig.class);
        logger = new TestLogger();

        when(config.images()).thenReturn(imageconfig);
        when(config.destinationDirectory()).thenReturn("target/test-classes");
        when(config.logger()).thenReturn(logger);
        when(imageconfig.formats()).thenReturn(formats);
        when(imageconfig.directory()).thenReturn(Optional.of("images"));
    }

    @AfterEach
    public void tearDown() {
        verify(config, atLeast(0)).images();
        verify(config, atLeast(0)).destinationDirectory();
        verify(config, atLeast(0)).logger();
        verify(imageconfig, atLeast(0)).formats();
        verify(imageconfig, atLeast(0)).directory();
        verifyNoMoreInteractions(imageconfig, config);
    }

    @Test
    public void testDiagramWithoutConfiguration() {
        NullPointerException expected = assertThrows(NullPointerException.class, () ->
                new TestDiagram(null, null));
        assertThat("Expected exception message", expected.getMessage(), notNullValue());
    }

    @Test
    public void testDiagramToString() {
        assertThat(new TestDiagram(config, new File("target/test-classes/foo/bar.puml")),
                hasToString(equalTo("target/test-classes/images/foo.bar.svg")));
    }

    @Test
    public void testWithoutImageDir() {
        reset(imageconfig);
        when(imageconfig.formats()).thenReturn(formats);
        when(imageconfig.directory()).thenReturn(Optional.empty());
        assertThat(new TestDiagram(config, new File("target/test-classes/foo/bar.puml")),
                hasToString(equalTo("target/test-classes/foo/bar.svg")));
    }

    @Test
    public void testDiagramToStringMultipleFormats() {
        formats.add(PNG);
        assertThat(new TestDiagram(config, new File("target/test-classes/foo/bar.puml")),
                hasToString(equalTo("target/test-classes/images/foo.bar.[svg,png]")));
    }

    @Test
    public void testCustomDirective() {
        // prepare
        when(config.customPlantumlDirectives()).thenReturn(singletonList("!pragma graphviz_dot jdot"));
        StringWriter output = new StringWriter();
        Diagram testDiagram = new TestDiagram(config, new File("target/test-classes/custom-directive.puml"));
        IndentingPrintWriter writer = IndentingPrintWriter.wrap(output, Indentation.NONE);
        String footer = logger.localize(Message.DOCLET_UML_FOOTER, Message.DOCLET_VERSION, Version.versionString());

        // execute
        testDiagram.writeTo(writer);

        // verify
        assertThat(asList(output.toString().split("\\n")), contains(
                "@startuml",
                "!pragma graphviz_dot jdot",
                "",
                "center footer " + footer,
                "@enduml"));
        verify(config).customPlantumlDirectives();
    }

    static class TestDiagram extends Diagram {
        private final File plantumlFile;

        private TestDiagram(Configuration config, File plantumlFile) {
            super(config);
            this.plantumlFile = plantumlFile;
        }

        @Override
        protected File getPlantUmlFile() {
            return plantumlFile;
        }
    }
}
