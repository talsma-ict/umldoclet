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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PackagenameValidatorTest {

    PackagenameValidator subject;

    @BeforeEach
    void setUp() {
        subject = new PackagenameValidator();
    }

    @Test
    void unnamed_package_must_be_valid() {
        assertThat(subject.test(""), is(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {".", ".prefixed", "suffixed."})
    void must_not_start_or_end_with_dot(String candidatePackageName) {
        assertThat(subject.test(candidatePackageName), is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"my package", "my-package"})
    void package_may_not_contain_spaces_or_dashes(String candidatePackageName) {
        assertThat(subject.test(candidatePackageName), is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"nl.talsmasoftware.umldoclet", "nl.talsma_software", "no_dot"})
    void valid_package_names_must_pass_validation(String candidatePackageName) {
        assertThat(subject.test(candidatePackageName), is(true));
    }

}
