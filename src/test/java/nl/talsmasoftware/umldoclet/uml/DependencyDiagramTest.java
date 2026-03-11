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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DependencyDiagramTest {
    private ImageConfig mockImages;
    private Configuration mockConfig;
    private DependencyDiagram diagram;
    private List<String> excluded;

    @BeforeEach
    public void prepareMocksDiagramAndExclusions() {
        excluded = new ArrayList<>(asList("java", "javax"));
        mockImages = mock(ImageConfig.class);
        mockConfig = mock(Configuration.class);
        when(mockConfig.images()).thenReturn(mockImages);
        when(mockImages.formats()).thenReturn(singleton(ImageConfig.Format.SVG));
        when(mockConfig.excludedPackageDependencies()).thenReturn(excluded);
        diagram = new DependencyDiagram(mockConfig, null, "package-dependencies.puml");
    }

    @AfterEach
    public void verifyMocks() {
        verify(mockConfig, atLeastOnce()).images();
        verify(mockImages, atLeastOnce()).formats();
        verify(mockConfig, atLeast(0)).plantumlServerUrl();
        verify(mockConfig, atLeast(0)).excludedPackageDependencies();
        verify(mockConfig, atLeast(0)).indentation();
        verifyNoMoreInteractions(mockConfig, mockImages);
    }

    @Test
    public void testDefaultExcludedPackageDependencies() {
        diagram.addPackageDependency("foo.bar", "java");
        diagram.addPackageDependency("foo.bar", "java.lang");
        diagram.addPackageDependency("foo.bar", "javax");
        diagram.addPackageDependency("foo.bar", "javax.activation");
        diagram.addPackageDependency("foo.bar", "foo.bar.baz");
        assertThat(diagram.getChildren())
                .singleElement()
                .extracting(UMLNode::toString, InstanceOfAssertFactories.STRING)
                .contains("foo.bar --> foo.bar.baz");
    }

    @Test
    public void testOnlyDefaultExcludedPackageDependencies() {
        diagram.addPackageDependency("foo.bar", "java");
        diagram.addPackageDependency("foo.bar", "java.lang");
        diagram.addPackageDependency("foo.bar", "javax");
        diagram.addPackageDependency("foo.bar", "javax.activation");
        assertThat(diagram.getChildren()).hasSize(4);
    }

    @Test
    public void testExcludedPackageDependenciesFalsePositives() {
        diagram.addPackageDependency("foo.bar", "javas");
        diagram.addPackageDependency("foo.bar", "javas.lang");
        diagram.addPackageDependency("foo.bar", "javaxi");
        diagram.addPackageDependency("foo.bar", "javaxi.activation");
        assertThat(diagram.getChildren()).hasSize(4);
    }

    @Test
    public void testUnnamedPackageIsIncludedByDefault() {
        diagram.addPackageDependency("foo.bar", "");
        assertThat(diagram.getChildren()).singleElement()
                .extracting(UMLNode::toString, InstanceOfAssertFactories.STRING)
                .contains("foo.bar --> unnamed");
    }

    @Test
    public void testExcludedPackageDependenciesUnnamed() {
        excluded.add("unnamed");
        diagram.addPackageDependency("foo.bar", "");
        diagram.addPackageDependency("foo.bar", "java.lang");
        diagram.addPackageDependency("foo.bar", "foo.bar.baz");
        assertThat(diagram.getChildren()).singleElement()
                .extracting(UMLNode::toString, InstanceOfAssertFactories.STRING)
                .contains("foo.bar --> foo.bar.baz");
    }

    @Test
    public void testEmptyDirectorynameIssue284() throws IOException {
        // prepare
        final File currentDirectory = new File(".").getCanonicalFile();
        final String expectedPath = new File(currentDirectory, "package-dependencies.puml").getPath();
        when(mockConfig.destinationDirectory()).thenReturn("");

        // execute
        final File plantUmlFile = diagram.getPlantUmlFile();

        // verify
        assertThat(plantUmlFile.getAbsolutePath()).isEqualTo(expectedPath);
        verify(mockConfig).destinationDirectory();
    }
}
