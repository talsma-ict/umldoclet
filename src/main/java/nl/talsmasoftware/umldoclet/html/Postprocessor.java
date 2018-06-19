/*
 * Copyright 2016-2018 Talsma ICT
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

import nl.talsmasoftware.umldoclet.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.Callable;

final class Postprocessor implements Callable<Boolean> {

    private final HtmlFile htmlFile;
    private final UmlDiagram umlDiagram;
    private final String relativePath, diagramFileName, diagramExtension;

    Postprocessor(HtmlFile htmlFile, UmlDiagram umlDiagram, String relativePath) {
        this.htmlFile = htmlFile;
        this.umlDiagram = umlDiagram;
        this.relativePath = relativePath;
        this.diagramFileName = FileUtils.fileNameOf(relativePath);
        int lastDot = diagramFileName.lastIndexOf('.');
        this.diagramExtension = lastDot > 0 ? diagramFileName.substring(lastDot) : "";
    }

    @Override
    public Boolean call() throws IOException {
        synchronized (htmlFile) {
            File tempFile = File.createTempFile(htmlFile.path.getFileName().toString(), ".tmp");
            List<String> html = htmlFile.readLines();
            boolean alreadyContainsDiagram = false;
            final Inserter inserter = umlDiagram.newInserter(relativePath);
            try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(tempFile)), htmlFile.config.htmlCharset())) {
                boolean written = false;
                for (String line : html) {
                    if (line.contains(diagramFileName)) {
                        alreadyContainsDiagram = true;
                        break;
                    }
                    if (written) writer.write(System.lineSeparator());
                    writer.write(inserter.process(line));
                    written = true;
                }
            }

            boolean result = false;
            if (!alreadyContainsDiagram && inserter.inserted) {
                htmlFile.replaceBy(tempFile);
                result = true;
            } else if (!tempFile.delete()) {
                throw new IllegalStateException("Couldn't delete " + tempFile + " after postprocessing!");
            }
            return result;
        }
    }

    static abstract class Inserter {
        protected boolean inserted = false;
        protected String relativePath;

        protected Inserter(String relativePath) {
            this.relativePath = relativePath;
        }

        abstract String process(String line);
    }

}
