/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
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
		if (system instanceof CucaDiagram) {
			return exportDiagramsCuca((CucaDiagram) system, suggestedFile, fileFormatOption);
		}
		if (system instanceof GanttDiagram) {
			return exportDiagramsGantt2((GanttDiagram) system, suggestedFile, fileFormatOption);
		}
		if (system instanceof ActivityDiagram3) {
			return exportDiagramsActivityDiagram3((ActivityDiagram3) system, suggestedFile, fileFormatOption);
		}
		return exportDiagramsDefault(system, suggestedFile, fileFormatOption);
	}

	private static List<FileImageData> exportDiagramsNewpaged(NewpagedDiagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		final List<FileImageData> result = new ArrayList<FileImageData>();
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

	static private List<FileImageData> exportDiagramsDefault(Diagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		if (suggestedFile.getFile(0).exists() && suggestedFile.getFile(0).isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}
		OutputStream os = null;
		ImageData imageData = null;
		try {
			if (PSystemUtils.canFileBeWritten(suggestedFile.getFile(0)) == false) {
				return Collections.emptyList();
			}
			os = suggestedFile.getFile(0).createBufferedOutputStream();
			// system.exportDiagram(os, null, 0, fileFormat);
			imageData = system.exportDiagram(os, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		return Arrays.asList(new FileImageData(suggestedFile.getFile(0), imageData));
	}

	static private List<FileImageData> exportDiagramsActivityDiagram3(ActivityDiagram3 system,
			SuggestedFile suggestedFile, FileFormatOption fileFormat) throws IOException {
		if (suggestedFile.getFile(0).exists() && suggestedFile.getFile(0).isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}
		OutputStream os = null;
		ImageData cmap = null;
		ImageData imageData = null;
		try {
			if (PSystemUtils.canFileBeWritten(suggestedFile.getFile(0)) == false) {
				return Collections.emptyList();
			}
			os = suggestedFile.getFile(0).createBufferedOutputStream();
			imageData = cmap = system.exportDiagram(os, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		if (cmap != null && cmap.containsCMapData()) {
			system.exportCmap(suggestedFile, 0, cmap);
		}
		return Arrays.asList(new FileImageData(suggestedFile.getFile(0), imageData));
	}

	private static List<FileImageData> exportDiagramsSequence(SequenceDiagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		final List<FileImageData> result = new ArrayList<FileImageData>();
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

	static private List<FileImageData> exportDiagramsCuca(CucaDiagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		if (suggestedFile.getFile(0).exists() && suggestedFile.getFile(0).isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}

		if (fileFormat.getFileFormat() == FileFormat.HTML) {
			return createFilesHtml(system, suggestedFile);
		}

		ImageData cmap = null;
		OutputStream os = null;
		try {
			if (PSystemUtils.canFileBeWritten(suggestedFile.getFile(0)) == false) {
				return Collections.emptyList();
			}
			// System.err.println("FOO11=" + suggestedFile);
			// os = SecurityUtils.BufferedOutputStream(suggestedFile));
			os = new NamedOutputStream(suggestedFile.getFile(0));
			cmap = system.exportDiagram(os, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		List<SFile> result = Arrays.asList(suggestedFile.getFile(0));

		if (cmap != null && cmap.containsCMapData()) {
			system.exportCmap(suggestedFile, 0, cmap);
		}

		if (fileFormat.getFileFormat() == FileFormat.PNG) {
			result = new PngSplitter(suggestedFile, system.getHorizontalPages(), system.getVerticalPages(),
					system.getMetadata(), (int) (system.getScaleCoef(fileFormat) * 96), fileFormat.isWithMetadata(),
					system.getSkinParam().getSplitParam()).getFiles();
		}
		final List<FileImageData> result2 = new ArrayList<FileImageData>();
		for (SFile f : result) {
			result2.add(new FileImageData(f, cmap));
		}
		return result2;

	}

	// static private List<FileImageData> exportDiagramsGantt1(GanttDiagram system,
	// SuggestedFile suggestedFile,
	// FileFormatOption fileFormat) throws IOException {
	// if (suggestedFile.getFile(0).exists() &&
	// suggestedFile.getFile(0).isDirectory()) {
	// throw new IllegalArgumentException("File is a directory " + suggestedFile);
	// }
	// OutputStream os = null;
	// ImageData imageData = null;
	// try {
	// if (PSystemUtils.canFileBeWritten(suggestedFile.getFile(0)) == false) {
	// return Collections.emptyList();
	// }
	// os = SecurityUtils.BufferedOutputStream(suggestedFile.getFile(0)));
	// imageData = system.exportDiagram(os, 0, fileFormat);
	// } finally {
	// if (os != null) {
	// os.close();
	// }
	// }
	// return Arrays.asList(new FileImageData(suggestedFile.getFile(0), imageData));
	// }

	static private List<FileImageData> exportDiagramsGantt2(GanttDiagram system, SuggestedFile suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		if (suggestedFile.getFile(0).exists() && suggestedFile.getFile(0).isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}

		ImageData cmap = null;
		OutputStream os = null;
		try {
			if (PSystemUtils.canFileBeWritten(suggestedFile.getFile(0)) == false) {
				return Collections.emptyList();
			}
			os = new NamedOutputStream(suggestedFile.getFile(0));
			cmap = system.exportDiagram(os, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		List<SFile> result = Arrays.asList(suggestedFile.getFile(0));

		if (fileFormat.getFileFormat() == FileFormat.PNG) {
			final SplitParam splitParam = new SplitParam(HColorUtils.BLACK, null, 5);
			result = new PngSplitter(suggestedFile, system.getHorizontalPages(), system.getVerticalPages(),
					system.getMetadata(), system.getDpi(fileFormat), fileFormat.isWithMetadata(), splitParam)
							.getFiles();
		}
		final List<FileImageData> result2 = new ArrayList<FileImageData>();
		for (SFile f : result) {
			result2.add(new FileImageData(f, cmap));
		}
		return result2;

	}

	private static List<FileImageData> createFilesHtml(CucaDiagram system, SuggestedFile suggestedFile)
			throws IOException {
		final String name = suggestedFile.getName();
		final int idx = name.lastIndexOf('.');
		final SFile dir = suggestedFile.getParentFile().file(name.substring(0, idx));
		final CucaDiagramHtmlMaker maker = new CucaDiagramHtmlMaker(system, dir);
		return maker.create();
	}

}
