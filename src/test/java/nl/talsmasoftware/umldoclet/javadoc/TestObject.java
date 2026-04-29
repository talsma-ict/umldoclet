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
package nl.talsmasoftware.umldoclet.javadoc;

import java.util.Objects;

/// Test object for Javadoc and UML Diagrams.
public class TestObject implements Comparable<TestObject> {
    /// Private field.
    private final String value;

    /// Public constructor.
    ///
    /// @param value The value for this test object.
    public TestObject(String value) {
        this.value = value;
    }

    /// Compares this object to the other object.
    ///
    /// @param other The other object to compare with.
    /// @return a negative integer, zero, or a positive integer
    /// as this object is less than, equal to, or greater than the specified object.
    @Override
    public int compareTo(TestObject other) {
        String otherValue = other == null ? null : other.value;
        if (value == null) {
            return otherValue == null ? 0 : -1;
        } else if (otherValue == null) {
            return 1;
        }
        int diff = value.toLowerCase().compareTo(other.value.toLowerCase());
        return diff == 0 ? value.compareTo(other.value) : diff;
    }

    /// Calculate a hash code for the test object.
    ///
    /// @return The hash code for the test object.
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /// Checks whether this object is equal to the other object.
    ///
    /// @param other the other object to compare with.
    /// @return `true` if this object equals the other object, otherwise `false`.
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof TestObject && this.compareTo((TestObject) other) == 0);
    }

    /// String representation for this test object.
    ///
    /// @return the string represtentation for the test object.
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{value=" + value + '}';
    }
}
