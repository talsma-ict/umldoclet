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
package nl.talsmasoftware.umldoclet.html;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.logging.TestLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class HtmlFileTest {

    private static File tempdir;

    @BeforeAll
    public static void createTempdir() throws IOException {
        tempdir = File.createTempFile("HtmlFile", "-test");
        assertThat("Delete tempfile", tempdir.delete(), is(true));
        assertThat("Create temporary directory", tempdir.mkdirs(), is(true));
    }

    @AfterAll
    public static void deleteTempdir() {
        for (File f : tempdir.listFiles()) f.delete();
        assertThat("Delete temporary directory", tempdir.delete(), is(true));
    }

    @Test
    public void testTooShortTempfile() throws IOException {
        Configuration config = mock(Configuration.class);
        Path path = new File(tempdir, "Eq.html").toPath();
        File newTempFile = new HtmlFile(config, path).createNewTempFile();
        assertThat(newTempFile.getName(), startsWith("Eq-")); // Note padding for length < 3
        assertThat(newTempFile.getName(), endsWith(".html"));
    }

    /**
     * In reality all HtmlFile instances should have the {@code .html} extension
     */
    @Test
    public void testTempfileForFileWithoutExtension() throws IOException {
        Configuration config = mock(Configuration.class);
        Path path = new File(tempdir, "X").toPath();
        File newTempFile = new HtmlFile(config, path).createNewTempFile();
        assertThat(newTempFile.getName(), startsWith("X--")); // Padding for length < 3
        assertThat(newTempFile.getName(), endsWith(".tmp")); // Java's default extension for temp files
    }

    @Test
    public void testReplaceBy() throws IOException {
        // prepare
        TestLogger testLogger = new TestLogger();
        Configuration config = mock(Configuration.class);
        Path path = tempdir.toPath().resolve("test.html");
        Path fox = tempdir.toPath().resolve("fox.tmp");
        Files.write(fox, "The quick brown fox jumps over the lazy dog".getBytes(UTF_8));
        when(config.logger()).thenReturn(testLogger);

        // execute
        new HtmlFile(config, path).replaceBy(fox.toFile());

        // verify
        assertThat(Files.readAllLines(path, UTF_8), contains("The quick brown fox jumps over the lazy dog"));
        assertThat(fox.toFile().exists(), is(false));
        verify(config).logger();
        assertThat(testLogger.countMessages(Message.DEBUG_REPLACING_BY::equals), is(1));
        Files.delete(path);
        verifyNoMoreInteractions(config);
    }

    @Test
    public void testReplaceByNull() throws IOException {
        // prepare
        Configuration config = mock(Configuration.class);
        Path path = tempdir.toPath().resolve("test.html");

        // execute
        new HtmlFile(config, path).replaceBy(null);

        // verify
        // should be no-op
        verifyNoMoreInteractions(config);
    }

    @Test
    public void testReplaceByNonFile() throws IOException {
        // prepare
        Configuration config = mock(Configuration.class);
        Path path = tempdir.toPath().resolve("test.html");

        // execute
        new HtmlFile(config, path).replaceBy(new File(tempdir, "does-not-exist"));

        // verify
        // should be no-op
        verifyNoMoreInteractions(config);
    }
}
