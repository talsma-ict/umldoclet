/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.swing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.Option;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.file.FileWatcher;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;

@Deprecated
public class DirWatcher {
	// ::remove file when __CORE__

	final private File dir;
	final private Option option;
	final private String pattern;

	final private Map<File, FileWatcher> modifieds = new HashMap<File, FileWatcher>();

	public DirWatcher(File dir, Option option, String pattern) {
		this.dir = dir;
		this.option = option;
		this.pattern = pattern;
	}

	public List<GeneratedImage> buildCreatedFiles() throws IOException, InterruptedException {
		boolean error = false;
		final List<GeneratedImage> result = new ArrayList<>();
		if (dir.listFiles() != null)
			for (File f : dir.listFiles()) {
				if (error)
					continue;

				if (f.isFile() == false)
					continue;

				if (fileToProcess(f.getName()) == false)
					continue;

				final FileWatcher watcher = modifieds.get(f);

				if (watcher == null || watcher.hasChanged()) {
					final SourceFileReader sourceFileReader = new SourceFileReader(Defines.createWithFileName(f), f,
							option.getOutputDir(), option.getConfig(), option.getCharset(),
							option.getFileFormatOption());
					final Set<File> files = FileWithSuffix.convert(sourceFileReader.getIncludedFiles());
					files.add(f);
					for (GeneratedImage g : sourceFileReader.getGeneratedImages()) {
						result.add(g);
						if (option.isFailfastOrFailfast2() && g.lineErrorRaw() != -1) {
							error = true;
						}
					}
					modifieds.put(f, new FileWatcher(files));
				}
			}
		Collections.sort(result);
		return Collections.unmodifiableList(result);
	}

	public File getErrorFile() throws IOException, InterruptedException {
		if (dir.listFiles() != null)
			for (File f : dir.listFiles()) {
				if (f.isFile() == false)
					continue;

				if (fileToProcess(f.getName()) == false)
					continue;

				final FileWatcher watcher = modifieds.get(f);

				if (watcher == null || watcher.hasChanged()) {
					final SourceFileReader sourceFileReader = new SourceFileReader(Defines.createWithFileName(f), f,
							option.getOutputDir(), option.getConfig(), option.getCharset(),
							option.getFileFormatOption());
					if (sourceFileReader.hasError())
						return f;

				}
			}
		return null;
	}

	private boolean fileToProcess(String name) {
		return name.matches(pattern);
	}

	public final File getDir() {
		return dir;
	}

	// public void setPattern(String pattern) {
	// this.pattern = pattern;
	// }
}
