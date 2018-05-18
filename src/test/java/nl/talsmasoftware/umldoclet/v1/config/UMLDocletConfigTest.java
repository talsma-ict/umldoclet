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
package nl.talsmasoftware.umldoclet.v1.config;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Tag;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static nl.talsmasoftware.umldoclet.testing.PatternMatcher.containsPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test class verifies that each UMLDocletConfig Setting value has been documented
 * in the USAGE.md file.
 *
 * @author Sjoerd Talsma
 */
@Ignore // Tests the old doclet
public class UMLDocletConfigTest {

    @Test
    public void testAvailableDocumentationForAllSettings() {
        for (UMLDocletConfig.Setting setting : UMLDocletConfig.Setting.values()) {
            assertSettingIsDocumented(setting);
        }
    }

    @Test
    public void testSplitDefaultValue() {
        UMLDocletConfig config = new UMLDocletConfig(new String[0][], mock(DocErrorReporter.class));
        assertThat(config.excludedReferences(), is(equalTo((Collection<String>) asList(
                Object.class.getName(), Enum.class.getName(), Annotation.class.getName()
        ))));
    }

    @Test
    public void testSplitMultipleOccurrances() {
        UMLDocletConfig config = new UMLDocletConfig(new String[][]{
                {"-umlExcludedReferences", Object.class.getName()},
                {"-umlExcludedReferences", Enum.class.getName()}
        }, mock(DocErrorReporter.class));

        assertThat(config.excludedReferences(), is(equalTo((Collection<String>) asList(
                Object.class.getName(), Enum.class.getName()
        ))));
    }

    @Test
    public void testIssue37_packagePrivateClassesDefaultValue() {
        UMLDocletConfig config = new UMLDocletConfig(new String[0][], mock(DocErrorReporter.class));
        ClassDoc packagePrivateClassDoc = mock(ClassDoc.class);
        when(packagePrivateClassDoc.isPackagePrivate()).thenReturn(true);
        when(packagePrivateClassDoc.tags(anyString())).thenReturn(new Tag[0]);
        when(packagePrivateClassDoc.annotations()).thenReturn(new AnnotationDesc[0]);

        assertThat(config.includeClass(packagePrivateClassDoc), is(false));
    }

    private void assertSettingIsDocumented(UMLDocletConfig.Setting setting) {
        final String optionName = optionName(setting);
        final String optionPattern = "^\\|\\s+" + optionName + "\\s+";

        assertThat(usageDocumentation(), containsString(optionName));
        assertThat(usageDocumentation(), containsPattern(optionPattern, Pattern.MULTILINE));
    }

    private static String optionName(UMLDocletConfig.Setting setting) {
        try {
            final Field delegateField = UMLDocletConfig.Setting.class.getDeclaredField("delegate");
            final Field nameField = AbstractSetting.class.getDeclaredField("name");
            try {
                delegateField.setAccessible(true);
                try {
                    nameField.setAccessible(true);
                    return "-" + nameField.get(delegateField.get(setting));
                } finally {
                    nameField.setAccessible(false);
                }
            } finally {
                delegateField.setAccessible(false);
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new AssertionError("Could not read \"optionName\" field from setting " + setting + ".", e);
        }
    }

    private static String _usageDocumentation;

    private static synchronized String usageDocumentation() {
        if (_usageDocumentation == null) {
            final String doc = "docs/USAGE.md";
            try (Reader reader = new InputStreamReader(new FileInputStream(doc), "UTF-8")) {
                StringWriter writer = new StringWriter();
                char[] buf = new char[1024];
                for (int read = reader.read(buf); read >= 0; read = reader.read(buf)) {
                    writer.write(buf, 0, read);
                }
                _usageDocumentation = writer.toString();
            } catch (IOException | RuntimeException e) {
                throw new AssertionError("Could not read \"" + doc + "\" documentation file.", e);
            }
        }
        return _usageDocumentation;
    }

}
