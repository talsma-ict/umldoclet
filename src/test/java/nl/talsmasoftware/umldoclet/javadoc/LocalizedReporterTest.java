/*
 * Copyright 2016-2025 Talsma ICT
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

import com.sun.source.util.DocTreePath;
import jdk.javadoc.doclet.Reporter;
import nl.talsmasoftware.umldoclet.logging.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Sjoerd Talsma
 */
public class LocalizedReporterTest {
    private static final Locale NL = new Locale("nl", "NL");
    private static Locale defaultLocale;

    private DocletConfig config;
    private Reporter mockReporter;
    private LocalizedReporter localizedReporter;

    @BeforeAll
    public static void presetDefaultLocale() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @AfterAll
    public static void restoreDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    @BeforeEach
    public void setup() {
        config = new DocletConfig();
        mockReporter = mock(Reporter.class);
        init(null);
    }

    private void init(Locale locale) {
        localizedReporter = new LocalizedReporter(config, mockReporter, locale);
    }

    @AfterEach
    public void verifyMockReporter() {
        verifyNoMoreInteractions(mockReporter);
    }

    @Test
    public void testDebug_when_verbose() {
        config.verbose = true;
        localizedReporter.debug(Message.DOCLET_COPYRIGHT, "1.2.3");

        verify(mockReporter).print(eq(Diagnostic.Kind.NOTE),
                eq("UML Doclet (C) Copyright Talsma ICT, version: 1.2.3."));
    }

    @Test
    public void testDebug_nl() {
        init(NL);
        config.verbose = true;
        localizedReporter.debug(Message.DOCLET_COPYRIGHT, "1.2.3");

        verify(mockReporter).print(eq(Diagnostic.Kind.NOTE),
                eq("UML Doclet (C) Copyright Talsma ICT, versie: 1.2.3."));
    }

    @Test
    public void testDebug_inlineNonResourceMessage() {
        config.verbose = true;
        localizedReporter.debug("The {1} jumps over the {0}", "lazy dog", "quick brown fox");

        verify(mockReporter).print(eq(Diagnostic.Kind.NOTE),
                eq("The quick brown fox jumps over the lazy dog"));
    }

    @Test
    public void testDebug_nonVerbose() {
        localizedReporter.debug(Message.DOCLET_COPYRIGHT, "1.2.3");
    }

    @Test
    public void testInfo() {
        localizedReporter.info(Message.INFO_GENERATING_FILE, "some file");
        verify(mockReporter).print(eq(Diagnostic.Kind.NOTE),
                eq("Generating some file..."));
    }

    @Test
    public void testInfo_nl() {
        init(NL);
        localizedReporter.info(Message.INFO_GENERATING_FILE, "bestand");
        verify(mockReporter).print(eq(Diagnostic.Kind.NOTE),
                eq("Genereren bestand..."));
    }

    @Test
    public void testInfo_when_quiet() {
        config.quiet = true;
        localizedReporter.info(Message.INFO_GENERATING_FILE, "some file");
    }

    @Test
    public void testWarn_when_quiet() {
        config.quiet = true;
        localizedReporter.warn(Message.WARNING_UNRECOGNIZED_IMAGE_FORMAT, ".doc");

        verify(mockReporter).print(eq(Diagnostic.Kind.WARNING),
                eq("Unrecognized image format: \".doc\"."));
    }

    @Test
    public void testError_when_quiet() {
        config.quiet = true;
        localizedReporter.error(Message.ERROR_UNANTICIPATED_ERROR_GENERATING_UML, "reason");

        verify(mockReporter).print(eq(Diagnostic.Kind.ERROR),
                eq("Unanticipated error generating UML: reason"));
    }

    @Test
    public void testPrint_nulls() {
        localizedReporter.print(null, null);
        localizedReporter.print(null, (DocTreePath) null, null);
        localizedReporter.print(null, (Element) null, null);
    }

    @Test
    public void testPrint_withoutReporter() {
        localizedReporter = new LocalizedReporter(config, null, null);
        String msg = getClass().getSimpleName() + ": print message without delegate";
        localizedReporter.print(Diagnostic.Kind.NOTE, msg);
        localizedReporter.print(Diagnostic.Kind.NOTE, (DocTreePath) null, msg + " (DocTreePath method variant)");
        localizedReporter.print(Diagnostic.Kind.NOTE, (Element) null, msg + " (Element method variant)");
    }

    @Test
    public void testPrint() {
        String msg = "Test print message";
        localizedReporter.print(Diagnostic.Kind.NOTE, msg);
        localizedReporter.print(Diagnostic.Kind.WARNING, (DocTreePath) null, msg + " + doctree path");
        localizedReporter.print(Diagnostic.Kind.ERROR, (Element) null, msg + " + element");

        verify(mockReporter).print(eq(Diagnostic.Kind.NOTE),
                eq("Test print message"));
        verify(mockReporter).print(eq(Diagnostic.Kind.WARNING), Mockito.<DocTreePath>isNull(),
                eq("Test print message + doctree path"));
        verify(mockReporter).print(eq(Diagnostic.Kind.ERROR), Mockito.<Element>isNull(),
                eq("Test print message + element"));
    }

    @Test
    void testLocalizeMessageParameter() {
        String result = localizedReporter.localize(Message.INFO_GENERATING_FILE, Message.PLANTUML_COPYRIGHT);
        assertThat(result, stringContainsInOrder("Generating", "This software uses PlantUML"));
    }
}
