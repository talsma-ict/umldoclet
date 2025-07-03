/*
 * Copyright 2016-2025 Talsma ICT
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
package nl.talsmasoftware.umldoclet.uml.plantuml;

import net.sourceforge.plantuml.FileFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;

@Testcontainers
class RemotePlantumlGeneratorTest {
    static final String testUml = "@startuml\r\nBob -> Alice : hello\r\n@enduml";

    @Container
    static final GenericContainer PLANTUML_SERVER = new GenericContainer(DockerImageName.parse("plantuml/plantuml-server"))
            .withExposedPorts(8080);

    PlantumlGenerator subject;

    @BeforeEach
    void setUp() {
        subject = new RemotePlantumlGenerator(String.format("http://%s:%s/",
                PLANTUML_SERVER.getHost(), PLANTUML_SERVER.getMappedPort(8080)));
    }

    @Test
    void nonHttpBaseUrlsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RemotePlantumlGenerator("file:///etc/passwd"));
    }

    @Test
    void simpleDiagramCanBeGenerated() throws IOException {
        // prepare
        final File testDiagram = new File("target/test-classes/"
                + getClass().getPackageName().replace('.', '/')
                + "/testUml.svg");
        testDiagram.delete();

        // execute
        try (OutputStream out = new FileOutputStream(testDiagram)) {
            subject.generatePlantumlDiagramFromSource(testUml, FileFormat.SVG, out);
        }

        // verify
        assertThat(testDiagram.isFile(), is(true));
    }

    @Test
    void exceptionsAreHandled() throws IOException {
        OutputStream mockOutput = Mockito.mock(OutputStream.class);
        IOException ioException = new IOException("Stream already closed!");
        doThrow(ioException).when(mockOutput).write(any(byte[].class), anyInt(), anyInt());

        RuntimeException expected = assertThrows(RuntimeException.class, () ->
                subject.generatePlantumlDiagramFromSource(testUml, FileFormat.SVG, mockOutput));

        assertThat(expected.getCause(), sameInstance(ioException));
    }

}
