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
package nl.talsmasoftware.umldoclet.javadoc.dependencies;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Package dependency.
 * <p>
 * Contains a 'from' package and a 'to' package. A (from) package has a dependency on a (to) package
 * if there is at least one element in the 'from' package that needs at least one element in the 'to' package.
 * <p>
 * This class overrides {@code equals} and {@code hashCode} methods so unique package dependencies
 * can easily be included in hashed collections.
 *
 * @author Sjoerd Talsma
 */
public class PackageDependency {

    /**
     * The qualified name of the depending package.
     * This package contains at least one element that has a dependency on an element in the {@link #toPackage}.
     */
    public final String fromPackage;

    /**
     * The qualified name of the depended-upon package.
     * This package contains at least one element that is needed by an element in the {@link #fromPackage}.
     */
    public final String toPackage;

    /**
     * Create a new package dependency object.
     *
     * @param fromPackage The package that has a dependency on another package.
     * @param toPackage   The package that is depended upon.
     */
    public PackageDependency(String fromPackage, String toPackage) {
        this.fromPackage = requireNonNull(fromPackage, "No 'from' package defined for dependency.");
        this.toPackage = requireNonNull(toPackage, "No 'to' package defined for dependency.");
    }

    /**
     * @return Hashcode implementation based on the {@link #fromPackage} and {@link #toPackage} values.
     */
    @Override
    public int hashCode() {
        return Objects.hash(fromPackage, toPackage);
    }

    /**
     * Equal implementation based on the {@link #fromPackage} and {@link #toPackage} values.
     *
     * @param other The other object to compare with.
     * @return {@code true} if the other object is also a {@link PackageDependency} and contains
     * the same {@link #fromPackage} and {@link #toPackage} values, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof PackageDependency
                && fromPackage.equals(((PackageDependency) other).fromPackage)
                && toPackage.equals(((PackageDependency) other).toPackage)
        );
    }

    /**
     * @return Human-readable representation of this package dependency.
     */
    @Override
    public String toString() {
        return fromPackage + "->" + toPackage;
    }
}
