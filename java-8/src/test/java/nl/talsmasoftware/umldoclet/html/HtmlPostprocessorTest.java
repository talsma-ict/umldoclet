/*
 * Copyright 2016-2019 Talsma ICT
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
package nl.talsmasoftware.umldoclet.html;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class HtmlPostprocessorTest {

    private Configuration mockConfiguration;
    private HtmlPostprocessor postprocessor;

    @BeforeEach
    void setUp() {
        mockConfiguration = mock(Configuration.class);
        postprocessor = new HtmlPostprocessor(mockConfiguration);
    }

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(mockConfiguration);
    }

    @Test
    void testCreatePostprocessorWithoutConfiguration() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new HtmlPostprocessor(null));
        assertThat(npe.getMessage(), notNullValue());
    }

    @Test
    void testNonExistingDestinationDirectory() {
        when(mockConfiguration.destinationDirectory()).thenReturn("/tmp/non-existing-directory");
        RuntimeException rte = assertThrows(RuntimeException.class, postprocessor::postProcessHtml);

        assertThat(rte, instanceOf(IllegalStateException.class));
        assertThat(rte.getMessage(), containsStringIgnoringCase("destination directory"));

        verify(mockConfiguration).destinationDirectory();
    }

}
