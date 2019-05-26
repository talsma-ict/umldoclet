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
package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Dependency {

    public final String fromPackage;
    public final String toPackage;

    public Dependency(String fromPackage, String toPackage) {
        this.fromPackage = requireNonNull(fromPackage, "No 'from' package defined for dependency.");
        this.toPackage = requireNonNull(toPackage, "No 'to' package defined for dependency.");
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromPackage, toPackage);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Dependency
                && fromPackage.equals(((Dependency) other).fromPackage)
                && toPackage.equals(((Dependency) other).toPackage)
        );
    }
}
