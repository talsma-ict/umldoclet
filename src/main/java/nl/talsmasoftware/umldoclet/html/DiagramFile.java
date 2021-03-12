/*
 * Copyright 2016-2021 Talsma ICT
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

/**
 * @author Sjoerd Talsma
 */
abstract class DiagramFile {

    final File basedir, diagramFile;

    DiagramFile(File basedir, File diagramFile) {
        this.basedir = basedir;
        this.diagramFile = diagramFile;
    }

    /**
     * Evaluate whether this diagram matches with the specified HTML file.
     *
     * @param htmlFile The html file being visited.
     * @return Whether the diagram should be inserted in the specified HTML file.
     */
    boolean matches(HtmlFile htmlFile) {
        return false;
    }

    public abstract Postprocessor.Inserter newInserter(String relativePathToDiagram);

}
