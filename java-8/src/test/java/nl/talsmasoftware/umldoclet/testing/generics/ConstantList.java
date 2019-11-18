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
package nl.talsmasoftware.umldoclet.testing.generics;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by sjoerd on 02-03-16.
 */
public class ConstantList<T, X extends CharSequence> extends AbstractList<T> {

    public final List<T> delegate;

    public ConstantList(T... values) {
        if (values == null) {
            delegate = Collections.emptyList();
        } else {
            delegate = Collections.unmodifiableList(Arrays.asList(values));
        }
    }

    public X getX() {
        return null;
    }

    @Override
    public T get(int index) {
        return delegate.get(index);
    }

    protected Collection<T> delegateCollection() {
        return delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
