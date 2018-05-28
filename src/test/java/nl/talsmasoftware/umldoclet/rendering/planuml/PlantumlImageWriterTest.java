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
package nl.talsmasoftware.umldoclet.rendering.planuml;

import nl.talsmasoftware.umldoclet.logging.Logger;
import nl.talsmasoftware.umldoclet.logging.Message;
import nl.talsmasoftware.umldoclet.rendering.plantuml.PlantumlImageWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

import static nl.talsmasoftware.umldoclet.logging.Message.INFO_GENERATING_FILE;
import static nl.talsmasoftware.umldoclet.logging.Message.WARNING_UNRECOGNIZED_IMAGE_FORMAT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * @author Sjoerd Talsma
 */
public class PlantumlImageWriterTest {
    private static String exampleUml = "@startuml\nversion\n@enduml";
    private StringWriter delegate;
    private Logger mockLogger;
    private File tempdir;

    @Before
    public void setUp() {
        delegate = new StringWriter();
        mockLogger = mock(Logger.class);
    }

    @Before
    public void createTempdir() throws IOException {
        tempdir = File.createTempFile("pumlwriter-", ".tmp");
        assertThat("Created temporary directory", tempdir.delete() && tempdir.mkdirs(), is(true));
    }

    @After
    public void cleanupTempdir() {
        Stream.of(tempdir.listFiles()).forEach(f -> assertThat("Delete " + f, f.delete(), is(true)));
        assertThat("Delete " + tempdir, tempdir.delete(), is(true));
    }

    @After
    public void verifyMocks() {
        verify(mockLogger, atLeast(0)).debug(any(Message.class), any());
        verifyNoMoreInteractions(mockLogger);
    }

    @Test
    public void testSimpleDiagram() throws IOException {
        try (PlantumlImageWriter writer = new PlantumlImageWriter(delegate, mockLogger, tempdir, "version", ".svg")) {
            writer.write(exampleUml);
        }
        assertThat(delegate, hasToString(exampleUml));

        File svgFile = new File(tempdir, "version.svg");
        verify(mockLogger).info(eq(INFO_GENERATING_FILE), eq(svgFile));
        assertThat(svgFile + " exists?", svgFile.isFile(), is(true));
    }

    @Test
    public void testMultipleDiagrams() throws IOException {
        try (PlantumlImageWriter writer = new PlantumlImageWriter(delegate, mockLogger, tempdir, "version", ".svg", ".png")) {
            writer.write(exampleUml);
        }
        assertThat(delegate, hasToString(exampleUml));

        for (String extension : new String[]{".svg", ".png"}) {
            File file = new File(tempdir, "version" + extension);
            verify(mockLogger).info(eq(INFO_GENERATING_FILE), eq(file));
            assertThat(file + " exists?", file.exists(), is(true));
            assertThat(file + " is file?", file.isFile(), is(true));
            assertThat(file + " is readable?", file.canRead(), is(true));
            assertThat(file + " size", file.length(), is(greaterThan(0L)));
        }
    }

    @Test
    public void testUnknownImageFormat() throws IOException {
        try (PlantumlImageWriter writer = new PlantumlImageWriter(delegate, mockLogger, null, "diagram", ".doc")) {
            writer.write(exampleUml);
            assertThat(writer, hasToString("PlantumlImageWriter{diagram.[]}"));
        }
        assertThat(delegate, hasToString(exampleUml));

        verify(mockLogger).warn(eq(WARNING_UNRECOGNIZED_IMAGE_FORMAT), eq("doc"));
    }

    @Test
    public void testNullImageFormats() throws IOException {
        try (PlantumlImageWriter writer = new PlantumlImageWriter(delegate, mockLogger, null, "diagram", (String[]) null)) {
            writer.write(exampleUml);
            assertThat(writer, hasToString("PlantumlImageWriter{diagram.[]}"));
        }
        assertThat(delegate, hasToString(exampleUml));

        delegate = new StringWriter();
        try (PlantumlImageWriter writer = new PlantumlImageWriter(delegate, mockLogger, null, "diagram", (String) null)) {
            writer.write(exampleUml);
            assertThat(writer, hasToString("PlantumlImageWriter{diagram.[]}"));
        }
        assertThat(delegate, hasToString(exampleUml));
        verify(mockLogger).warn(eq(WARNING_UNRECOGNIZED_IMAGE_FORMAT), eq(""));
    }

    @Test
    public void testToString() {
        final char sep = File.separatorChar;
        assertThat(new PlantumlImageWriter(delegate, mockLogger, null, "diagram"),
                hasToString("PlantumlImageWriter{diagram.[]}"));
        assertThat(new PlantumlImageWriter(delegate, mockLogger, null, "diagram", (String[]) null),
                hasToString("PlantumlImageWriter{diagram.[]}"));
        assertThat(new PlantumlImageWriter(delegate, mockLogger, new File("directory"), "diagram", "svg"),
                hasToString("PlantumlImageWriter{directory" + sep + "diagram.svg}"));
        assertThat(new PlantumlImageWriter(delegate, mockLogger, new File("directory"), "diagram", ".svg", ".png"),
                hasToString("PlantumlImageWriter{directory" + sep + "diagram.[PNG, SVG]}"));
    }

}
