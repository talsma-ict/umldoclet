/*
 * Copyright 2016-2020 Talsma ICT
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
package nl.talsmasoftware.umldoclet.issues;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Issue267Test {

    @Test
    public void testDiagramWithNestedNamespace() throws IOException {
        String uml = TestUtil.readUml(getClass().getResourceAsStream("/issue-267-example.puml"));
        File svgDiagram = new File("target/issues/267/example.svg");
        svgDiagram.getParentFile().mkdirs();
        OutputStream out = new FileOutputStream(svgDiagram);
        new SourceStringReader(uml).outputImage(out, new FileFormatOption(FileFormat.SVG));
    }

}
