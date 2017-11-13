/*
 * Copyright 2016-2017 Talsma ICT
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
package nl.talsmasoftware.umldoclet.v1;

import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

/**
 * Concatenation of one or more iterables.
 *
 * @author Sjoerd Talsma
 */
public class Concatenation<T> implements Iterable<T> {
    private final Iterable<T>[] iterables;

    @SuppressWarnings("unchecked")
    private Concatenation(Iterable<? extends T>... iterables) {
        this.iterables = (Iterable<T>[]) iterables;
    }

    public static <T> Concatenation<T> of(Iterable<? extends T>... iterables) {
        return new Concatenation<>(iterables);
    }

    public static <U extends T, T> Concatenation<T> append(Iterable<? extends T> iterable, U element) {
        return Concatenation.of(iterable, singleton(element));
    }

    public static <U extends T, T> Concatenation<T> prepend(U headElement, Iterable<? extends T> tail) {
        return Concatenation.of(singleton(headElement), tail);
    }

    @Override
    public Iterator<T> iterator() {
        return new ConcIterator();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + asList(iterables);
    }

    private class ConcIterator implements Iterator<T> {
        int cursor = 0;
        Iterator<T> current;

        private Iterator<T> current() {
            if (current == null) current = requireNonNull(iterables[cursor].iterator(), "Current iterator is <null>.");
            while (!current.hasNext() && cursor < iterables.length - 1) {
                current = requireNonNull(iterables[++cursor].iterator(), "Next iterator is <null>.");
            }
            return current;
        }

        @Override
        public boolean hasNext() {
            return current().hasNext();
        }

        @Override
        public T next() {
            return current().next();
        }

        @Override
        public void remove() {
            current().remove();
        }
    }
}
