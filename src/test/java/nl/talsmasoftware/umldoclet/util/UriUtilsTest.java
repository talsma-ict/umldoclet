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
package nl.talsmasoftware.umldoclet.util;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class UriUtilsTest {

    @Test
    public void testUnsupportedConstructor() {
        Testing.assertUnsupportedConstructor(UriUtils.class);
    }

    @Test
    public void testAddParam_nulls() {
        final URI uri = URI.create("http://www.google.com");
        assertThat(UriUtils.addParam(null, "name", "value"), is(nullValue()));
        assertThat(UriUtils.addParam(uri, null, "value"), is(equalTo(URI.create("http://www.google.com"))));
        assertThat(UriUtils.addParam(uri, "name", null), is(equalTo(URI.create("http://www.google.com"))));
    }

    @Test
    public void testAddParam_relativeLink() {
        final URI uri = URI.create("../relativepath");
        assertThat(UriUtils.addParam(uri, "name", "value"), is(equalTo(uri)));
    }

    @Test
    public void testAddParam_fileLink() {
        final URI uri = URI.create("file:/absolutepath");
        assertThat(UriUtils.addParam(uri, "name", "value"), is(equalTo(uri)));
    }

    @Test
    public void testAddParam_httpLink() {
        final URI uri = URI.create("http://www.google.com");
        assertThat(UriUtils.addParam(uri, "q", "This is my query"),
                is(equalTo(URI.create("http://www.google.com?q=This%20is%20my%20query"))));
    }

}
