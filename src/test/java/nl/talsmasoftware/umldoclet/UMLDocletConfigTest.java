/*
 * Copyright (C) 2016 Talsma ICT
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
 *
 */

package nl.talsmasoftware.umldoclet;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import static nl.talsmasoftware.umldoclet.testing.PatternMatcher.containsPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * This test class verifies that each UMLDocletConfig Setting value has been documented.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class UMLDocletConfigTest {

    @Test
    public void testAvailableDocumentationForAllSettings() {
        for (UMLDocletConfig.Setting setting : UMLDocletConfig.Setting.values()) {
            assertSettingIsDocumented(setting);
        }
    }

    private void assertSettingIsDocumented(UMLDocletConfig.Setting setting) {
        final String optionName = optionName(setting);
        final String optionPattern = "\\|\\s+" + optionName + "\\s+\\|";

        assertThat(usageDocumentation(), containsString(optionName));
        assertThat(usageDocumentation(), containsPattern(optionPattern, Pattern.MULTILINE));
    }

    private static String optionName(UMLDocletConfig.Setting setting) {
        try {
            final Field field = setting.getClass().getDeclaredField("optionName");
            synchronized (field) {
                final boolean accessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    return (String) field.get(setting);
                } finally {
                    field.setAccessible(accessible);
                }
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new AssertionError("Could not read \"optionName\" field from setting " + setting + ".", e);
        }
    }

    private static String _usageDocumentation;

    private static synchronized String usageDocumentation() {
        if (_usageDocumentation == null) {
            final String doc = "USAGE.md";
            try (Reader reader = new InputStreamReader(UMLDocletConfig.class.getResourceAsStream("/" + doc), "UTF-8")) {
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
