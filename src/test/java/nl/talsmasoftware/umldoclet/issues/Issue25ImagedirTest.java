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
package nl.talsmasoftware.umldoclet.issues;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Tests <a href="https://github.com/talsma-ict/umldoclet/issues/25">enhancement 25</a>:
/// Send images to a single directory.
///
/// The maven job is configured so that it creates a directory called `test-content` in the target
/// where images should be located in a single `images` directory.
///
/// @author Sjoerd Talsma
public class Issue25ImagedirTest {

    /// Set-up to create Javadoc and UML Diagrams.
    @BeforeAll
    public static void createJavadoc() {
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", "target/issues/25",
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageDirectory", "images",
                "src/test/java/" + Issue25ImagedirTest.class.getName().replace('.', '/') + ".java"
        );
    }

    /// Default constructor.
    Issue25ImagedirTest() {
        super();
    }

    /// Test that the images directory was created.
    @Test
    public void testImagesDirectoryPresence() {
        File imagesDir = new File("target/issues/25/images");
        assertThat(imagesDir).as("Images directory").exists().isDirectory();
    }

    /// Test that the image was generated in the correct directory.
    @Test
    public void testEnhancement25ImagePresence() {
        File imageFile = new File("target/issues/25/images/" + getClass().getName() + ".svg");
        assertThat(imageFile).as("Image file").exists().isFile();
    }

}
