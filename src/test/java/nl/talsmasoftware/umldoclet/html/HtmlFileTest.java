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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.mock;

public class HtmlFileTest {

    private static File tempdir;

    @BeforeClass
    public static void createTempdir() throws IOException {
        tempdir = File.createTempFile("HtmlFile", "-test");
        assertThat("Delete tempfile", tempdir.delete(), is(true));
        assertThat("Create temporary directory", tempdir.mkdirs(), is(true));
    }

    @AfterClass
    public static void deleteTempdir() throws IOException {
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
}
