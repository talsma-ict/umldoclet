/*
 * Copyright 2016-2018 Talsma ICT
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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.logging.TestLogger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ExternalLinkTest {
    private Configuration config;
    private TestLogger logger;

    @Before
    public void setup() {
        logger = new TestLogger();
        config = mock(Configuration.class);
        when(config.logger()).thenReturn(logger);
    }

    @After
    public void verifyMocks() {
        verify(config, atLeast(0)).logger();
        verifyNoMoreInteractions(config);
    }

    @Test(expected = NullPointerException.class)
    public void testExternalLinkWithoutConfig() {
        new ExternalLink(null, "apidoc", "packageList");
    }

    @Test(expected = NullPointerException.class)
    public void testExternalLinkWithoutApidoc() {
        new ExternalLink(config, null, "packageList");
    }

    @Test(expected = NullPointerException.class)
    public void testExternalLinkWithoutPackageListLocation() {
        new ExternalLink(config, "apidoc", null);
    }

    @Test
    public void testNonExistingUrls() {
        when(config.destinationDirectory()).thenReturn("");
        ExternalLink externalLink = new ExternalLink(config, "doesn't-exist", "doesn't-exist");

        Optional<URI> resolved = externalLink.resolveType("com.my.package", "MyBeautifulClass");
        assertThat(resolved, is(Optional.empty()));

        assertThat(logger.countMessages(Message.WARNING_CANNOT_READ_PACKAGE_LIST::equals), is(1));
        verify(config, times(1)).destinationDirectory();
    }
}
