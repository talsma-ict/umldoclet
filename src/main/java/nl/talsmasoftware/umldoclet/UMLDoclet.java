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
 */
package nl.talsmasoftware.umldoclet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;
import nl.talsmasoftware.umldoclet.rendering.UMLDiagram;

import java.io.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Eerste aanzet tot een UML doclet die plantuml klassediagrammen kan produceren voor documentatie.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class UMLDoclet extends Standard {
    private static final Logger LOGGER = Logger.getLogger(UMLDoclet.class.getName());

    private final RootDoc rootDoc;
    private final UMLDocletConfig config;

    public UMLDoclet(RootDoc rootDoc) {
        this.rootDoc = Objects.requireNonNull(rootDoc, "No root document received.");
        this.config = new UMLDocletConfig(rootDoc.options(), rootDoc);
    }

    public static int optionLength(String option) {
        return UMLDocletConfig.optionLength(option);
    }

    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        return UMLDocletConfig.validOptions(options, reporter);
    }

    public static boolean start(RootDoc rootDoc) {
        boolean result = new UMLDoclet(rootDoc).generateUMLDocumentation();
        return result && Standard.start(rootDoc);
    }

    public boolean generateUMLDocumentation() {
        boolean result = true;
        // Maak voor elke gedocumenteerde klasse een .puml klassendiagram aan.
        final ClassDoc[] classes = rootDoc.classes();
        for (ClassDoc classDoc : classes) {
            if (!generateClassDiagram(classDoc)) {
                result = false;
                break;
            }
        }
        return result;
    }

    protected Writer createPumlFileWriterFor(ClassDoc classDoc, String encoding) throws IOException {
        File pumlfile = new File(config.basePath());
        for (String packageNm : classDoc.containingPackage().name().split("\\.")) {
            if (packageNm.trim().length() > 0) {
                pumlfile = new File(pumlfile, packageNm);
            }
        }
        if (pumlfile.exists() || pumlfile.mkdirs()) {
            pumlfile = new File(pumlfile, classDoc.name() + ".puml");
            if (pumlfile.exists() || pumlfile.createNewFile()) {
                return new OutputStreamWriter(new FileOutputStream(pumlfile), encoding);
            }
        }
        throw new IllegalStateException("Error creating: " + pumlfile);
    }

    protected boolean generateClassDiagram(ClassDoc classDoc) {
        try (Writer out = createPumlFileWriterFor(classDoc, "UTF-8")) {

            new UMLDiagram(config).singleClassDiagram(classDoc).writeTo(out);
            return true;

        } catch (IOException | RuntimeException ioe) {
            LOGGER.log(Level.SEVERE, "Error writing to .puml file for {0}: {1}", new Object[]{classDoc, ioe.getMessage(), ioe});
            rootDoc.printError(classDoc.position(), String.format("Error writing to .puml file for %s: %s", classDoc, ioe.getMessage()));
            return false;
        }
    }


}
