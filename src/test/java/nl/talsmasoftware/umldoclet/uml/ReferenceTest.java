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

import static nl.talsmasoftware.umldoclet.uml.Reference.from;
import static nl.talsmasoftware.umldoclet.uml.Reference.to;
import static org.assertj.core.api.Assertions.assertThat;

/// @author Sjoerd Talsma
public class ReferenceTest {

    @Test
    public void testSelfReference() {
        Reference ref = new Reference(from(getClass().getName(), null), "-->", to(getClass().getName(), null));
        assertThat(ref.isSelfReference()).isTrue();
    }

    @Test
    public void testCanonical() {
        Reference ref1 = new Reference(from("type1", null), "-->", to("type2", "*"));
        Reference ref2 = new Reference(from("type2", "*"), "<--", to("type1", null));
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref2).isEqualTo(ref1);
        assertThat(ref1.toString()).contains("type1 --> \"*\" type2");
        assertThat(ref2.toString()).contains("type2 \"*\" <-- type1");
        assertThat(ref2.canonical()).hasToString(ref1.toString());
    }

}
