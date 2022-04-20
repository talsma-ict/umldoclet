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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sourceforge.plantuml.preproc.FileWithSuffix;

public class DirWatcher2 {

	final private File dir;
	final private Option option;
	final private String pattern;

	final private Map<File, FileWatcher> modifieds = new ConcurrentHashMap<File, FileWatcher>();
	final private ExecutorService executorService;

	public DirWatcher2(File dir, Option option, String pattern) {
		this.dir = dir;
		this.option = option;
		this.pattern = pattern;
		final int nb = Option.defaultNbThreads();
		this.executorService = Executors.newFixedThreadPool(nb);

	}

	public Map<File, Future<List<GeneratedImage>>> buildCreatedFiles() throws IOException, InterruptedException {
		final Map<File, Future<List<GeneratedImage>>> result = new TreeMap<File, Future<List<GeneratedImage>>>();
		if (dir.listFiles() != null)
			for (final File f : dir.listFiles()) {
				if (f.isFile() == false)
					continue;

				if (fileToProcess(f.getName()) == false)
					continue;

				final FileWatcher watcher = modifieds.get(f);

				if (watcher == null || watcher.hasChanged()) {
					final SourceFileReader sourceFileReader = new SourceFileReader(option.getDefaultDefines(f), f,
							option.getOutputDir(), option.getConfig(), option.getCharset(),
							option.getFileFormatOption());
					modifieds.put(f, new FileWatcher(Collections.singleton(f)));
					final Future<List<GeneratedImage>> value = executorService
							.submit(new Callable<List<GeneratedImage>>() {
								public List<GeneratedImage> call() throws Exception {
									try {
										final List<GeneratedImage> generatedImages = sourceFileReader
												.getGeneratedImages();
										final Set<File> files = FileWithSuffix
												.convert(sourceFileReader.getIncludedFiles());
										files.add(f);
										modifieds.put(f, new FileWatcher(files));
										return Collections.unmodifiableList(generatedImages);
									} catch (Exception e) {
										e.printStackTrace();
										return Collections.emptyList();
									}
								}
							});
					result.put(f, value);
				}
			}
		return Collections.unmodifiableMap(result);
	}

	private boolean fileToProcess(String name) {
		return name.matches(pattern);
	}

	public final File getDir() {
		return dir;
	}

	public void cancel() {
		executorService.shutdownNow();
	}

	public void waitEnd() throws InterruptedException {
		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

}
