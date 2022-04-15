/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.html.CucaDiagramHtmlMaker;
import net.sourceforge.plantuml.png.PngSplitter;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PSystemUtils {

	public static List<FileImageData> exportDiagrams(Diagram system, SuggestedFile suggested,
			FileFormatOption fileFormatOption) throws IOException {
		return exportDiagrams(system, suggested, fileFormatOption, false);
	}

	public static List<FileImageData> exportDiagrams(Diagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormatOption, boolean checkMetadata) throws IOException {

		final SFile existingFile = suggestedFile.getFile(0);
		if (checkMetadata && fileFormatOption.getFileFormat().doesSupportMetadata() && existingFile.exists()
				&& system.getNbImages() == 1) {
			// final String version = Version.versionString();
			// System.out.println(system.getMetadata());
			// System.out.println(data);
			// System.out.println(version);
			// System.out.println(data.contains(version));
			final boolean sameMetadata = fileFormatOption.getFileFormat().equalsMetadata(system.getMetadata(),
					existingFile);
			if (sameMetadata) {
				Log.info("Skipping " + existingFile.getPrintablePath() + " because metadata has not changed.");
				return Arrays.asList(new FileImageData(existingFile, null));
			}
		}

		if (system instanceof NewpagedDiagram) {
			return exportDiagramsNewpaged((NewpagedDiagram) system, suggestedFile, fileFormatOption);
		}
		if (system instanceof SequenceDiagram) {
			return exportDiagramsSequence((SequenceDiagram) system, suggestedFile, fileFormatOption);
		}
		if (system instanceof CucaDiagram && fileFormatOption.getFileFormat() == FileFormat.HTML) {
			return createFilesHtml((CucaDiagram) system, suggestedFile);
		}

		return exportDiagramsDefault(system, suggestedFile, fileFormatOption);
	}

	private static List<FileImageData> exportDiagramsNewpaged(NewpagedDiagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		final List<FileImageData> result = new ArrayList<>();
		final int nbImages = system.getNbImages();
		for (int i = 0; i < nbImages; i++) {

			final SFile f = suggestedFile.getFile(i);
			if (canFileBeWritten(f) == false) {
				return result;
			}
			final OutputStream fos = f.createBufferedOutputStream();
			ImageData cmap = null;
			try {
				system.exportDiagram(fos, i, fileFormat);
			} finally {
				fos.close();
			}
			// if (system.hasUrl() && cmap != null && cmap.containsCMapData()) {
			// system.exportCmap(suggestedFile, cmap);
			// }
			Log.info("File size : " + f.length());
			result.add(new FileImageData(f, cmap));
		}
		return result;
	}

	public static boolean canFileBeWritten(final SFile f) {
		Log.info("Creating file: " + f.getAbsolutePath());
		if (f.exists() && f.canWrite() == false) {
			if (OptionFlags.getInstance().isOverwrite()) {
				Log.info("Overwrite " + f);
				f.setWritable(true);
				f.delete();
				return true;
			}
			Log.error("Cannot write to file " + f.getAbsolutePath());
			return false;
		}
		return true;
	}

	private static List<FileImageData> exportDiagramsSequence(SequenceDiagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		final List<FileImageData> result = new ArrayList<>();
		final int nbImages = system.getNbImages();
		for (int i = 0; i < nbImages; i++) {

			final SFile f = suggestedFile.getFile(i);
			if (PSystemUtils.canFileBeWritten(suggestedFile.getFile(i)) == false) {
				return result;
			}
			final OutputStream fos = f.createBufferedOutputStream();
			ImageData cmap = null;
			try {
				cmap = system.exportDiagram(fos, i, fileFormat);
			} finally {
				fos.close();
			}
			if (cmap != null && cmap.containsCMapData()) {
				system.exportCmap(suggestedFile, i, cmap);
			}
			Log.info("File size : " + f.length());
			result.add(new FileImageData(f, cmap));
		}
		return result;
	}

	private static List<FileImageData> createFilesHtml(CucaDiagram system, SuggestedFile suggestedFile)
			throws IOException {
		final String name = suggestedFile.getName();
		final int idx = name.lastIndexOf('.');
		final SFile dir = suggestedFile.getParentFile().file(name.substring(0, idx));
		final CucaDiagramHtmlMaker maker = new CucaDiagramHtmlMaker(system, dir);
		return maker.create();
	}

	private static List<FileImageData> splitPng(TitledDiagram diagram, SuggestedFile pngFile, ImageData imageData, FileFormatOption fileFormatOption)
			throws IOException {

		final List<SFile> files = new PngSplitter(
				pngFile,
				diagram.getSplitPagesHorizontal(),
				diagram.getSplitPagesVertical(),
				fileFormatOption.isWithMetadata() ? diagram.getMetadata() : null,
				diagram.getSkinParam().getDpi(),
				diagram instanceof GanttDiagram
						? new SplitParam(HColorUtils.BLACK, null, 5)  // for backwards compatibility
						: diagram.getSkinParam().getSplitParam()
		).getFiles();

		final List<FileImageData> result = new ArrayList<>();
		for (SFile f : files) {
			result.add(new FileImageData(f, imageData));
		}
		return result;
	}

	private static List<FileImageData> exportDiagramsDefault(Diagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormatOption) throws IOException {

		final SFile outputFile = suggestedFile.getFile(0);

		if (outputFile.isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}

		if (!canFileBeWritten(outputFile)) {
			return emptyList();
		}

		final ImageData imageData;

		try (OutputStream os = outputFile.createBufferedOutputStream()) {
			imageData = system.exportDiagram(os, 0, fileFormatOption);
		}

		if (imageData == null) {
			return emptyList();
		}

		if (imageData.containsCMapData() && system instanceof UmlDiagram) {
			((UmlDiagram) system).exportCmap(suggestedFile, 0, imageData);
		}

		if (system instanceof TitledDiagram && fileFormatOption.getFileFormat() == FileFormat.PNG) {
			return splitPng((TitledDiagram) system, suggestedFile, imageData, fileFormatOption);
		}

		return singletonList(new FileImageData(outputFile, imageData));
	}
}
