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

import com.sun.javadoc.*;
import com.sun.tools.doclets.standard.Standard;
import nl.talsmasoftware.umldoclet.config.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.logging.GlobalPosition;
import nl.talsmasoftware.umldoclet.rendering.DiagramRenderer;
import nl.talsmasoftware.umldoclet.rendering.plantuml.PlantumlImageWriter;
import nl.talsmasoftware.umldoclet.rendering.plantuml.PlantumlSupport;

import java.io.*;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.logging.LogSupport.*;

/**
 * UML doclet that generates <a href="http://plantuml.com">PlantUML</a> class diagrams from your java code just as
 * easily as creating proper JavaDoc comments. It actually does that too by delegating to JavaDoc's {@link Standard}
 * doclet for the 'regular' HTML documentation.
 *
 * @author Sjoerd Talsma
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
        info("{0} version {1}", getClass().getSimpleName(), config.version());
        trace("Plantuml {0} detected.", PlantumlSupport.isPlantumlDetected() ? "was" : "was not");
        debug("Initialized {0}...", config);
    }

    /**
     * Let's assume we support the java version the standard Doclet is made for!
     *
     * @return The same language version the Standard doclet also supports.
     */
    public static LanguageVersion languageVersion() {
        return Standard.languageVersion();
    }

    public static int optionLength(String option) {
        final int optionLength = UMLDocletConfig.optionLength(option);
        return optionLength > 0 ? optionLength : Standard.optionLength(option);
    }

    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        final UMLDocletConfig config = new UMLDocletConfig(options, reporter);
        return Standard.validOptions(config.standardOptions(), reporter) && config.isValid();
    }

    public static boolean start(RootDoc rootDoc) {
        UMLDoclet umlDoclet = new UMLDoclet(rootDoc);
        boolean umlDocletResult = umlDoclet.generateUMLDiagrams();
        // Regarding issue #13: I don't understand why the Standard doclet will run on a 'bad' javadoc
        // contained in RootDoc somewhere after UMLDoclet has done it's thing, but
        // send us a (correct) JavaDoc ERROR if ran on the same rootDoc 'untouched'...
        // Think about this; Is there some way to 'clone' it and pass the original rootDoc to the
        // Standard doclet??
        // TODO re-test this issue with the added 'languageVersion()' method.
        if (umlDocletResult && !umlDoclet.config.skipStandardDoclet()) {
            return Standard.start(rootDoc);
        }
        return umlDocletResult;
    }

    public boolean generateUMLDiagrams() {
        try (GlobalPosition gp = new GlobalPosition(rootDoc)) {

            return generateIndividualClassDiagrams(rootDoc.classes())
                    && generatePackageDiagrams();
            
        } catch (RuntimeException rte) {
            error(rte.getMessage(), rte);
            return false;
        }
    }

    protected boolean generateIndividualClassDiagrams(ClassDoc... classDocs) {
        debug("Generating class diagrams for all individual classes...");
        for (ClassDoc classDoc : classDocs) {
            encounteredPackages.add(classDoc.containingPackage());
            try (GlobalPosition gp = new GlobalPosition(classDoc);
                 Writer out = createWriterForNewClassFile(classDoc)) {

                new DiagramRenderer(config).addClass(classDoc).writeTo(out);

            } catch (IOException | RuntimeException exception) {
                String message = String.format("Error writing to %s file for %s: %s",
                        config.umlFileExtension(), classDoc.qualifiedName(), exception.getMessage());
                if (isTraceEnabled()) {
                    StringWriter stacktrace = new StringWriter();
                    exception.printStackTrace(new PrintWriter(stacktrace));
                    trace("{0}\n{1}", message, stacktrace);
                }
                // TODO Log error at current position and return false?
                throw new IllegalStateException(message, exception);
            }
        }
        debug("All individual class diagrams have been generated.");
        return true;
    }

    protected boolean generatePackageDiagrams() {
        debug("Generating package diagrams for all packages...");
        for (PackageDoc packageDoc : encounteredPackages) {
            try (GlobalPosition gp = new GlobalPosition(packageDoc);
                 Writer out = createWriterForNewPackageFile(packageDoc)) {

                new DiagramRenderer(config).addPackage(packageDoc).writeTo(out);

            } catch (IOException | RuntimeException exception) {
                String message = String.format("Error writing to %s file for package %s: %s",
                        config.umlFileExtension(), packageDoc.name(), exception.getMessage());
                if (isTraceEnabled()) {
                    StringWriter stacktrace = new StringWriter();
                    exception.printStackTrace(new PrintWriter(stacktrace));
                    trace("{0}\n{1}", message, stacktrace);
                }
                // TODO Log error at current position and return false?
                throw new IllegalStateException(message, exception);
            }
        }
        debug("All package diagrams have been generated.");
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
        return createUmlWriterFor(documentedClass.containingPackage().name(), documentedClass.name());
    }

    /**
     * Create a new plant UML file for the given documented package and return a new {@link Writer} object to it.
     *
     * @param documentedPackage The package that should be documented in a new PlantUML definition file.
     * @return The created Writer to the correct PlantUML file.
     * @throws IOException In case there were I/O errors creating a new plantUML file or opening a Writer to it.
     */
    protected Writer createWriterForNewPackageFile(PackageDoc documentedPackage) throws IOException {
        return createUmlWriterFor(documentedPackage.name(), "package");
    }

    private Writer createUmlWriterFor(String qualifiedPackageName, String baseName) throws IOException {
        final File basePath = new File(config.basePath());
        final File umlDirectory = subDirectory(basePath, qualifiedPackageName, "\\.");
        File imageDirectory = umlDirectory;
        String imageBaseName = baseName;
        if (config.imageDirectory() != null) { // Enhancement #25: Use a single directory for images.
            imageDirectory = subDirectory(basePath, config.imageDirectory(), "[/\\\\]");
            if (qualifiedPackageName.length() > 0) imageBaseName = qualifiedPackageName + "." + baseName;
        }
        return createWriterForUmlFile(umlDirectory, baseName, imageDirectory, imageBaseName);
    }

    private static File subDirectory(File baseDirectory, String subDirectoryName, String separatorRegex) {
        File subDirectory = baseDirectory;
        if (subDirectoryName != null) for (String subdir : subDirectoryName.split(separatorRegex)) {
            subdir = subdir.trim();
            if (subdir.length() > 0) subDirectory = new File(subDirectory, subdir);
        }
        return subDirectory;
    }

    /**
     * Creates a new Writer for a new UML file in the given directory with the specified baseName.
     * The {@link UMLDocletConfig#umlFileExtension() UML file extension} will be added to the file.
     * <p>
     * Also, if PlantUML is detected on the classpath, an attempt will be made to automatically generate a binary
     * image with the same name.
     *
     * @param umlDirectory The directory where to create the new UML file to render to.
     * @param umlBaseName  The base filename (without extension) to render to.
     * @param imgDirectory The directory where images need to be created.
     * @param imgBaseName  The base filename for the image(s) to create.
     * @return The writer to render the UML diagram with.
     * @throws IOException in case there were I/O errors creating a new PlantUML file for opening a Writer to it.
     */
    private Writer createWriterForUmlFile(File umlDirectory, String umlBaseName, File imgDirectory, String imgBaseName)
            throws IOException {
        File umlFile = requireNonNull(umlDirectory, "Directory was null.");
        if (umlFile.exists() || umlFile.mkdirs()) {
            umlFile = new File(umlFile, umlBaseName + config.umlFileExtension());
            if (umlFile.exists() || umlFile.createNewFile()) {
                info("Generating {0}...", umlFile);
                Writer writer = new OutputStreamWriter(new FileOutputStream(umlFile), config.umlFileEncoding());
                String[] imageFormats = config.imageFormats();
                if (imageFormats.length > 0 && PlantumlSupport.isPlantumlDetected()) {
                    if (!imgDirectory.exists() && !imgDirectory.mkdirs()) {
                        throw new IllegalStateException("Error creating: " + imgDirectory.getPath());
                    }
                    writer = new PlantumlImageWriter(writer, imgDirectory, imgBaseName, imageFormats);
                }
                return writer;
            }
        }
        throw new IllegalStateException("Error creating: " + umlFile);
    }

}
