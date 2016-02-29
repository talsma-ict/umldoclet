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
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import java.util.ArrayList;
import java.util.List;
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

    public PackageRenderer(UMLDocletConfig config, UMLDiagram diagram, PackageDoc packageDoc) {
        super(config, diagram);
        this.packageDoc = requireNonNull(packageDoc, "No package documentation provided.");
        for (ClassDoc classDoc : packageDoc.allClasses(false)) {
            if (classDoc != null) {
                children.add(new ClassRenderer(config, diagram, classDoc));
            } else {
                LOGGER.log(Level.WARNING, "Encountered <null> class doc in package \"{0}\"!", packageDoc.name());
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

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        // out.append("' Package \"").append(packageDoc.name()).append("\":").newline();
        out.append("namespace ").append(packageDoc.name()).append(" {").newline().newline();
        writeChildrenTo(out);
        return out.append("}").newline().newline();
    }

}
