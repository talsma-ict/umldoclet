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

import java.util.concurrent.Callable;

final class Postprocessor implements Callable<Boolean> {

    private final HtmlFile htmlFile;
    private final UmlDiagram umlDiagram;
    private final String relativePath;

    Postprocessor(HtmlFile htmlFile, UmlDiagram umlDiagram, String relativePath) {
        this.htmlFile = htmlFile;
        this.umlDiagram = umlDiagram;
        this.relativePath = relativePath;
    }

//    private Path determineTemporaryOutputFile() {
//        File htmlFile = path.toFile();
//        String newName = path.getFileName().toString();
//        int afterDot = newName.lastIndexOf('.') + 1;
//        newName = newName.substring(0, afterDot) + '_' + newName.substring(afterDot);
//        return new File(htmlFile.getParent(), newName).toPath();
//    }

    @Override
    public Boolean call() {
        // TODO: Delegate to UmlDiagram and move implementation into separate class
//        try {
//            String diagramName = fileNameOf(relativeDiagramPath);
//            AtomicBoolean containsDiagram = new AtomicBoolean(false);
//            AtomicBoolean diagramInserted = new AtomicBoolean(false);
//            Stream<String> lines = readHtml().peek(line -> {
//                if (line.contains(diagramName)) containsDiagram.set(true);
//            }).map(line -> {
//                if (line.contains("<hr>") && diagramInserted.compareAndSet(false, true)) {
//                    return line.replaceFirst("<hr>", "<hr>" + System.lineSeparator()
//                            + "<img src=\"" + relativeDiagramPath + "\" alt=\"UML diagram\" style=\"float: right;\">");
//                }
//                return line;
//            });
//            Path newCopy = determineTemporaryOutputFile();
//            try (BufferedWriter writer = newBufferedWriter(newCopy, config.htmlCharset())) {
//                writer.write(lines.collect(joining(System.lineSeparator())));
//            }
//            if (!containsDiagram.get() && diagramInserted.get()) {
//                File pathFile = path.toFile();
//                File newCopyFile = newCopy.toFile();
//                if (!pathFile.delete() || !newCopyFile.renameTo(pathFile)) {
//                    throw new IllegalStateException("Could not replace original " + path + " by postprocessed " + newCopy.getFileName());
//                }
//                System.out.println("UmlClassDiagram " + relativeDiagramPath + " inserted into " + path);
//            } else {
//                if (!newCopy.toFile().delete()) {
//                    throw new IllegalStateException("Could not delete unnecessary copy " + newCopy + ".");
//                }
//                System.out.println("UmlClassDiagram " + relativeDiagramPath + " wasn't inserted into " + path);
//            }
//
//            return true;
//        } catch (IOException ioe) {
//            throw new IllegalStateException("I/O error processing " + path + ": " + ioe.getMessage(), ioe);
//        }
        return false;
    }

}
