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
package nl.talsmasoftware.umldoclet.uml;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;
import nl.talsmasoftware.umldoclet.logging.TestLogger;
import nl.talsmasoftware.umldoclet.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Optional;

import static java.util.Collections.singleton;
import static nl.talsmasoftware.umldoclet.configuration.ImageConfig.Format.SVG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;

public class ClassDiagramTest {

    private static final String testdir = "target/test-uml/classdiagram";
    private ImageConfig images;
    private Configuration config;

    @BeforeEach
    public void initializeMocks() {
        images = mock(ImageConfig.class);
        config = mock(Configuration.class);
        when(config.images()).thenReturn(images);
        when(config.destinationDirectory()).thenReturn(testdir);
        when(config.renderPumlFile()).thenReturn(true);
        when(config.logger()).thenReturn(new TestLogger());
        when(config.umlCharset()).thenReturn(Charset.forName("UTF-8"));
        when(images.formats()).thenReturn(singleton(SVG));
        when(images.directory()).thenReturn(Optional.empty());
    }

    @AfterEach
    public void verifyMocks() {
        verify(config, atLeast(1)).images();
        verify(images, atLeast(1)).formats();
    }

    @Test
    public void testClassWithSuperClassInAnotherPackageRelativePath() {
        TestUtil.touch(new File(testdir + "/foo/bar/Bar.html"));
        TestUtil.touch(new File(testdir + "/foo/Foo.html"));
        Type bar = new Type(
                new Namespace(null, "foo.bar", null),
                Type.Classification.CLASS,
                new TypeName("Bar", "foo.bar.Bar"));

        ClassDiagram classDiagram = new ClassDiagram(config, bar);

        // Add Superclass com.foo.Foo
        Type foo = new Type(new Namespace(null, "foo", null),
                Type.Classification.CLASS,
                new TypeName("Foo", "foo.Foo"));
        classDiagram.addChild(foo);
        classDiagram.addChild(new Reference(
                Reference.from("foo.Foo", null),
                "<|--",
                Reference.to("foo.bar.Bar", null)));

        classDiagram.render();
        String uml = TestUtil.read(new File(testdir + "/foo/bar/Bar.puml"));
        assertThat(uml, containsString("foo.bar.Bar [[Bar.html]]"));
        assertThat(uml, containsString("foo.Foo [[../Foo.html]]"));
    }

}
