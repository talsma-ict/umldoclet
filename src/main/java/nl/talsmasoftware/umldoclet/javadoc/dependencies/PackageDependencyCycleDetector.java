/*
 * Copyright 2016-2024 Talsma ICT
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

import nl.talsmasoftware.umldoclet.javadoc.DocletConfig;
import nl.talsmasoftware.umldoclet.logging.Message;

import java.util.Set;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

public class PackageDependencyCycleDetector {
    private final DocletConfig config;

    public PackageDependencyCycleDetector(DocletConfig config) {
        this.config = config;
    }

    public Set<PackageDependencyCycle> detectPackageDependencyCycles(Set<PackageDependency> packageDependencies) {
        Set<PackageDependencyCycle> cycles = PackageDependencyCycle.detectCycles(packageDependencies);
        if (!cycles.isEmpty()) {
            String cyclesString = cycles.stream().map(cycle -> " - " + cycle).collect(joining(lineSeparator(), lineSeparator(), ""));
            if (config.failOnCyclicPackageDependencies()) {
                config.logger().error(Message.WARNING_PACKAGE_DEPENDENCY_CYCLES, cyclesString);
            } else {
                config.logger().warn(Message.WARNING_PACKAGE_DEPENDENCY_CYCLES, cyclesString);
            }
        }
        return cycles;
    }
}
