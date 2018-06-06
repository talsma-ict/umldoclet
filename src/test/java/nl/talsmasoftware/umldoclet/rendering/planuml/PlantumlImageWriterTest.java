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
import java.util.stream.Stream;

import static nl.talsmasoftware.umldoclet.logging.Message.INFO_GENERATING_FILE;
import static nl.talsmasoftware.umldoclet.logging.Message.WARNING_UNRECOGNIZED_IMAGE_FORMAT;
import static nl.talsmasoftware.umldoclet.testing.Testing.read;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Sjoerd Talsma
 */
public class PlantumlImageWriterTest {
    private static String exampleUml = "@startuml\nversion\n@enduml";
    private Logger mockLogger;
    private File tempdir;

    @Before
    public void setUp() {
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
        File puml = new File(tempdir, "version.puml");
        File svg = new File(tempdir, "version.svg");
        try (PlantumlImageWriter writer = PlantumlImageWriter.create(mockLogger, puml, svg)) {
            writer.write(exampleUml);
        }
        assertThat(read(puml), is(exampleUml));

        verify(mockLogger).info(eq(INFO_GENERATING_FILE), eq(svg));
        assertThat(svg + " exists?", svg.isFile(), is(true));
    }

    @Test
    public void testMultipleDiagrams() throws IOException {
        File puml = new File(tempdir, "version.puml");
        File svg = new File(tempdir, "version.svg");
        File png = new File(tempdir, "version.png");

        try (PlantumlImageWriter writer = PlantumlImageWriter.create(mockLogger, puml, svg, png)) {
            writer.write(exampleUml);
        }
        assertThat(read(puml), is(exampleUml));

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
        File puml = new File(tempdir, "version.puml");
        File unknown = new File(tempdir, "diagram.doc");

        try (PlantumlImageWriter writer = PlantumlImageWriter.create(mockLogger, puml, unknown)) {
            writer.write(exampleUml);
            assertThat(writer, hasToString("PlantumlImageWriter[]"));
        }
        assertThat(read(puml), is(exampleUml));

        verify(mockLogger).warn(eq(WARNING_UNRECOGNIZED_IMAGE_FORMAT), eq("diagram.doc"));
    }

    @Test
    public void testNoImages() throws IOException {
        File puml = new File(tempdir, "version.puml");
        try (PlantumlImageWriter writer = PlantumlImageWriter.create(mockLogger, puml)) {
            writer.write(exampleUml);
            assertThat(writer, hasToString("PlantumlImageWriter[]"));
        }
        assertThat(read(puml), is(exampleUml));
    }

    @Test(expected = IllegalStateException.class)
    public void testNoWriteablePlantumlFile() {
        PlantumlImageWriter.create(mockLogger, tempdir);
    }

    @Test
    public void testToString_unrecognizedFormat() throws IOException {
        File puml = new File(tempdir, "diagram.puml");
        try (PlantumlImageWriter writer = PlantumlImageWriter.create(mockLogger, puml, new File(tempdir, "diagram.unrecognized"))) {
            assertThat(writer, hasToString("PlantumlImageWriter[]"));
            verify(mockLogger).warn(WARNING_UNRECOGNIZED_IMAGE_FORMAT, "diagram.unrecognized");
        }
    }

    @Test
    public void testToString_oneDiagram() throws IOException {
        File puml = new File(tempdir, "diagram.puml");
        File svg = new File(tempdir, "diagram.svg");

        try (PlantumlImageWriter writer = PlantumlImageWriter.create(mockLogger, puml, svg)) {
            assertThat(writer, hasToString("PlantumlImageWriter[diagram.svg]"));
        }
        verify(mockLogger).info(INFO_GENERATING_FILE, svg);
    }

    @Test
    public void testToString_twoDiagrams() throws IOException {
        File puml = new File(tempdir, "diagram.puml");
        File svg = new File(tempdir, "diagram.svg");
        File png = new File(tempdir, "diagram.png");

        try (PlantumlImageWriter writer = PlantumlImageWriter.create(mockLogger, puml, png, svg)) {
            assertThat(writer, hasToString("PlantumlImageWriter[diagram.png, diagram.svg]"));
        }
        verify(mockLogger).info(INFO_GENERATING_FILE, png);
        verify(mockLogger).info(INFO_GENERATING_FILE, svg);
    }

}
