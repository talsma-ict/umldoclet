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

import static java.util.Collections.singleton;
import static nl.talsmasoftware.umldoclet.configuration.ImageConfig.Format.SVG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class NamespaceTest {

    private Configuration config;
    private ImageConfig images;

    @BeforeEach
    public void initializeMocks() {
        config = mock(Configuration.class);
        images = mock(ImageConfig.class);
        when(config.images()).thenReturn(images);
        when(images.formats()).thenReturn(singleton(SVG));
    }

    @AfterEach
    public void verifyMocks() {
        verify(config, atLeast(0)).images();
        verify(images, atLeast(0)).formats();
        verifyNoMoreInteractions(config, images);
    }

    @Test
    public void testEquals() {
        PackageDiagram packageUml = new PackageDiagram(config, "a.b.c");
        Namespace namespace = new Namespace(packageUml, "a.b.c");
        assertThat(namespace.equals(namespace), is(true));
        assertThat(namespace, is(equalTo(new Namespace(null, "a.b.c"))));
        assertThat(namespace, is(equalTo(new Namespace(packageUml, "a.b.c"))));
        assertThat(namespace, is(not(equalTo(new Namespace(packageUml, "A.B.C")))));
    }

}
