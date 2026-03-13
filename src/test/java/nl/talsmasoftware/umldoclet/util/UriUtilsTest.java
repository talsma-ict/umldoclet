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
package nl.talsmasoftware.umldoclet.util;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UriUtilsTest {

    @Test
    public void testUnsupportedConstructor() {
        TestUtil.assertUnsupportedConstructor(UriUtils.class);
    }

    @Test
    public void testAddPathComponentNulls() {
        final URI uri = URI.create("http://www.google.com");
        assertThat(UriUtils.addPathComponent(null, "component")).isNull();
        assertThat(UriUtils.addPathComponent(uri, null)).isSameAs(uri);
    }

    @Test
    public void testAddPathComponent() {
        URI uri = URI.create("http://www.google.com?q=query#fragment");
        String expected = "http://www.google.com";
        String query = "?q=query#fragment";

        uri = UriUtils.addPathComponent(uri, "/component");
        expected += "/component";
        assertThat(uri).isEqualTo(URI.create(expected + query));

        uri = UriUtils.addPathComponent(uri, "endsWithSlash/");
        expected += "/endsWithSlash/";
        assertThat(uri).isEqualTo(URI.create(expected + query));

        uri = UriUtils.addPathComponent(uri, "last");
        expected += "last";
        assertThat(uri).isEqualTo(URI.create(expected + query));
    }

    @Test
    public void testAddPathComponentSpecialCharacters() {
        URI uri = URI.create("http://www.google.com?q=query#fragment");
        String expectedPath = "http://www.google.com";
        String query = "?q=query#fragment";

        uri = UriUtils.addPathComponent(uri, "/with-dashes");
        expectedPath += "/with-dashes";
        assertThat(uri).isEqualTo(URI.create(expectedPath + query));

        uri = UriUtils.addPathComponent(uri, "and spaces/");
        expectedPath += "/and%20spaces/";
        assertThat(uri).isEqualTo(URI.create(expectedPath + query));

        uri = UriUtils.addPathComponent(uri, "or ? question-marks");
        expectedPath += "or%20%3F%20question-marks";
        assertThat(uri).isEqualTo(URI.create(expectedPath + query));
    }

    @Test
    public void testAddPathComponentRelativePathInAbsoluteUri() {
        URI uri = URI.create("http://www.google.com?q=query");
        assertThatThrownBy(() -> UriUtils.addPathComponent(uri, "relative"))
                .isInstanceOf(IllegalArgumentException.class)
                .message().isNotBlank();
    }

    @Test
    public void testAddParamNulls() {
        final URI uri = URI.create("http://www.google.com");
        assertThat(UriUtils.addHttpParam(null, "name", "value")).isNull();
        assertThat(UriUtils.addHttpParam(uri, null, "value")).isEqualTo(URI.create("http://www.google.com"));
        assertThat(UriUtils.addHttpParam(uri, "name", null)).isEqualTo(URI.create("http://www.google.com"));
    }

    @Test
    public void testAddParamRelativeLink() {
        final URI uri = URI.create("../relativepath");
        assertThat(UriUtils.addHttpParam(uri, "name", "value")).isEqualTo(uri);
    }

    @Test
    public void testAddParamFileLink() {
        final URI uri = URI.create("file:/absolutepath");
        assertThat(UriUtils.addHttpParam(uri, "name", "value")).isEqualTo(uri);
    }

    @Test
    public void testAddParamHttpLink() {
        final URI uri = URI.create("http://www.google.com");
        assertThat(UriUtils.addHttpParam(uri, "q", "This is my query"))
                .isEqualTo(URI.create("http://www.google.com?q=This%20is%20my%20query"));
    }

    @Test
    public void testAddParamSecondParameterWithFragment() {
        final URI uri = URI.create("https://www.google.com?q=This%20is%20my%20query#fragment");
        assertThat(UriUtils.addHttpParam(uri, "q", "And this is my second"))
                .isEqualTo(URI.create("https://www.google.com?q=This%20is%20my%20query&q=And%20this%20is%20my%20second#fragment"));
    }

    @Test
    public void testAddParamSpecialQueryCharacters() {
        URI uri = URI.create("http://www.google.com#fragment");
        uri = UriUtils.addHttpParam(uri, "query parameter#", "left = right");
        String expected = "http://www.google.com?query%20parameter%23=left%20%3D%20right";
        assertThat(uri).isEqualTo(URI.create(expected + "#fragment"));

        uri = UriUtils.addHttpParam(uri, "what's the query?", "this & that");
        expected += "&what%27s%20the%20query%3F=this%20%26%20that";
        assertThat(uri).isEqualTo(URI.create(expected + "#fragment"));
    }

}
