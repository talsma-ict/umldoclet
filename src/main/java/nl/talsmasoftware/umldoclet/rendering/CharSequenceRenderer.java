/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.umldoclet.rendering;

import java.io.IOException;
import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * Simple renderer for fixed content of type {@link CharSequence}.
 *
 * @author Sjoerd Talsma
 */
public final class CharSequenceRenderer implements CharSequence, UMLPart, Serializable {
    public static final UMLPart NEWLINE = CharSequenceRenderer.of(System.lineSeparator());

    private final CharSequence content;

    private CharSequenceRenderer(CharSequence content) {
        this.content = requireNonNull(content, "Content is <null>.");
    }

    public static CharSequenceRenderer of(CharSequence content) {
        return new CharSequenceRenderer(content);
    }

    @Override
    public int length() {
        return content.length();
    }

    @Override
    public char charAt(int index) {
        return content.charAt(index);
    }

    @Override
    public CharSequenceRenderer subSequence(int start, int end) {
        return start == 0 && end == length() ? this : new CharSequenceRenderer(content.subSequence(start, end));
    }

    @Override
    public <A extends Appendable> A writeTo(A output) {
        try {
            output.append(content, 0, content.length());
            return output;
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O exception writing character sequence \""
                    + this + "\": " + ioe.getMessage(), ioe);
        }
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof CharSequence && content.toString().equals(other.toString()));
    }

    @Override
    public String toString() {
        return content.toString();
    }

}
