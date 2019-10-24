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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

import static java.util.stream.Collectors.joining;

/**
 * A cycle of dependencies.
 *
 * <p>
 * Package Dependencies can form a cycle if the chain of package dependencies somehow 'return' to the
 * initial package. For example if you have three packages {@code a}, {@code b} and {@code c} and the following
 * dependencies: {@code a -> b}, {@code b -> c}, they will form a cycle if you somehow create a dependency back to
 * {@code a}, e.g. {@code b -> a} or {@code c -> a}.
 */
public class PackageDependencyCycle extends AbstractList<PackageDependency> implements RandomAccess {

    private final PackageDependency[] cycle;

    public PackageDependencyCycle(PackageDependency... dependencies) {
        if (dependencies.length < 1) {
            throw new IllegalArgumentException("A dependency cycle may not be empty.");
        }
        this.cycle = dependencies;
        // check for an actual cycle
        for (int i = 0; i < dependencies.length; i++) {
            String to = dependencies[i].toPackage;
            String from = dependencies[(i + 1) % dependencies.length].fromPackage;
            if (!to.equals(from)) {
                throw new IllegalArgumentException("Not a dependency cycle: '" + to + "' != '" + from + "': " + this);
            }
        }
    }

    public static Set<PackageDependencyCycle> detectCycles(Iterable<PackageDependency> dependencies) {
        List<PackageDependency[]> chains = new LinkedList<>();
        for (PackageDependency dependency : dependencies) {
            List<PackageDependency[]> newChains = new ArrayList<>();
            for (PackageDependency[] chain : chains) {
                if (dependency.fromPackage.equals(last(chain))) {
                    PackageDependency[] longerChain = growChain(chain, dependency);
                    if (longerChain != null) newChains.add(longerChain);
                }
            }
            chains.addAll(newChains);
            chains.add(new PackageDependency[]{dependency});
        }
        Set<PackageDependencyCycle> cycles = new LinkedHashSet<>();
        for (Iterator<PackageDependency[]> it = chains.iterator(); it.hasNext(); it.remove()) {
            PackageDependency[] chain = it.next();
            if (chain.length > 1 && chain[0].fromPackage.equals(last(chain))) {
                cycles.add(new PackageDependencyCycle(chain));
            }
        }
        return cycles;
    }

    private static String last(PackageDependency[] chain) {
        return chain.length == 0 ? null : chain[chain.length - 1].toPackage;
    }

    private static PackageDependency[] growChain(PackageDependency[] chain, PackageDependency dependency) {
        PackageDependency[] longerChain = new PackageDependency[chain.length + 1];
        for (int i = 0; i < chain.length; i++) {
            if (dependency.equals(chain[i])) return null;
            else longerChain[i] = chain[i];
        }
        longerChain[chain.length] = dependency;
        return longerChain;
    }

    @Override
    public PackageDependency get(int index) {
        return cycle[index];
    }

    @Override
    public int size() {
        return cycle.length;
    }

    @Override
    public String toString() {
        return cycle[0].fromPackage + " > " + stream().map(dep -> dep.toPackage).collect(joining(" > "));
    }
}
