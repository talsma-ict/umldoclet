/*
 * Copyright 2016-2017 Talsma ICT
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
import nl.talsmasoftware.umldoclet.logging.GlobalPosition;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

/**
 * Specific renderer for annotations.
 *
 * @author Sjoerd Talsma
 */
public class AnnotationRenderer extends ClassRenderer {

    protected AnnotationRenderer(Renderer parent, ClassDoc classDoc) {
        super(parent, classDoc);
    }

    @Override
    protected IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        // As far as I know, annotations don't currently support rendering children yet.
        try (GlobalPosition gp = new GlobalPosition(classDoc.position())) {
            return writeNameTo(out.append(umlType()).whitespace()).newline().newline();
        }
    }

}
