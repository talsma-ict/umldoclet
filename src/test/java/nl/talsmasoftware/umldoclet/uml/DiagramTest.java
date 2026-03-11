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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.indentation.Indentation;
import nl.talsmasoftware.indentation.io.IndentingWriter;
import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;
import nl.talsmasoftware.umldoclet.logging.TestLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static nl.talsmasoftware.umldoclet.configuration.ImageConfig.Format.PNG;
import static nl.talsmasoftware.umldoclet.configuration.ImageConfig.Format.SVG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DiagramTest {
    Configuration config;
    ImageConfig imageconfig;
    TestLogger logger;
    Collection<ImageConfig.Format> formats = new ArrayList<>(singleton(SVG));

    @BeforeEach
    void setUp() {
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
    void tearDown() {
        verify(config, atLeast(0)).plantumlServerUrl();
        verify(config, atLeast(0)).images();
        verify(config, atLeast(0)).destinationDirectory();
        verify(config, atLeast(0)).logger();
        verify(imageconfig, atLeast(0)).formats();
        verify(imageconfig, atLeast(0)).directory();
        verifyNoMoreInteractions(imageconfig, config);
    }

    @Test
    void testDiagramWithoutConfiguration() {
        assertThatThrownBy(() -> new TestDiagram(null, null))
                .isInstanceOf(NullPointerException.class)
                .message().isNotBlank();
    }

    @Test
    void testDiagramToString() {
        assertThat(new TestDiagram(config, new File("target/test-classes/foo/bar.puml")))
                .hasToString("target/test-classes/images/foo.bar.svg");
    }

    @Test
    void testWithoutImageDir() {
        reset(imageconfig);
        when(imageconfig.formats()).thenReturn(formats);
        when(imageconfig.directory()).thenReturn(Optional.empty());
        assertThat(new TestDiagram(config, new File("target/test-classes/foo/bar.puml")))
                .hasToString("target/test-classes/foo/bar.svg");
    }

    @Test
    void testDiagramToStringMultipleFormats() {
        formats.add(PNG);
        assertThat(new TestDiagram(config, new File("target/test-classes/foo/bar.puml")))
                .hasToString("target/test-classes/images/foo.bar.[svg,png]");
    }

    @Test
    void testCustomDirective() {
        // prepare
        when(config.customPlantumlDirectives()).thenReturn(singletonList("skinparam handwritten true"));
        StringWriter output = new StringWriter();
        Diagram testDiagram = new TestDiagram(config, new File("target/test-classes/custom-directive.puml"));
        IndentingWriter writer = new IndentingWriter(output, Indentation.EMPTY);

        // execute
        testDiagram.writeTo(writer);

        // verify
        assertThat(Arrays.asList(output.toString().split("\\n"))).contains("skinparam handwritten true");
        verify(config).customPlantumlDirectives();
    }

    @Test
    void testCustomBackgroundcolor() {
        // prepare
        when(config.customPlantumlDirectives()).thenReturn(singletonList("skinparam backgroundcolor green"));
        StringWriter output = new StringWriter();
        Diagram testDiagram = new TestDiagram(config, new File("target/test-classes/custom-directive.puml"));
        IndentingWriter writer = new IndentingWriter(output, Indentation.EMPTY);

        // execute
        testDiagram.writeTo(writer);

        // verify
        assertThat(Arrays.asList(output.toString().split("\\n"))).contains("skinparam backgroundcolor green");
        verify(config).customPlantumlDirectives();
    }

    static class TestDiagram extends Diagram {
        final File plantumlFile;

        TestDiagram(Configuration config, File plantumlFile) {
            super(config);
            this.plantumlFile = plantumlFile;
        }

        @Override
        protected File getPlantUmlFile() {
            return plantumlFile;
        }
    }
}
