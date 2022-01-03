/*
 * Copyright 2016-2022 Talsma ICT
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
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Issue70ConfigurableFormatTest {
    private static final String CLASS_AS_PATH = Issue70ConfigurableFormatTest.class.getName().replace('.', '/');
    private static final String SOURCE_FILE = "src/test/java/" + CLASS_AS_PATH + ".java";

    private File outputDir = null;

    @BeforeEach
    public void resetCommonOutputDir() {
        outputDir = new File("target/issues/70");
    }

    private void assertImageExists(String type, Matcher<Boolean> matcher) {
        File image = new File(outputDir, CLASS_AS_PATH + "." + type);
        assertThat(image.getPath() + " exists and is a file?", image.isFile(), matcher);
    }

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

        assertImageExists("svg", is(true));
        assertImageExists("png", is(false));
        assertImageExists("eps", is(false));
        assertImageExists("pdf", is(false));
        assertImageExists("xml", is(false));
        assertImageExists("avi", is(false));
    }

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

        assertImageExists("svg", is(false));
        assertImageExists("png", is(false));
        assertImageExists("eps", is(false));
        assertImageExists("pdf", is(false));
        assertImageExists("xml", is(false));
        assertImageExists("avi", is(false));
    }

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

        assertImageExists("svg", is(true));
        assertImageExists("png", is(false));
        assertImageExists("eps", is(false));
    }

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

        assertImageExists("svg", is(false));
        assertImageExists("png", is(true));
        assertImageExists("eps", is(false));
    }

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

        assertImageExists("svg", is(true));
        assertImageExists("png", is(true));
        assertImageExists("eps", is(true));
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

        assertImageExists("svg", is(false));
        assertImageExists("png", is(false));
        assertImageExists("eps", is(true));
    }

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

        assertImageExists("svg", is(false));
        assertImageExists("png", is(false));
        assertImageExists("eps", is(false));
        assertImageExists("pdf", is(false));
        assertImageExists("xml", is(false));
        assertImageExists("avi", is(false));
    }


}
