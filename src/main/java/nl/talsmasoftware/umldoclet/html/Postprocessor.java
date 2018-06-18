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
            File tempFile = File.createTempFile(diagramFileName, ".tmp");
            List<String> html = htmlFile.readLines();
            boolean alreadyContainsDiagram = false, diagramInserted = false;
            try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(tempFile)), htmlFile.config.htmlCharset())) {
                boolean written = false, summaryDivCleared = false;
                for (String line : html) {
                    if (line.contains(diagramFileName)) {
                        alreadyContainsDiagram = true;
                        break;
                    } else if (!diagramInserted && line.contains("<hr>")) {
                        line = line.replaceFirst("(\\s*)<hr>", "$1<hr>" + System.lineSeparator() + "$1" + getImageTag());
                        diagramInserted = true;
                    } else if (diagramInserted && !summaryDivCleared) {
                        String cleared = clearSummaryDiv(line);
                        if (cleared != null) {
                            line = cleared;
                            summaryDivCleared = true;
                        }
                    }

                    if (written) writer.write(System.lineSeparator());
                    writer.write(line);
                    written = true;
                }
            }

            boolean result = false;
            if (!alreadyContainsDiagram && diagramInserted) {
                htmlFile.replaceBy(tempFile);
                result = true;
            } else if (!tempFile.delete()) {
                throw new IllegalStateException("Couldn't delete " + tempFile + " after postprocessing!");
            }
            return result;
        }
    }

    private String getImageTag() {
        final String name = diagramFileName.substring(0, diagramFileName.length() - diagramExtension.length());
        return "<img src=\"" + relativePath + "\" alt=\"" + name + " UML Diagram\" style=\"float: right;\">";
    }

    private String clearSummaryDiv(String line) {
        final String summaryDiv = "<div class=\"summary\"";
        int idx = line.indexOf(summaryDiv);
        if (idx < 0) return null;
        int ins = idx + summaryDiv.length();
        line = line.substring(0, ins) + " style=\"clear: right;\"" + line.substring(ins);
        return line;
    }

}
