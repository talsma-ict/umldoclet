/*
 * Copyright 2016-2022 Talsma ICT
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
package nl.talsmasoftware.umldoclet.issues.bug146;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

class PackageProtectedSuperclass extends AbstractList<String> {
    private final List<String> delegate = new ArrayList<>();

    @Override
    public String get(int index) {
        return delegate.get(index);
    }

    @Override
    public String set(int index, String value) {
        return delegate.set(index, value);
    }

    @Override
    public void add(int index, String value) {
        delegate.add(index, value);
    }

    @Override
    public String remove(int index) {
        return delegate.remove(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }
}
