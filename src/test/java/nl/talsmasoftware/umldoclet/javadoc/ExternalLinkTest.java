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
package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.logging.TestLogger;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ExternalLinkTest {
    private Configuration config;
    private TestLogger logger;
    private File tempdir;

    @Before
    public void setup() throws IOException {
        logger = new TestLogger();
        config = mock(Configuration.class);
        tempdir = File.createTempFile("umldoclet-externallink", ".test");
        assertThat("Delete tempfile", tempdir.delete(), is(true));
        assertThat("Create tempdir", tempdir.mkdirs(), is(true));
        when(config.logger()).thenReturn(logger);
    }

    @After
    public void verifyMocks() {
        verify(config, atLeast(0)).logger();
        verifyNoMoreInteractions(config);
    }

    @After
    public void deleteTempdir() {
        TestUtil.deleteRecursive(tempdir);
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
        verify(config, atLeast(1)).destinationDirectory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalUrls() {
        new ExternalLink(config, "https://www.google.com?\nq=query", "");
    }

    @Test
    public void testLiveExternalLink_packageList_badUrl() {
        when(config.destinationDirectory()).thenReturn("");
        TestUtil.write(new File(tempdir, "package-list"), "java.lang\n");
        ExternalLink externalLink = new ExternalLink(config, "https://www.google.com/apidocs", tempdir.getPath());

        Optional<URI> resolved = externalLink.resolveType("java.lang", "Object");
        assertThat(resolved, is(Optional.empty()));
        verify(config, atLeast(1)).destinationDirectory();
    }

    @Test
    public void testLiveExternalLink_elementList_badUrl() {
        when(config.destinationDirectory()).thenReturn("");
        TestUtil.write(new File(tempdir, "element-list"), "module:java.base\njava.lang\n");
        ExternalLink externalLink = new ExternalLink(config, "https://www.google.com/apidocs", tempdir.getPath());

        Optional<URI> resolved = externalLink.resolveType("java.lang", "Object");
        assertThat(resolved, is(Optional.empty()));
        verify(config, atLeast(1)).destinationDirectory();
    }

}
