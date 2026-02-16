/*
 * Copyright 2016-2026 Talsma ICT
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

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import nl.talsmasoftware.umldoclet.configuration.ImageConfig;

import java.io.File;

import static nl.talsmasoftware.umldoclet.util.FileUtils.relativePath;

/// @author Sjoerd Talsma
final class PackageDiagramInserter extends DiagramFile {

    private final String extension, pathToCompare;

    PackageDiagramInserter(File basedir, File diagramFile, ImageConfig.Format format, boolean hasImagesDirectory) {
        super(basedir, diagramFile, format);
        final String fileName = diagramFile.getName();
        int dotIdx = fileName.lastIndexOf('.');
        this.extension = fileName.substring(dotIdx);
        if (hasImagesDirectory) {
            this.pathToCompare = fileName.substring(0, dotIdx).replace('.', '/') + extension;
        } else {
            this.pathToCompare = relativePath(this.basedir, this.diagramFile);
        }
    }

    private String changeHtmlFileNameToDiagram(String htmlFileName) {
        return htmlFileName.replaceFirst("package-summary.html$", "package" + extension);
    }

    @Override
    protected boolean matches(HtmlFile html) {
        return pathToCompare.equals(changeHtmlFileNameToDiagram(relativePath(basedir, html.path.toFile())));
    }

    @Override
    public Postprocessor.Inserter newInserter(String relativePathToDiagram) {
        return new Inserter(relativePathToDiagram, ImageConfig.Format.SVG.equals(this.format));
    }

    private static final class Inserter extends Postprocessor.Inserter {
        private static final String CENTER_STYLE = "style=\"display:block;margin-left:auto;margin-right:auto;max-width:100%;\"";

        private final boolean insertAsObject;

        private Inserter(String relativePath, boolean insertAsObject) {
            super(relativePath);
            this.insertAsObject = insertAsObject;
        }

        @Override
        String process(String line) {
            if (!inserted) {
                int idx = line.indexOf("<table");
                if (idx >= 0) {
                    inserted = true;
                    return line.substring(0, idx) + getImageTag() + System.lineSeparator() + line.substring(idx);
                }
                idx = line.indexOf("<section class=\"summary\">");
                if (idx >= 0) {
                    inserted = true;
                    idx = line.indexOf('>', idx) + 1;
                    return line.substring(0, idx) + System.lineSeparator() + getImageTag() + System.lineSeparator() + line.substring(idx);
                }
            }
            return line;
        }

        private String getImageTag() {
            if (insertAsObject) { // Render SVG images as objects to make their links work
                return "<object type=\"image/svg+xml\" data=\"" + relativePath + "\" " + CENTER_STYLE + "></object>";
            }
            return "<img src=\"" + relativePath + "\" alt=\"Package summary UML Diagram\" " + CENTER_STYLE + "/>";
        }
    }

	@Override
	protected boolean excludedBy(Configuration config) {
		return config.excludePackageDiagram();
	}

}
