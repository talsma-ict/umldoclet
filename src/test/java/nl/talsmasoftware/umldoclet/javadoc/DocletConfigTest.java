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
package nl.talsmasoftware.umldoclet.javadoc;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.configuration.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Tests the Doclet Config parsing.
 *
 * @author Sjoerd Talsma
 */
public class DocletConfigTest {
    private static final String UTF_8 = "UTF-8";

    DocletConfig config;

    @BeforeEach
    public void createDocletConfig() {
        config = new DocletConfig();
    }

    private String getDocletHelpOutput() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (PrintStream out = new PrintStream(bytes, true, UTF_8)) {
                ToolProvider.findFirst("javadoc").get().run(
                        out, out, "-doclet", UMLDoclet.class.getName(), "--help"
                );
            }
            return new String(bytes.toByteArray(), UTF_8);
        } catch (IOException ioe) {
            throw new AssertionError("Could not get doclet help", ioe);
        }
    }

    /**
     * Tests whether there were any undocumented options added to the doclet.
     *
     * <p>
     * Please add documentation for the new option(s)
     * in the {@code nl.talsmasoftware.umldoclet.UMLDoclet} resource bundle.
     */
    @Test
    public void testForUndocumentedMissingKeys() {
        assertThat(getDocletHelpOutput(), not(containsString("<MISSING KEY>")));
    }

    private void assertMemberVisibility(Visibility visibility, boolean expected) {
        assertThat(config.fieldConfig.visibilities.contains(visibility), is(expected));
        assertThat(config.methodConfig.visibilities.contains(visibility), is(expected));
    }

    @Test
    public void testShowMembersPublic() {
        config.showMembers("public");
        assertMemberVisibility(Visibility.PRIVATE, false);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, false);
        assertMemberVisibility(Visibility.PROTECTED, false);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembersProtected() {
        config.showMembers("protected");
        assertMemberVisibility(Visibility.PRIVATE, false);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, false);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembersPackage() {
        config.showMembers("package");
        assertMemberVisibility(Visibility.PRIVATE, false);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, true);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembersPrivate() {
        config.showMembers("private");
        assertMemberVisibility(Visibility.PRIVATE, true);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, true);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembersAll() {
        config.showMembers("all");
        assertMemberVisibility(Visibility.PRIVATE, true);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, true);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembersUnknown() {
        // Unknown setting defaults to the Javadoc standard 'protected'
        config.showMembers("unknown");
        assertMemberVisibility(Visibility.PRIVATE, false);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, false);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testOptionDocExcludedPackageDependencies() {
        String help = getDocletHelpOutput();
        assertThat(help, containsString("-umlExcludedPackageDependencies <package>(,<package>)*"));
        assertThat(help, containsString("Defaults to 'java,javax'"));
    }
}
