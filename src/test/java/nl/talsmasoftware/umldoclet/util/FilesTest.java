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

import java.io.File;
import java.io.IOException;

import static nl.talsmasoftware.umldoclet.util.Files.relativePath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class FilesTest {

    @Test
    public void testUnsupportedConstructor() {
        Testing.assertUnsupportedConstructor(Files.class);
    }

    @Test
    public void testRelativePath() throws IOException {
        final File tempfile = File.createTempFile("dummy", ".tmp");
        try {
            final File tempdir = tempfile.getParentFile();
            final char sep = File.separatorChar;

            // relatief aan tempfile == relatief aan tempdir
            assertThat(relativePath(tempfile,
                    new File(tempdir, "testfile.adoc")),
                    is("testfile.adoc"));
            assertThat(relativePath(tempdir,
                    new File(tempdir, "testfile.adoc")),
                    is("testfile.adoc"));
            assertThat(relativePath(tempdir,
                    new File(tempdir, "subdir1" + sep + "testfile.adoc")),
                    is("subdir1/testfile.adoc"));
            assertThat(relativePath(tempdir,
                    new File(tempdir, "subdir1" + sep + "subdir2" + sep + "testfile.adoc")),
                    is("subdir1/subdir2/testfile.adoc"));

        } finally {
            assertThat("Tempfile opruimen", tempfile.delete(), is(true));
        }
    }

    @Test
    public void testRelativePath_fromNull() {
        assertThat(relativePath(null, new File("testfile.adoc")), is(nullValue()));
    }

    @Test
    public void testRelativePath_toNull() {
        assertThat(relativePath(new File("."), null), is(nullValue()));
    }
}
