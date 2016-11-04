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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration Test for Issue 13; Doclet seems to suppress JavaDoc errors.
 *
 * @author Sjoerd Talsma
 */
@Ignore
public class Issue13IT {
    private static final Logger LOGGER = LoggerFactory.getLogger(Issue13IT.class);

    private Properties mavenProps;

    @Before
    public void setUp() throws IOException {
        mavenProps = new Properties();
        try (InputStream in = Issue13IT.class.getResourceAsStream("/META-INF/umldoclet.properties")) {
            mavenProps.load(in);
        }
        String jarfile = "target/umldoclet-" + mavenProps.getProperty("version") + ".jar";
        mavenProps.setProperty("docletPath", new File(jarfile).getCanonicalPath());
        LOGGER.debug("Set docletPath maven property to \"{}\".", mavenProps.getProperty("docletPath"));
    }

    @Test
    public void testJavaDocErrors() throws MavenInvocationException, IOException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("src/test/resources/issue-13/pom.xml"));
        request.setGoals(asList("clean", "verify"));
        request.setProperties(mavenProps);
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
