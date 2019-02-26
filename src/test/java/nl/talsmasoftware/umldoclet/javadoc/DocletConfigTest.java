/*
 * Copyright 2016-2019 Talsma ICT
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
import nl.talsmasoftware.umldoclet.uml.Visibility;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * @author Sjoerd Talsma
 */
public class DocletConfigTest {
    private static final String UTF_8 = "UTF-8";

    DocletConfig config;

    @Before
    public void setup() {
        config = new DocletConfig(new UMLDoclet());
    }


    @Test
    public void testUndocumentedOptions() throws UnsupportedEncodingException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(bytes, true, UTF_8)) {
            ToolProvider.findFirst("javadoc").get().run(
                    out, out, "-doclet", UMLDoclet.class.getName(), "--help"
            );
        }

        assertThat(new String(bytes.toByteArray(), UTF_8), not(containsString("<MISSING KEY>")));
    }

    private void assertMemberVisibility(Visibility visibility, boolean expected) {
        assertThat(config.fieldConfig.visibilities.contains(visibility), is(expected));
        assertThat(config.methodConfig.visibilities.contains(visibility), is(expected));
    }

    @Test
    public void testShowMembers_public() {
        config.showMembers("public");
        assertMemberVisibility(Visibility.PRIVATE, false);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, false);
        assertMemberVisibility(Visibility.PROTECTED, false);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembers_protected() {
        config.showMembers("protected");
        assertMemberVisibility(Visibility.PRIVATE, false);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, false);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembers_package() {
        config.showMembers("package");
        assertMemberVisibility(Visibility.PRIVATE, false);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, true);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembers_private() {
        config.showMembers("private");
        assertMemberVisibility(Visibility.PRIVATE, true);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, true);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }

    @Test
    public void testShowMembers_unknown() {
        // Unknown setting defaults to the Javadoc standard 'protected'
        config.showMembers("unknown");
        assertMemberVisibility(Visibility.PRIVATE, false);
        assertMemberVisibility(Visibility.PACKAGE_PRIVATE, false);
        assertMemberVisibility(Visibility.PROTECTED, true);
        assertMemberVisibility(Visibility.PUBLIC, true);
    }
}
