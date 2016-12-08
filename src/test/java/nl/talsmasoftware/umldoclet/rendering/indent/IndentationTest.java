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

package nl.talsmasoftware.umldoclet.rendering.indent;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Sjoerd Talsma
 */
public class IndentationTest {

    @Test
    public void testDefault() {
        assertThat(Indentation.DEFAULT, hasToString(equalTo("")));
        assertThat(Indentation.DEFAULT.increase(), hasToString(equalTo("    ")));
        assertThat(Indentation.DEFAULT.increase().decrease(), is(sameInstance(Indentation.DEFAULT)));
    }

    @Test
    public void testTabs() {
        assertThat(Indentation.tabs(-1), hasToString(""));
        assertThat(Indentation.tabs(0), hasToString(""));
        assertThat(Indentation.tabs(1), hasToString("\t"));
        assertThat(Indentation.tabs(2), hasToString("\t\t"));
    }

    @Test
    public void testDefaultSpaces() {
        Indentation defaultSpaces = Indentation.spaces(-1, 0);
        assertThat(defaultSpaces, hasToString(""));
        assertThat(defaultSpaces.increase(), hasToString("    ")); // four spaces by default.
        assertThat(defaultSpaces.increase().decrease(), is(sameInstance(defaultSpaces)));
        assertThat(defaultSpaces, is(sameInstance(Indentation.spaces(-1, -1)))); // negative level becomes 0
    }

    @Test
    public void testSpaces_width1() {
        final int width = 1;
        assertThat(Indentation.spaces(width, -1), hasToString(""));
        assertThat(Indentation.spaces(width, 0), hasToString(""));
        assertThat(Indentation.spaces(width, 1), hasToString(" "));
        assertThat(Indentation.spaces(width, -1).increase(), hasToString(" "));
        assertThat(Indentation.spaces(width, 2), hasToString("  "));
    }

    @Test
    public void testSpaces_width2() {
        final int width = 2;
        assertThat(Indentation.spaces(width, -1), hasToString(""));
        assertThat(Indentation.spaces(width, 0), hasToString(""));
        assertThat(Indentation.spaces(width, 1), hasToString("  "));
        assertThat(Indentation.spaces(width, -1).increase(), hasToString("  "));
        assertThat(Indentation.spaces(width, 2), hasToString("    "));
    }

    @Test
    public void testSpaces_width3() {
        final int width = 3;
        assertThat(Indentation.spaces(width, -1), hasToString(""));
        assertThat(Indentation.spaces(width, 0), hasToString(""));
        assertThat(Indentation.spaces(width, 1), hasToString("   "));
        assertThat(Indentation.spaces(width, -1).increase(), hasToString("   "));
        assertThat(Indentation.spaces(width, 2), hasToString("      "));
    }

    @Test
    public void testSpaces_width4() {
        final int width = 4;
        assertThat(Indentation.spaces(width, -1), hasToString(""));
        assertThat(Indentation.spaces(width, 0), hasToString(""));
        assertThat(Indentation.spaces(width, 1), hasToString("    "));
        assertThat(Indentation.spaces(width, -1).increase(), hasToString("    "));
        assertThat(Indentation.spaces(width, 2), hasToString("        "));
    }

}
