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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class HtmlFileTest {

    static File tempdir;

    @BeforeAll
    static void createTempdir() throws IOException {
        tempdir = Files.createTempDirectory("HtmlFile-test").toFile();
        assertThat(tempdir).as("Temporary directory").isDirectory();
    }

    @AfterAll
    static void deleteTempdir() {
        for (File f : tempdir.listFiles()) f.delete();
        assertThat(tempdir.delete()).as("Delete result").isTrue();
    }

    @Test
    void testTooShortTempfile() throws IOException {
        Configuration config = mock(Configuration.class);
        Path path = new File(tempdir, "Eq.html").toPath();
        File newTempFile = new HtmlFile(config, path).createNewTempFile();
        assertThat(newTempFile.getName())
                .startsWith("Eq-") // Note padding for length < 3
                .endsWith(".html");
    }

    /// In reality all HtmlFile instances should have the `.html` extension
    @Test
    void testTempfileForFileWithoutExtension() throws IOException {
        Configuration config = mock(Configuration.class);
        Path path = new File(tempdir, "X").toPath();
        File newTempFile = new HtmlFile(config, path).createNewTempFile();
        assertThat(newTempFile.getName())
                .startsWith("X--") // Padding for length < 3
                .endsWith(".tmp"); // Java's default extension for temp files
    }

    @Test
    void testReplaceBy() throws IOException {
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
        assertThat(Files.readAllLines(path, UTF_8)).contains("The quick brown fox jumps over the lazy dog");
        assertThat(fox.toFile()).doesNotExist();
        verify(config).logger();
        assertThat(testLogger.countMessages(Message.DEBUG_REPLACING_BY::equals)).isOne();
        Files.delete(path);
        verifyNoMoreInteractions(config);
    }

    @Test
    void testReplaceByNull() throws IOException {
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
    void testReplaceByNonFile() throws IOException {
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
