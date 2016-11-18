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

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Class to capture the indentation as an immutable type containing a pre-filled buffer to quickly be written.
 *
 * @author Sjoerd Talsma
 */
public final class Indentation implements CharSequence, Serializable {

    // Cache of the first 5 four-spaces indentations.
    private static final Indentation[] FOUR_SPACES = {new Indentation(4, ' ', 0), new Indentation(4, ' ', 1),
            new Indentation(4, ' ', 2), new Indentation(4, ' ', 3), new Indentation(4, ' ', 4)};

    // Cache of the first 5 tab indentations.
    private static final Indentation[] TABS = {new Indentation(1, '\t', 0), new Indentation(1, '\t', 1),
            new Indentation(1, '\t', 2), new Indentation(1, '\t', 3), new Indentation(1, '\t', 4)};

    /**
     * The default indentation is four spaces, initially at level 0.
     */
    public static Indentation DEFAULT = FOUR_SPACES[0];

    /**
     * A reusable constant for no indentation at all (even after calls to {@link #increase()}.
     */
    public static Indentation NONE = new Indentation(0, ' ', 0);

    // All fields of Indentation class are final.
    final int width, level;
    final char ch;
    final char[] buf;

    private Indentation(int width, char ch, int level) {
        this.width = width > 0 ? width : 0;
        this.level = level > 0 ? level : 0;
        this.ch = ch;
        this.buf = new char[width * level];
        Arrays.fill(this.buf, ch);
    }

    /**
     * Returns an indentation of <code>level</code> tabs, increasing or decreasing
     * by one tab at a time.
     *
     * @param level The number of tabs for this indentation.
     * @return The indentation of <code>level</code> tabs.
     */
    public static Indentation tabs(final int level) {
        return level < 0 ? TABS[0]
                : level < TABS.length ? TABS[level]
                : new Indentation(1, '\t', level);
    }

    /**
     * Returns an indentation of <code>width</code> spaces, initially indented at
     * <code>width * level</code> spaces.
     * This indentation increases or decreases by <code>width</code> spaces at a time.
     *
     * @param width The number of spaces for a single indentation level (often 2 or 4).
     * @param level The current indentation level (multiply this with the width for the initial number of spaces).
     * @return The indentation level as <code>level</code> multiples of <code>width</code> spaces.
     */
    public static Indentation spaces(final int width, final int level) {
        if (width == 0) return NONE;
        else if (width == 4 && level < FOUR_SPACES.length) {
            return level < 0 ? FOUR_SPACES[0] : FOUR_SPACES[level];
        }
        return new Indentation(width, ' ', level);
    }

    private static Indentation resolve(final int width, final char ch, final int level) {
        return width == 0 ? NONE
                : ch == ' ' ? spaces(width, level)
                : ch == '\t' && width == 1 ? tabs(level)
                : new Indentation(width, ch, level);
    }

    /**
     * @return This indentation with the level increased by one.
     */
    public Indentation increase() {
        return width > 0 ? resolve(width, ch, level + 1) : this;
    }

    /**
     * @return This indentation with the level decreased by one (if there was indentation left to decrease).
     */
    public Indentation decrease() {
        return width > 0 && level > 0 ? resolve(width, ch, level - 1) : this;
    }

    /**
     * Writes this indentation to the given writer object.<br>
     * Please be aware that usually it may prove easier to just create an {@link IndentingWriter} instead which will
     * automatically write the indentation whenever needed (i.e. before the first character on any new line is written).
     *
     * @param writer The writer to write this indentation to.
     * @throws IOException if thrown by the writer while writing the indentation.
     * @see IndentingWriter
     */
    /* package */ void writeTo(Writer writer) throws IOException {
        requireNonNull(writer, "Writer was <null>.").write(buf);
    }

    /**
     * Makes sure that after deserialization, objects from cache are used where possible.
     *
     * @return The deserialized object from the cache if possible.
     */
    private Object readResolve() {
        return resolve(width, ch, level);
    }

    /**
     * @return hashCode of this indentation object.
     */
    public int hashCode() {
        return Objects.hash(width, ch, level);
    }

    /**
     * Whether the other object represents the exact same indentation object.
     *
     * @param other The other indentation object to compare with.
     * @return <code>true</code> if the other object is the same indentation object.
     */
    public boolean equals(Object other) {
        return this == other || (other instanceof Indentation
                && width == ((Indentation) other).width
                && ch == ((Indentation) other).ch
                && level == ((Indentation) other).level
        );
    }

    @Override
    public int length() {
        return buf.length;
    }

    @Override
    public char charAt(int index) {
        return buf[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().substring(start, end);
    }

    /**
     * @return The indentation as a string.
     */
    public String toString() {
        return String.valueOf(buf);
    }
}
