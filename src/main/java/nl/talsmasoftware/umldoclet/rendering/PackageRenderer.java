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
package nl.talsmasoftware.umldoclet.rendering;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import nl.talsmasoftware.umldoclet.logging.GlobalPosition;
import nl.talsmasoftware.umldoclet.logging.LogSupport;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static nl.talsmasoftware.umldoclet.rendering.ClassPropertyRenderer.addDiagramDependenciesTo;

/**
 * Created on 22-02-2016.
 *
 * @author Sjoerd Talsma
 */
public class PackageRenderer extends Renderer {
    protected final PackageDoc packageDoc;

    protected PackageRenderer(DiagramRenderer diagram, PackageDoc packageDoc) {
        super(diagram);
        try (GlobalPosition gp = new GlobalPosition(packageDoc)) {
            this.packageDoc = requireNonNull(packageDoc, "No package documentation provided.");

            // Phase 1: find all classes in the package.
            Collection<ClassRenderer> classes = new LinkedHashSet<>();
            Collection<ClassDoc> encounteredClasses = new LinkedHashSet<>();
            for (ClassDoc classDoc : packageDoc.allClasses(false)) {
                if (classDoc == null) {
                    LogSupport.warn("Encountered <null> class doc in package \"{0}\"!", packageDoc.name());
                } else if (diagram.config.includeClass(classDoc)) {
                    if (classes.add(new ClassRenderer(this, classDoc))) encounteredClasses.add(classDoc);
                }
            }

            // Phase 2: find all references within the package and any superclass references etc.
            Collection<ClassReferenceRenderer> references = new LinkedHashSet<>();
            for (ClassRenderer child : classes) {
                for (ClassReferenceRenderer ref : ClassReferenceRenderer.referencesFor(child)) {
                    if (references.add(ref)) encounteredClasses.add(ref.classDoc);
                }
            }

            // Phase 3: add dependencies within this package.
            if (diagram.config.usePackageDependencies()) for (ClassRenderer child : classes) {
                addDiagramDependenciesTo(references, child, encounteredClasses);
            }

            // Finally, compose the diagram in the order we want things rendered.
            children.addAll(classes);
            children.addAll(references);
        }
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        try (GlobalPosition pos = new GlobalPosition(packageDoc.position())) {
            out.append("namespace").whitespace()
                    .append(packageDoc.name()).whitespace()
                    .append('{').newline().newline();
            writeChildrenTo(out);
            return out.append('}').newline().newline();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageDoc.name());
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof PackageRenderer
                && Objects.equals(packageDoc.name(), ((PackageRenderer) other).packageDoc.name()));
    }

}
