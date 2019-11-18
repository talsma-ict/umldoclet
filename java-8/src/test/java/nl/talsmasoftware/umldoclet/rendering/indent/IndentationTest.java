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
package nl.talsmasoftware.umldoclet.rendering.indent;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

/**
 * @author Sjoerd Talsma
 */
public class IndentationTest {

    @Test
    public void testDefault() {
        // 4 spaces, initially at level 0:
        assertThat(Indentation.DEFAULT, hasToString(equalTo("")));
        assertThat(Indentation.DEFAULT.increase(), hasToString(equalTo("    ")));
        assertThat(Indentation.DEFAULT.increase().decrease(), is(sameInstance(Indentation.DEFAULT)));
        assertThat(Indentation.DEFAULT.decrease(), is(sameInstance(Indentation.DEFAULT)));
    }

    @Test
    public void testNone() {
        assertThat(Indentation.NONE, hasToString(equalTo("")));
        assertThat(Indentation.NONE.increase(), is(sameInstance(Indentation.NONE)));
        assertThat(Indentation.NONE.decrease(), is(sameInstance(Indentation.NONE)));
    }

    @Test
    public void testTabs() {
        assertThat(Indentation.tabs(-1), is(sameInstance(Indentation.tabs(0))));
        assertThat(Indentation.tabs(0), hasToString(""));
        assertThat(Indentation.tabs(1), hasToString("\t"));
        assertThat(Indentation.tabs(2), hasToString("\t\t"));
        assertThat(Indentation.tabs(6), hasToString("\t\t\t\t\t\t"));
    }

    @Test
    public void testDefaultSpaces() {
        Indentation defaultSpaces = Indentation.spaces(-1, 0);
        assertThat(defaultSpaces, hasToString(""));
        assertThat(defaultSpaces.increase(), hasToString("    ")); // four spaces by default.
        assertThat(defaultSpaces.increase().decrease(), is(sameInstance(defaultSpaces)));
        assertThat(Indentation.spaces(-1, -1), is(sameInstance(defaultSpaces))); // negative level becomes 0
    }

    @Test
    public void testSpacesWidth0() {
        final int width = 0;
        assertThat(Indentation.spaces(width, -1), is(sameInstance(Indentation.spaces(width, 0))));
        assertThat(Indentation.spaces(width, 0), is(sameInstance(Indentation.NONE)));
        assertThat(Indentation.spaces(width, 1), is(sameInstance(Indentation.NONE)));
        assertThat(Indentation.spaces(width, 2), is(sameInstance(Indentation.NONE)));
        assertThat(Indentation.spaces(width, 6), is(sameInstance(Indentation.NONE)));
    }

    @Test
    public void testSpacesWidth1() {
        final int width = 1;
        assertThat(Indentation.spaces(width, -1), is(equalTo(Indentation.spaces(width, 0))));
        assertThat(Indentation.spaces(width, 0), hasToString(""));
        assertThat(Indentation.spaces(width, 1), hasToString(" "));
        assertThat(Indentation.spaces(width, 2), hasToString("  "));
        assertThat(Indentation.spaces(width, 6), hasToString("      "));
    }

    @Test
    public void testSpacesWidth2() {
        final int width = 2;
        assertThat(Indentation.spaces(width, -1), is(sameInstance(Indentation.spaces(width, 0))));
        assertThat(Indentation.spaces(width, 0), hasToString(""));
        assertThat(Indentation.spaces(width, 1), hasToString("  "));
        assertThat(Indentation.spaces(width, 2), hasToString("    "));
        assertThat(Indentation.spaces(width, 6), hasToString("            "));
    }

    @Test
    public void testSpacesWidth3() {
        final int width = 3;
        assertThat(Indentation.spaces(width, -1), is(equalTo(Indentation.spaces(width, 0))));
        assertThat(Indentation.spaces(width, 0), hasToString(""));
        assertThat(Indentation.spaces(width, 1), hasToString("   "));
        assertThat(Indentation.spaces(width, 2), hasToString("      "));
        assertThat(Indentation.spaces(width, 6), hasToString("                  "));
    }

    @Test
    public void testSpacesWidth4() {
        final int width = 4;
        assertThat(Indentation.spaces(width, -1), is(sameInstance(Indentation.spaces(width, 0))));
        assertThat(Indentation.spaces(width, 0), hasToString(""));
        assertThat(Indentation.spaces(width, 1), hasToString("    "));
        assertThat(Indentation.spaces(width, 2), hasToString("        "));
        assertThat(Indentation.spaces(width, 6), hasToString("                        "));
    }

    @Test
    public void testDeserialization() {
        Indentation deserialized = deserialize(serialize(Indentation.DEFAULT));
        assertThat(deserialized, is(sameInstance(Indentation.DEFAULT)));

        deserialized = deserialize(serialize(Indentation.spaces(4, 3)));
        assertThat(deserialized, is(sameInstance(Indentation.spaces(4, 3))));

        deserialized = deserialize(serialize(Indentation.tabs(4)));
        assertThat(deserialized, is(sameInstance(Indentation.tabs(4))));

        deserialized = deserialize(serialize(Indentation.spaces(1, 0)));
        assertThat(deserialized, is(equalTo(Indentation.spaces(1, 0)))); // Not a constant; other instance
    }

    @Test
    public void testHashcode() {
        assertThat(Indentation.DEFAULT.hashCode(), is(Indentation.DEFAULT.hashCode()));
        assertThat(Indentation.spaces(1, 15).hashCode(), is(Indentation.spaces(1, 15).hashCode()));
        assertThat(Indentation.tabs(28).hashCode(), is(Indentation.tabs(28).hashCode()));
    }

    @Test
    public void testLenght() {
        assertThat(Indentation.DEFAULT.length(), is(0));
        assertThat(Indentation.DEFAULT.increase().length(), is(4));
        assertThat(Indentation.DEFAULT.increase().increase().length(), is(8));
        assertThat(Indentation.tabs(5).length(), is(5));
    }

    @Test
    public void testSubsequence() {
        assertThat(Indentation.DEFAULT.increase().increase().subSequence(3, 6), hasToString("   "));
    }

    private static byte[] serialize(Serializable object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(object);
            }
            return bos.toByteArray();
        } catch (IOException ioe) {
            throw new IllegalStateException("Couldn't serialize object: " + ioe.getMessage(), ioe);
        }
    }

    @SuppressWarnings("unchecked")
    private static <S extends Serializable> S deserialize(byte[] bytes) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (S) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Couldn't deserialize object: " + e.getMessage(), e);
        }
    }
}
