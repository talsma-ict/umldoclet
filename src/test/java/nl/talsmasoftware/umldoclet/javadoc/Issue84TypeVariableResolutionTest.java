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

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class Issue84TypeVariableResolutionTest {

    @Test
    public void testTypeMemberImplementsComparableTypeMember() throws IOException {
        TestUtil.createDirectory(new File("target/issues/84"));
        String testObjectPath = TestObject.class.getName().replace('.', '/');
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/issues/84",
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
                "src/test/java/" + testObjectPath + ".java"
        );

        assertThat(TestUtil.readUml(Files.newInputStream(Paths.get("target/issues/84/" + testObjectPath + ".puml"))))
                .contains("java.lang.Comparable<TestObject>", "{abstract} +compareTo(TestObject): int");
    }

}
