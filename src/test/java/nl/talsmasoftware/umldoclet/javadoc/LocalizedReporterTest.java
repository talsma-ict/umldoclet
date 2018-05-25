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
package nl.talsmasoftware.umldoclet.javadoc;

import jdk.javadoc.doclet.Reporter;
import nl.talsmasoftware.umldoclet.UMLDoclet;
import org.junit.After;
import org.junit.Before;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Sjoerd Talsma
 */
public class LocalizedReporterTest {

    private DocletConfig config;
    private Reporter mockReporter;
    private LocalizedReporter localizedReporter;

    @Before
    public void setup() {
        config = new DocletConfig(new UMLDoclet());
        mockReporter = mock(Reporter.class);
    }

    private void init(Locale locale) {
        localizedReporter = new LocalizedReporter(config, mockReporter, locale);
    }

    @After
    public void verifyMockReporter() {
        verifyNoMoreInteractions(mockReporter);
    }


}
