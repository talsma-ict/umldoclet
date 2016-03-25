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
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.rendering.UMLDiagram;

import java.io.*;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Objects.requireNonNull;

/**
 * UML doclet that generates <a href="http://plantuml.com">PlantUML</a> class diagrams from your java code just as
 * easily as creating proper JavaDoc comments. It actually does that too by delegating to JavaDoc's {@link Standard}
 * doclet for the 'regular' HTML documentation.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class UMLDoclet extends Standard {
    private final RootDoc rootDoc;
    private final UMLDocletConfig config;
    private final SortedSet<PackageDoc> encounteredPackages = new TreeSet<>(new Comparator<PackageDoc>() {
        public int compare(PackageDoc o1, PackageDoc o2) {
            return o1 == null ? (o2 == null ? 0 : -1)
                    : o2 == null ? 1
                    : o1.name().compareToIgnoreCase(o2.name());
        }
    });

    public UMLDoclet(RootDoc rootDoc) {
        this.rootDoc = requireNonNull(rootDoc, "No root document received.");
        this.config = new UMLDocletConfig(rootDoc.options(), rootDoc);
        LogSupport.info("{0} version {1}", getClass().getSimpleName(), config.version());
        LogSupport.debug("Initialized {0}...", config);
    }

    public static int optionLength(String option) {
        return UMLDocletConfig.optionLength(option);
    }

    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        return UMLDocletConfig.validOptions(options, reporter);
    }

    public static boolean start(RootDoc rootDoc) {
        UMLDoclet umlDoclet = new UMLDoclet(rootDoc);
        return umlDoclet.generateUMLDiagrams()
                && (umlDoclet.config.skipStandardDoclet() || Standard.start(rootDoc));
    }

    public boolean generateUMLDiagrams() {
        try {
            return generateIndividualClassDiagrams(rootDoc.classes())
                    && generatePackageDiagrams();
        } catch (RuntimeException rte) {
            LogSupport.INSTANCE.printError(rootDoc.position(), rte.getMessage());
            return false;
        }
    }

    protected boolean generateIndividualClassDiagrams(ClassDoc... classDocs) {
        LogSupport.debug("Generating class diagrams for all individual classes...");
        for (ClassDoc classDoc : classDocs) {
            encounteredPackages.add(classDoc.containingPackage());
            try (Writer out = createWriterForNewClassFile(classDoc)) {
                new UMLDiagram(config).addClass(classDoc).writeTo(out);
            } catch (IOException | RuntimeException exception) {
                String message = String.format("Error writing to %s file for %s: %s",
                        config.umlFileExtension(), classDoc.qualifiedName(), exception.getMessage());
                if (LogSupport.isTraceEnabled()) {
                    StringWriter stacktrace = new StringWriter();
                    exception.printStackTrace(new PrintWriter(stacktrace));
                    LogSupport.trace("{0}\n{1}", message, stacktrace);
                }
                // TODO Log error at current position and return false?
                throw new IllegalStateException(message, exception);
            }
        }
        LogSupport.debug("All individual class diagrams have been generated.");
        return true;
    }

    protected boolean generatePackageDiagrams() {
        LogSupport.debug("Generating package diagrams for all packages...");
        for (PackageDoc packageDoc : encounteredPackages) {
            try (Writer out = createWriterForNewPackageFile(packageDoc)) {
                new UMLDiagram(config).addPackage(packageDoc).writeTo(out);
            } catch (IOException | RuntimeException exception) {
                String message = String.format("Error writing to %s file for package %s: %s",
                        config.umlFileExtension(), packageDoc.name(), exception.getMessage());
                if (LogSupport.isTraceEnabled()) {
                    StringWriter stacktrace = new StringWriter();
                    exception.printStackTrace(new PrintWriter(stacktrace));
                    LogSupport.trace("{0}\n{1}", message, stacktrace);
                }
                // TODO Log error at current position and return false?
                throw new IllegalStateException(message, exception);
            }
        }
        LogSupport.debug("All package diagrams have been generated.");
        return true;
    }

    /**
     * Create a new plant UML file for the given documented class and return a new {@link Writer} object to it.
     *
     * @param documentedClass The class that should be documented in a new PlantUML definition file.
     * @return The created Writer to the correct PlantUML file.
     * @throws IOException In case there were I/O errors creating a new plantUML file or opening a Writer to it.
     */

    protected Writer createWriterForNewClassFile(ClassDoc documentedClass) throws IOException {
        File umlFile = new File(config.basePath());
        for (String packageNm : documentedClass.containingPackage().name().split("\\.")) {
            if (packageNm.trim().length() > 0) {
                umlFile = new File(umlFile, packageNm);
            }
        }
        if (umlFile.exists() || umlFile.mkdirs()) {
            umlFile = new File(umlFile, documentedClass.name() + config.umlFileExtension());
            if (umlFile.exists() || umlFile.createNewFile()) {
                LogSupport.info("Generating {0}...", umlFile);
                return new OutputStreamWriter(new FileOutputStream(umlFile), config.umlFileEncoding());
            }
        }
        throw new IllegalStateException("Error creating: " + umlFile);
    }

    /**
     * Create a new plant UML file for the given documented package and return a new {@link Writer} object to it.
     *
     * @param documentedPackage The package that should be documented in a new PlantUML definition file.
     * @return The created Writer to the correct PlantUML file.
     * @throws IOException In case there were I/O errors creating a new plantUML file or opening a Writer to it.
     */
    protected Writer createWriterForNewPackageFile(PackageDoc documentedPackage) throws IOException {
        File umlFile = new File(config.basePath());
        for (String packageNm : documentedPackage.name().split("\\.")) {
            if (packageNm.trim().length() > 0) {
                umlFile = new File(umlFile, packageNm);
            }
        }
        if (umlFile.exists() || umlFile.mkdirs()) {
            umlFile = new File(umlFile, "package" + config.umlFileExtension());
            if (umlFile.exists() || umlFile.createNewFile()) {
                LogSupport.info("Generating {0}...", umlFile);
                return new OutputStreamWriter(new FileOutputStream(umlFile), config.umlFileEncoding());
            }
        }
        throw new IllegalStateException("Error creating: " + umlFile);
    }

}
