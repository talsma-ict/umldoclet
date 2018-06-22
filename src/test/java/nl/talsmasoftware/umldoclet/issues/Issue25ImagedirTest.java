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
package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests <a href="https://github.com/talsma-ict/umldoclet/issues/25">enhancement 25</a>:
 * Send images to a single directory.
 * <p>
 * The maven job is configured so that it creates a directory called <code>test-content</code> in the target
 * where images should be located in a single <code>images</code> directory.
 *
 * @author Sjoerd Talsma
 */
public class Issue25ImagedirTest {

    @BeforeClass
    public static void createJavadoc() {
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/test-25",
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageDirectory", "images",
                "src/test/java/" + Issue25ImagedirTest.class.getName().replace('.', '/') + ".java"
        );
    }

    @Test
    public void testImagesDirectoryPresence() {
        File imagesDir = new File("target/test-25/images");
        assertThat("images dir exists", imagesDir.exists(), is(true));
        assertThat("images dir is directory", imagesDir.isDirectory(), is(true));
    }

    @Test
    public void testEnhancement25ImagePresence() {
        File imageFile = new File("target/test-25/images/" + getClass().getName() + ".svg");
        assertThat("image exists", imageFile.exists(), is(true));
        assertThat("image is directory", imageFile.isDirectory(), is(false));
        assertThat("image is file", imageFile.isFile(), is(true));
    }

}
