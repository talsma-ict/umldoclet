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

package nl.talsmasoftware.umldoclet.testing.legacy;

import nl.talsmasoftware.umldoclet.testing.Testing;
import org.junit.BeforeClass;
import org.junit.Test;

import static nl.talsmasoftware.umldoclet.testing.PatternMatcher.containsPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit test for generated UML diagrams based on 'legacy' JavaDoc tags.
 *
 * @assoc * needs 1 SetTopController
 * @navassoc 1 tests - SetTopController
 */
public class LegacyTest {
    private static final String channelIteratorName = ChannelIterator.class.getName();
    private static final String setTopControllerName = SetTopController.class.getName();
    private static final String embeddedAgentName = EmbeddedAgent.class.getName();
    private static final String urlStreamHandlerName = URLStreamHandler.class.getName();
    private static final String autoCloseableName = AutoCloseable.class.getName();
    private static final String legacytestName = LegacyTest.class.getName();
    private static final String agentName = "com.unavailable.api.Agent";
    private static final String controllerName = "com.unavailable.api.Controller";
    private static final String powermanagerName = "nl.talsmasoftware.umldoclet.testing.legacy.PowerManager";
    private static final String targetName = "nl.talsmasoftware.umldoclet.testing.legacy.Target";

    private static String packageUml;

    @BeforeClass
    public static void readUml() {
        packageUml = Testing.readFile("testing/legacy/package.puml");
        assertThat("Package UML", packageUml, is(not(nullValue())));
    }

    @Test
    public void testDependTag() {
        assertThat(packageUml, containsString(
                setTopControllerName + " <.. " + channelIteratorName + ": friend"));
    }

    @Test
    public void testImplementsTag() {
        assertThat(packageUml, containsString(
                autoCloseableName + " <|.. " + embeddedAgentName));
        assertThat(packageUml, containsString(
                agentName + " <|.. " + embeddedAgentName));
        assertThat(packageUml, containsString(
                urlStreamHandlerName + " <|.. " + setTopControllerName));
    }

    @Test
    public void testExtendsTag() {
        assertThat(packageUml, containsString(
                controllerName + " <|-- " + setTopControllerName));
        assertThat(packageUml, containsString(
                embeddedAgentName + " <|-- " + setTopControllerName));
    }

    @Test
    public void testNavassocTag() {
        assertThat(packageUml, containsString(
                powermanagerName + " \"1..*\" <-- " + setTopControllerName));
        assertThat(packageUml, containsString(
                targetName + " \"1\" <-- \"*\" " + setTopControllerName + ": has"));
        assertThat(packageUml, containsString(
                setTopControllerName + " <-- \"1\" " + legacytestName + ": tests"));
    }

    @Test
    public void testAssocTag() {
        assertThat(packageUml, containsString(
                setTopControllerName + " \"1\" -- \"*\" " + legacytestName + ": needs"));
    }

    @Test
    public void testNoteTag() {
        assertThat(packageUml, containsPattern(
                "note bottom of " + setTopControllerName + "\\\n" +
                        "\\s+this is a note\\\n" +
                        "\\s+over multiple lines <i>and <b>containing</b> markup</i>\\\n" +
                        "\\s*end note"));
    }

}
