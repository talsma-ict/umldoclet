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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

/// Test of [issue 70, configurable image formats](https://github.com/talsma-ict/umldoclet/issues/70).
public class Issue70ConfigurableFormatTest {
    /// This test class as path with slashes.
    private static final String CLASS_AS_PATH = Issue70ConfigurableFormatTest.class.getName().replace('.', '/');
    /// The source file.
    private static final String SOURCE_FILE = "src/test/java/" + CLASS_AS_PATH + ".java";

    /// The directory to write output to.
    private File outputDir = null;

    /// Set-up to reset the common output directory.
    @BeforeEach
    public void resetCommonOutputDir() {
        outputDir = new File("target/issues/70");
    }

    /// Assertion that the specified image type exists or not.
    ///
    /// @param type     The image type to check.
    /// @param expected Whether we expect the image type to exist or not.
    private void assertImageExists(String type, boolean expected) {
        File image = new File(outputDir, CLASS_AS_PATH + "." + type);
        if (expected) {
            assertThat(image).exists().isFile();
        } else {
            assertThat(image).doesNotExist();
        }
    }

    /// Test the default image formats.
    @Test
    public void testImageFormatDefaults() {
        outputDir = new File(outputDir, "defaults");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-quiet",
                SOURCE_FILE
        );

        assertImageExists("svg", true);
        assertImageExists("png", false);
        assertImageExists("eps", false);
        assertImageExists("pdf", false);
        assertImageExists("xml", false);
        assertImageExists("avi", false);
    }

    /// Test `-umlImageFormat none` setting.
    @Test
    public void testImageFormatNone() {
        outputDir = new File(outputDir, "none");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageFormat", "none",
                "-quiet",
                SOURCE_FILE
        );

        assertImageExists("svg", false);
        assertImageExists("png", false);
        assertImageExists("eps", false);
        assertImageExists("pdf", false);
        assertImageExists("xml", false);
        assertImageExists("avi", false);
    }

    /// Test `-umlImageFormat svg` setting.
    @Test
    public void testImageFormatSvg() {
        outputDir = new File(outputDir, "svg");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageFormat", "svg",
                "-quiet",
                SOURCE_FILE
        );

        assertImageExists("svg", true);
        assertImageExists("png", false);
        assertImageExists("eps", false);
    }

    /// Test `-umlImageFormat png` setting.
    @Test
    public void testImageFormatPng() {
        outputDir = new File(outputDir, "png");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageFormat", "png",
                "-quiet",
                SOURCE_FILE
        );

        assertImageExists("svg", false);
        assertImageExists("png", true);
        assertImageExists("eps", false);
    }

    /// Test `-umlImageFormat` with `svg`, `png` and `eps` values.
    @Test
    public void testImageFormatSvgPngEps() {
        outputDir = new File(outputDir, "svg_png_eps");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageFormat", "svg",
                "-umlImageFormat", "png",
                "-umlImageFormat", "eps",
                "-quiet",
                SOURCE_FILE
        );

        assertImageExists("svg", true);
        assertImageExists("png", true);
        assertImageExists("eps", true);
    }

    @Test
    public void testImageFormatEps() {
        outputDir = new File(outputDir, "eps");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageFormat", "eps",
                "-quiet",
                SOURCE_FILE
        );

        assertImageExists("svg", false);
        assertImageExists("png", false);
        assertImageExists("eps", true);
    }

    /// Test unrecognized image format setting.
    @Test
    public void testImageFormatUnrecognized() {
        outputDir = new File(outputDir, "unrecognized");
        ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputDir.getPath(),
                "-doclet", UMLDoclet.class.getName(),
                "-umlImageFormat", "unrecognized",
                "-quiet",
                SOURCE_FILE
        );

        assertImageExists("svg", false);
        assertImageExists("png", false);
        assertImageExists("eps", false);
        assertImageExists("pdf", false);
        assertImageExists("xml", false);
        assertImageExists("avi", false);
    }

}
