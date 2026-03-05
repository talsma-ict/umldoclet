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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LinkTest {
    Configuration configMock;
    Diagram diagramMock;

    @BeforeEach
    void setUp() {
        configMock = mock(Configuration.class);
        diagramMock = mock(Diagram.class);
        when(diagramMock.getConfiguration()).thenReturn(configMock);
    }

    @BeforeEach
    @AfterEach
    void clearBasepath() {
        Link.linkFrom(null);
    }

    @Test
    void forType_withoutBasepath() {
        Type type = classType("nl.talsmasoftware.umldoclet.uml.Testing");

        Link subject = Link.forType(type);

        assertThat(subject, hasToString(""));
    }

    @Test
    void forType_withBasepath() {
        final String url = "https://www.javadoc.io/static/nl.talsmasoftware/umldoclet/latest/nl/talsmasoftware/umldoclet/uml/Testing.html";
        Type type = classType("nl.talsmasoftware.umldoclet.uml.Testing");
        Link.linkFrom(new File(".").getPath());
        when(configMock.resolveExternalLinkToType("nl.talsmasoftware.umldoclet.uml", "Testing"))
                .thenReturn(Optional.of(URI.create(url)));

        Link subject = Link.forType(type);

        assertThat(subject, hasToString("[[" + url + "]]"));
    }

    Type classType(String qualifiedName) {
        String packagename = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        String className = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        return new Type(
                new Namespace(diagramMock, packagename, null),
                Type.Classification.CLASS,
                new TypeName(packagename, className, qualifiedName));
    }
}
