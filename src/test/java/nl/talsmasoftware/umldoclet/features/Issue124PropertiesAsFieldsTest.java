/*
 * Copyright 2016-2019 Talsma ICT
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
package nl.talsmasoftware.umldoclet.features;

import nl.talsmasoftware.umldoclet.UMLDoclet;
import nl.talsmasoftware.umldoclet.features.beans.StandardBean;
import org.junit.Test;

import java.io.File;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test that properties can be rendered as fields with the option {@code -umlPropertiesAsFields true}.
 */
public class Issue124PropertiesAsFieldsTest {
    private static final File outputdir = new File("target/issues/124");

    @Test
    public void testPropertiesAsFieldsForPublicClass() {
        assertThat("Javadoc result", ToolProvider.findFirst("javadoc").get().run(
                System.out, System.err,
                "-d", outputdir.getPath(),
                "-sourcepath", "src/test/java",
                "-doclet", UMLDoclet.class.getName(),
                "-quiet", "-createPumlFiles",
//                "-umlPropertiesAsFields", "true",
                StandardBean.class.getPackageName()
        ), is(0));

    }


}
