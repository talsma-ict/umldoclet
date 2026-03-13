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

import java.io.File;
import java.io.IOException;

import static nl.talsmasoftware.umldoclet.util.FileUtils.relativePath;
import static nl.talsmasoftware.umldoclet.util.TestUtil.assertUnsupportedConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileUtilsTest {

    @Test
    public void testUnsupportedConstructor() {
        assertUnsupportedConstructor(FileUtils.class);
    }

    @Test
    public void testRelativePath() throws IOException {
        final File tempfile = File.createTempFile("dummy", ".tmp");
        try {
            final File tempdir = tempfile.getParentFile();
            final char sep = File.separatorChar;

            // relitive to tempfile means relative to tempdir
            assertThat(relativePath(tempfile, new File(tempdir, "testfile.adoc"))).isEqualTo("testfile.adoc");
            assertThat(relativePath(tempdir, new File(tempdir, "testfile.adoc"))).isEqualTo("testfile.adoc");
            assertThat(relativePath(tempdir, new File(tempdir, "subdir1" + sep + "testfile.adoc")))
                    .isEqualTo("subdir1/testfile.adoc");
            assertThat(relativePath(tempdir, new File(tempdir, "subdir1" + sep + "subdir2" + sep + "testfile.adoc")))
                    .isEqualTo("subdir1/subdir2/testfile.adoc");

        } finally {
            assertThat(tempfile.delete()).isTrue();
        }
    }

    @Test
    public void testRelativePathFromNull() {
        assertThat(relativePath(null, new File("testfile.adoc"))).isNull();
    }

    @Test
    public void testRelativePathToNull() {
        assertThat(relativePath(new File("."), null)).isNull();
    }

    @Test
    public void testCreateParentDirWhenParentIsFile() throws IOException {
        File tempFile = File.createTempFile("umldoclet-", "-test.tmp");
        File subfile = new File(tempFile, "sub-file");
        assertThatThrownBy(() -> FileUtils.ensureParentDir(subfile)).isInstanceOf(IllegalStateException.class);
        tempFile.delete();
    }

    @Test
    public void testWithoutExtension() {
        assertThat(FileUtils.withoutExtension(null)).isNull();
        assertThat(FileUtils.withoutExtension("")).isEmpty();
        assertThat(FileUtils.withoutExtension("foo.bar")).isEqualTo("foo");
        assertThat(FileUtils.withoutExtension("foo/bar.ext")).isEqualTo("foo/bar");
        assertThat(FileUtils.withoutExtension("foo.ext/bar.ext")).isEqualTo("foo.ext/bar");
        assertThat(FileUtils.withoutExtension("Outer.InnerClass.html")).isEqualTo("Outer.InnerClass");
        assertThat(FileUtils.withoutExtension("foo.bar/ext")).isEqualTo("foo.bar/ext");
    }

}
