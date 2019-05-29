/*
 * Copyright 2016-2019 Talsma ICT
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
package nl.talsmasoftware.umldoclet.html;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PackageDependenciesInserter extends DiagramFile {
    private static final Pattern CONTENT_CONTAINER_DIV = Pattern.compile("<div[^>]+class=\"(\\d+,\\s*)*contentContainer\\W[^>]*>");

    PackageDependenciesInserter(File basedir, File diagramFile) {
        super(basedir, diagramFile);
    }

    @Override
    boolean matches(HtmlFile htmlFile) {
        return htmlFile.path.equals(new File(basedir, "overview-summary.html").toPath());
    }

    @Override
    public Postprocessor.Inserter newInserter(String relativePathToDiagram) {
        //  For overview-summary.html:
        //      <center><object type="image/svg+xml" data="package-dependencies.svg" style="max-width:80%;"></object></center>
        //  Between <div class="contentContainer"> and <a name="Packages">

        return new Postprocessor.Inserter(relativePathToDiagram) {
            @Override
            String process(String line) {
                if (!inserted) {
                    Matcher m = CONTENT_CONTAINER_DIV.matcher(line);
                    if (m.find()) {
                        int insertionPoint = m.end();
                        // TODO similar to class + package diagrams, create both SVG and IMG versions
                        line = line.substring(0, insertionPoint)
                                + "<center><object type=\"image/svg+xml\" data=\""
                                + relativePathToDiagram +
                                "\" style=\"max-width:80%;\"></object></center>"
                                + line.substring(insertionPoint);
                        inserted = true;
                    }
                }
                return line;
            }
        };
    }
}
