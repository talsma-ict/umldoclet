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
import java.util.Optional;

final class PackageDependenciesInserter extends UmlDiagram {

    PackageDependenciesInserter(File basedir, File diagramFile) {
        super(basedir, diagramFile);
    }

    Optional<Postprocessor> createPostprocessor(HtmlFile htmlFile) {
        if (htmlFile.path.equals(new File(basedir, "overview-summary.html").toPath())) {
            return Optional.of(new Postprocessor(htmlFile, this));
        }
        return Optional.empty();
    }

    @Override
    public Postprocessor.Inserter newInserter(String relativePathToDiagram) {
        //  For overview-summary.html:
        //      <center><object type="image/svg+xml" data="package-dependencies.svg" style="max-width:80%;"></object></center>
        //  Between <div class="contentContainer"> and <a name="Packages">

        return new Postprocessor.Inserter(relativePathToDiagram) {
            @Override
            String process(String line) {
                if (!inserted && line.contains("<div class=\"contentContainer\">")) {
                    line = line.replace("<div class=\"contentContainer\">", "<div class=\"contentContainer\">"
                            + "<center><object type=\"image/svg+xml\" data=\"" + relativePathToDiagram +
                            "\" style=\"max-width:80%\"></object></center>");
                    inserted = true;
                }
                return line;
            }
        };
    }
}
