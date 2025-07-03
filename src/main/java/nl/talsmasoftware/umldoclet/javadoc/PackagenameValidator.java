/*
 * Copyright 2016-2025 Talsma ICT
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

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Validator for java package names.
 */
public class PackagenameValidator implements Predicate<String> {
    private static final Pattern PACKAGENAME_PATTERN = Pattern.compile("([a-zA-Z_]+(\\.[a-zA-Z_]+)*)?");

    /**
     * Validate a candidate java package name.
     *
     * @param candidatePackageName the candidate package name to be validated.
     * @return Whether the given name is a valid java package name.
     */
    @Override
    public boolean test(String candidatePackageName) {
        return candidatePackageName != null && PACKAGENAME_PATTERN.matcher(candidatePackageName).matches();
    }

}
