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

import com.sun.javadoc.PackageDoc;
import nl.talsmasoftware.umldoclet.UMLDocletConfig;
import nl.talsmasoftware.umldoclet.rendering.indent.IndentingPrintWriter;

import static java.util.Objects.requireNonNull;

/**
 * Created on 22-02-2016.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class PackageRenderer extends Renderer {

    private final PackageDoc packageDoc;

    public PackageRenderer(UMLDocletConfig config, PackageDoc packageDoc) {
        super(config);
        this.packageDoc = requireNonNull(packageDoc, "No package documentation provided.");
    }

    public IndentingPrintWriter writeTo(IndentingPrintWriter out) {
        out.println(String.format("' Package \"%s\":", packageDoc.name()));

        // TODO obviously:
        out.println("TODO: Create package diagram!");

        return out;
    }

}
