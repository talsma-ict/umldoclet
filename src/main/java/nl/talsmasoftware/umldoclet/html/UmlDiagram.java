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

/**
 * @author Sjoerd Talsma
 */
abstract class UmlDiagram {

    protected final File basedir, diagramFile;

    UmlDiagram(File basedir, File diagramFile) {
        this.basedir = basedir;
        this.diagramFile = diagramFile;
    }

    /**
     * Creates a postprocessor <strong>if</strong> this diagram corresponds to the HTML file.
     *
     * @param htmlFile
     * @return
     */
    Optional<Postprocessor> createPostprocessor(HtmlFile htmlFile) {
        return Optional.empty();
    }

    public abstract Postprocessor.Inserter newInserter(String relativePathToDiagram);

}
