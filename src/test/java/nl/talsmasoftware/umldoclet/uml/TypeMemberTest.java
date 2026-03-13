/*
 * Copyright 2016-2026 Talsma ICT
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
package nl.talsmasoftware.umldoclet.uml;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeMemberTest {

    @Test
    public void testMinimalTypeMemberInstance() {
        TypeMember minimalInstance = new MinimalTypeMember("name");
        assertThat(minimalInstance)
                .hasSameHashCodeAs(new MinimalTypeMember("name"))
                .isEqualTo(new MinimalTypeMember("name"));
        // TODO: newline rendering should be in the writeChildren logic, not the child itself
//        assertThat(minimalInstance, hasToString("+name"));
    }

    private static class MinimalTypeMember extends TypeMember {
        private MinimalTypeMember(String name) {
            super(null, name, null);
        }
    }
}
