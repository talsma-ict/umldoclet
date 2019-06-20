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

import java.util.Collection;
import java.util.List;

/**
 * A cycle of dependencies.
 *
 * <p>
 * Package Dependencies can form a cycle if the chain of package dependencies somehow 'return' to the
 * initial package. For example if you have three packages {@code a}, {@code b} and {@code c} and the following
 * dependencies: {@code a --> b}, {@code b --> c}, they will form a cycle if you somehow create a dependency back to
 * {@code a}, e.g. {@code b -> a} or {@code c --> a}.
 */
public class DependencyCycle {

    public static Collection<DependencyCycle> detect(List<Dependency> dependencies) {
        return null;
    }

}
