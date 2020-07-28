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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.fail;

public class Issue267Test {

    @Test
    public void testDiagramWithNestedNamespace() throws IOException {
        // prepare
        String uml = TestUtil.readUml(getClass().getResourceAsStream("/issue-267-example.puml"));
        File svgDiagram = new File("target/issues/267/example.svg");
        svgDiagram.getParentFile().mkdirs();
        OutputStream out = new FileOutputStream(svgDiagram);

        // execute
        String output = interceptSystemOut(() -> {
            try {
                new SourceStringReader(uml).outputImage(out, new FileFormatOption(FileFormat.SVG));
            } catch (IOException ioe) {
                fail("I/O error generating image " + svgDiagram, ioe);
            }
        }).toLowerCase();

        // verify
        assertThat(svgDiagram.isFile(), is(true));
        assertThat(output, not(containsString("error")));
        assertThat(output, not(containsString("exception")));
        String svgcontent = TestUtil.read(svgDiagram).toLowerCase();
        assertThat(svgcontent, not(containsString("error")));
        assertThat(svgcontent, not(containsString("exception")));
    }

    private static synchronized String interceptSystemOut(Runnable runnable) {
        final String charset = StandardCharsets.UTF_8.name();
        try {
            PrintStream currentSystemOut = System.out;
            PrintStream currentSystemErr = System.err;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                PrintStream newOut = new PrintStream(baos, true, charset);
                System.setOut(newOut);
                System.setErr(newOut);
                runnable.run();
                newOut.flush();
            } finally {
                System.setOut(currentSystemOut);
                System.setErr(currentSystemErr);
            }
            return baos.toString(charset);
        } catch (UnsupportedEncodingException unsuppored) {
            throw new IllegalStateException("Platform doesn't know " + charset + "!", unsuppored);
        }
    }

}
