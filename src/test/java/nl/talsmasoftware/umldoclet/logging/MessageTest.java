/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.umldoclet.logging;

import org.junit.Test;

import java.util.Locale;

import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class MessageTest {
    private static final Locale DUTCH = new Locale("nl", "NL");

    @Test
    public void testAllMessageAvailability() {
        stream(Message.values()).forEach(key -> assertThat(key.name(), key, hasToString(notNullValue())));
    }

    @Test
    public void testAllMessagesInDutch() {
        stream(Message.values()).forEach(key -> assertThat("Dutch " + key.name(), key.toString(DUTCH), is(notNullValue())));
    }

}
