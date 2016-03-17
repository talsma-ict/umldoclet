/*
 * Copyright (C) 2016 Talsma ICT
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
 *
 */

package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.testing.PatternMatcher;
import org.apache.maven.shared.invoker.*;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for Issue 13; Doclet seems to suppress JavaDoc errors.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class Issue13Test {
    private static final Logger LOGGER = LoggerFactory.getLogger(Issue13Test.class);

    @Test
    @Ignore // Issue 13 still needs to be fixed! Test passes witout umldoclet and fails with it.
    public void testJavaDocErrors() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("src/test/resources/issue-13/pom.xml"));
        request.setGoals(asList("clean", "verify"));
        request.setDebug(true);
        OutputHandler outputHandler = new OutputHandler();
        request.setOutputHandler(outputHandler);

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);

        // Test for exit code 1.
//        assertThat(result.getExitCode(), is(equalTo(1)));

        // Test for meaningful error in Maven output.
        // [ERROR] Exit code: 1 - /Users/sjoerd/Documents/workspace/talsmasoftware/umldoclet/src/test/resources/issue-13/src/main/java/example/JavadocError.java:6: error: invalid use of @return
        final String output = outputHandler.toString();
        assertThat(output, PatternMatcher.containsPattern("\\[ERROR\\] Exit code\\: 1"));
        assertThat(output, PatternMatcher.containsPattern("JavadocError\\.java\\:6\\: error\\: invalid use of @return"));
    }

    private static class OutputHandler implements InvocationOutputHandler {
        private final StringWriter output = new StringWriter();
        private final PrintWriter writer = new PrintWriter(output);

        @Override
        public void consumeLine(String line) {
            LOGGER.trace("> {}", line);
            writer.println(line);
        }

        @Override
        public String toString() {
            return output.toString();
        }
    }
}
