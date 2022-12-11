/*
 * Copyright 2016-2022 Talsma ICT
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@Testcontainers
class RemotePlantumlGeneratorTest {
    static final String testUml = "@startuml\r\nBob -> Alice : hello\r\n@enduml";

    @Container
    GenericContainer plantumlServer = new GenericContainer(DockerImageName.parse("plantuml/plantuml-server"))
            .withExposedPorts(8080);

    PlantumlGenerator subject;

    @BeforeEach
    void setUp() {
        subject = new RemotePlantumlGenerator(String.format("https://%s:%s/plantuml/",
                plantumlServer.getHost(), plantumlServer.getMappedPort(8080)));
    }

    @Test
    void simpleDiagramCanBeGenerated() throws IOException {
        // prepare
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // execute
        subject.generatePlantumlDiagramFromSource(testUml, FileFormat.SVG, bos);

        // verify
        final byte[] bytes = bos.toByteArray();
        assertThat("Diagram size", bytes.length, greaterThan(0));
    }

}
