/*
 * Copyright 2016-2020 Talsma ICT
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
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
        diagram = new DependencyDiagram(mockConfig, "package-dependencies.puml");
    }

    @AfterEach
    public void verifyMocks() {
        verify(mockConfig, atLeastOnce()).images();
        verify(mockImages, atLeastOnce()).formats();
        verify(mockConfig, atLeast(0)).excludedPackageDependencies();
        verifyNoMoreInteractions(mockConfig, mockImages);
    }

    @Test
    public void testDefaultExcludedPackageDependencies() {
        diagram.addPackageDependency("foo.bar", "java");
        diagram.addPackageDependency("foo.bar", "java.lang");
        diagram.addPackageDependency("foo.bar", "javax");
        diagram.addPackageDependency("foo.bar", "javax.activation");
        diagram.addPackageDependency("foo.bar", "foo.bar.baz");
        assertThat(diagram.getChildren(), hasSize(1));
        assertThat(diagram.getChildren(), contains(hasToString(containsString("foo.bar --> foo.bar.baz"))));
    }

    @Test
    public void testOnlyDefaultExcludedPackageDependencies() {
        diagram.addPackageDependency("foo.bar", "java");
        diagram.addPackageDependency("foo.bar", "java.lang");
        diagram.addPackageDependency("foo.bar", "javax");
        diagram.addPackageDependency("foo.bar", "javax.activation");
        assertThat(diagram.getChildren(), hasSize(4));
    }

    @Test
    public void testExcludedPackageDependenciesFalsePositives() {
        diagram.addPackageDependency("foo.bar", "javas");
        diagram.addPackageDependency("foo.bar", "javas.lang");
        diagram.addPackageDependency("foo.bar", "javaxi");
        diagram.addPackageDependency("foo.bar", "javaxi.activation");
        assertThat(diagram.getChildren(), hasSize(4));
    }

    @Test
    public void testUnnamedPackageIsIncludedByDefault() {
        diagram.addPackageDependency("foo.bar", "");
        assertThat(diagram.getChildren(), hasSize(1));
        String dependency = diagram.getChildren().get(0).toString().trim();
        assertThat(dependency, is(equalTo("foo.bar --> unnamed")));
    }

    @Test
    public void testExcludedPackageDependenciesUnnamed() {
        excluded.add("unnamed");
        diagram.addPackageDependency("foo.bar", "");
        diagram.addPackageDependency("foo.bar", "java.lang");
        diagram.addPackageDependency("foo.bar", "foo.bar.baz");
        assertThat(diagram.getChildren(), hasSize(1));
        assertThat(diagram.getChildren(), contains(hasToString(containsString("foo.bar --> foo.bar.baz"))));
    }
}
