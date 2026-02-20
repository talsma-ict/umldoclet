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
package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.logging.TestLogger;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ExternalLinkTest {
    private Configuration config;
    private TestLogger logger;
    private File tempdir;

    @BeforeEach
    public void setup() throws IOException {
        logger = new TestLogger();
        config = mock(Configuration.class);
        tempdir = Files.createTempDirectory("umldoclet-externallink-test").toFile();
        assertThat("Create tempdir", tempdir.isDirectory(), is(true));
        when(config.logger()).thenReturn(logger);
    }

    @AfterEach
    public void verifyMocks() {
        verify(config, atLeast(0)).logger();
        verifyNoMoreInteractions(config);
    }

    @AfterEach
    public void deleteTempdir() {
        TestUtil.deleteRecursive(tempdir);
    }

    @Test
    public void testExternalLinkWithoutConfig() {
        NullPointerException expected = assertThrows(NullPointerException.class, () ->
                new ExternalLink(null, "apidoc", "packageList"));
        assertThat(expected.getMessage(), notNullValue());
    }

    @Test
    public void testExternalLinkWithoutApidoc() {
        NullPointerException expected = assertThrows(NullPointerException.class, () ->
                new ExternalLink(config, null, "packageList"));
        assertThat(expected.getMessage(), notNullValue());
    }

    @Test
    public void testExternalLinkWithoutPackageListLocation() {
        NullPointerException expected = assertThrows(NullPointerException.class, () ->
                new ExternalLink(config, "apidoc", null));
        assertThat(expected.getMessage(), notNullValue());
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

    @Test
    public void testIllegalUrls() {
        IllegalArgumentException expected = assertThrows(IllegalArgumentException.class, () ->
                new ExternalLink(config, "https://www.google.com?\nq=query", ""));
        assertThat(expected.getMessage(), notNullValue());
    }

    @Test
    @Disabled // No live package validation at the moment (see issue #227)
    public void testLiveExternalLink_packageList_badUrl() {
        when(config.destinationDirectory()).thenReturn("");
        TestUtil.write(new File(tempdir, "package-list"), "java.lang\n");
        ExternalLink externalLink = new ExternalLink(config, "https://www.google.com/apidocs", tempdir.getPath());

        Optional<URI> resolved = externalLink.resolveType("java.lang", "Object");
        assertThat(resolved, is(Optional.empty()));
        verify(config, atLeast(1)).destinationDirectory();
    }

    @Test
    @Disabled // No live package validation at the moment (see issue #227)
    public void testLiveExternalLink_elementList_badUrl() {
        when(config.destinationDirectory()).thenReturn("");
        TestUtil.write(new File(tempdir, "element-list"), "module:java.base\njava.lang\n");
        ExternalLink externalLink = new ExternalLink(config, "https://www.google.com/apidocs", tempdir.getPath());

        Optional<URI> resolved = externalLink.resolveType("java.lang", "Object");
        assertThat(resolved, is(Optional.empty()));
        verify(config, atLeast(1)).destinationDirectory();
    }

    @Test
    public void testFileURIs() throws IOException {
        File file = new File(tempdir, "dummy.xyz");
        new FileOutputStream(file).close();
        URI toUri = file.toURI();
        URI uriFromPath = URI.create(file.getPath());

        assertThat(toUri.isAbsolute(), is(true));
        assertThat(toUri.getScheme(), is("file"));
        assertThat(new File(toUri).isFile(), is(true));

        assertThat(uriFromPath.isAbsolute(), is(false));
        assertThat(uriFromPath.getScheme(), is(nullValue()));
        assertThat(new File(uriFromPath.toString()).isFile(), is(true));
    }

}
