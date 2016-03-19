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
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Created on 22-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class PackageRenderer extends Renderer {
    private final static Logger LOGGER = Logger.getLogger(PackageRenderer.class.getName());

    protected final PackageDoc packageDoc;

    protected PackageRenderer(UMLDiagram diagram, PackageDoc packageDoc) {
        super(diagram);
        this.packageDoc = requireNonNull(packageDoc, "No package documentation provided.");
        for (ClassDoc classDoc : packageDoc.allClasses(false)) {
            if (classDoc == null) {
                LOGGER.log(Level.WARNING, "Encountered <null> class doc in package \"{0}\"!", packageDoc.name());
            } else if (diagram.config.includeClass(classDoc)) {
                children.add(new ClassRenderer(this, classDoc));
            }
        }
        List<ClassReferenceRenderer> references = new ArrayList<>();
        for (Renderer child : children) {
            if (child instanceof ClassRenderer) {
                references.addAll(ClassReferenceRenderer.referencesFor((ClassRenderer) child));
            }
        }
        children.addAll(references);
    }

    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        out.append("namespace").whitespace()
                .append(packageDoc.name()).whitespace()
                .append('{').newline().newline();
        writeChildrenTo(out);
        return out.append('}').newline().newline();
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
