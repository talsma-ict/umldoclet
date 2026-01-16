/*
 * Copyright 2016-2026 Talsma ICT
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.Math.max;

/// Type to capture the indentation as an immutable type containing a pre-filled buffer to quickly be written.
///
/// @author Sjoerd Talsma
public final class Indentation implements CharSequence, Serializable {

    // Cache of the first 5 instances of: 2, 4 spaces + tabs indentations.
    private static final Indentation[] TWO_SPACES = new Indentation[5];
    private static final Indentation[] FOUR_SPACES = new Indentation[5];
    private static final Indentation[] TABS = new Indentation[5];

    static {
        for (int lvl = 0; lvl < TWO_SPACES.length; lvl++) TWO_SPACES[lvl] = new Indentation(2, ' ', lvl);
        for (int lvl = 0; lvl < FOUR_SPACES.length; lvl++) FOUR_SPACES[lvl] = new Indentation(4, ' ', lvl);
        for (int lvl = 0; lvl < TABS.length; lvl++) TABS[lvl] = new Indentation(1, '\t', lvl);
    }

    /// The default indentation is four spaces, initially at level 0.
    public static final Indentation DEFAULT = FOUR_SPACES[0];

    /// A reusable constant for no indentation at all (even after calls to [#increase()]).
    public static final Indentation NONE = new Indentation(0, ' ', 0);

    // All fields of Indentation class are final.
    private final int width;
    private final int level;
    private final char ch;
    private final transient String value;

    private Indentation(final int width, final char ch, final int level) {
        this.width = max(width, 0);
        this.level = max(level, 0);
        this.ch = ch;
        char[] buf = new char[this.width * this.level];
        Arrays.fill(buf, this.ch);
        this.value = String.valueOf(buf);
    }

    /// Returns an indentation of `level` tabs, increasing or decreasing
    /// by one tab at a time.
    ///
    /// @param level The number of tabs for this indentation.
    /// @return The indentation of `level` tabs.
    public static Indentation tabs(final int level) {
        return level < TABS.length ? TABS[max(level, 0)] : new Indentation(1, '\t', level);
    }

    /// Returns an indentation of `width` spaces, initially indented at
    /// `width * level` spaces.
    /// This indentation increases or decreases by `width` spaces at a time.
    ///
    /// @param width The number of spaces for a single indentation level (often 2 or 4).
    /// @param level The current indentation level (multiply this with the width for the initial number of spaces).
    /// @return The indentation level as `level` multiples of `width` spaces.
    public static Indentation spaces(int width, int level) {
        return width < 0 ? spaces(DEFAULT.ch == ' ' ? DEFAULT.width : 4, level)
                : width == 0 ? NONE
                : width == 2 && level < TWO_SPACES.length ? TWO_SPACES[max(level, 0)]
                : width == 4 && level < FOUR_SPACES.length ? FOUR_SPACES[max(level, 0)]
                : new Indentation(width, ' ', level);
    }

    /// Internal 'factory' method that tries to resolve a constant indentation instance before returning a new object.
    ///
    /// @param width The indentation width for one indentation unit
    /// @param ch    The character used in the indentation
    /// @param level The numer of logical indentations to apply
    /// @return the requested indentation either as a resolved constant instance or a new object
    private static Indentation resolve(final int width, final char ch, final int level) {
        return width == 0 ? NONE
                : ch == ' ' ? spaces(width, level)
                : ch == '\t' && width == 1 ? tabs(level)
                : new Indentation(width, ch, level);
    }

    /// @return An indentation instance with the level increased by one.
    public Indentation increase() {
        return resolve(width, ch, level + 1);
    }

    /// @return An indentation instance with the level decreased by one (if there was indentation left to decrease).
    public Indentation decrease() {
        return level == 0 ? this : resolve(width, ch, level - 1);
    }

    /// Makes sure that after deserialization, the constant instances are resolved where possible.
    ///
    /// @return The deserialized object from the cache if possible or a new instance otherwise.
    private Object readResolve() {
        return resolve(width, ch, level);
    }

    /// @return The hash code of this indentation object.
    public int hashCode() {
        return Objects.hash(width, ch, level);
    }

    /// Compares this indentation with another object for equality.
    ///
    /// @param other The other object to compare with.
    /// @return `true` if the other object is an [Indentation] with the same width, character, and level.
    public boolean equals(Object other) {
        return this == other || (other instanceof Indentation
                && width == ((Indentation) other).width
                && ch == ((Indentation) other).ch
                && level == ((Indentation) other).level
        );
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.substring(start, end);
    }

    /// @return The indentation as a string.
    public String toString() {
        return value;
    }

}
