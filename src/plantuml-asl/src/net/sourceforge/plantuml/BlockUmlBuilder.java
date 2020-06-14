/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc.ImportedFiles;
import net.sourceforge.plantuml.preproc.PreprocessorChangeModeReader;
import net.sourceforge.plantuml.preproc.ReadLineNumbered;
import net.sourceforge.plantuml.preproc.ReadLineReader;
import net.sourceforge.plantuml.preproc.UncommentReadLine;
import net.sourceforge.plantuml.preproc2.Preprocessor;
import net.sourceforge.plantuml.preproc2.PreprocessorMode;
import net.sourceforge.plantuml.utils.StartUtils;

public final class BlockUmlBuilder implements DefinitionsContainer {

	private PreprocessorMode mode = PreprocessorMode.V2_NEW_TIM;

	private final List<BlockUml> blocks = new ArrayList<BlockUml>();
	private Set<FileWithSuffix> usedFiles = new HashSet<FileWithSuffix>();
	private final UncommentReadLine reader2;
	private final Defines defines;
	private final ImportedFiles importedFiles;
	private final String charset;

	public BlockUmlBuilder(List<String> config, String charset, Defines defines, Reader reader, File newCurrentDir,
			String desc) throws IOException {
		ReadLineNumbered includer = null;
		this.defines = defines;
		this.charset = charset;
		try {
			this.reader2 = new UncommentReadLine(new PreprocessorChangeModeReader(ReadLineReader.create(reader, desc),
					this));
			this.importedFiles = ImportedFiles.createImportedFiles(new AParentFolderRegular(newCurrentDir));
			includer = new Preprocessor(config, reader2, charset, defines, this, importedFiles);
			init(includer);
		} finally {
			if (includer != null) {
				includer.close();
				usedFiles = includer.getFilesUsed();
			}
		}
	}

	public BlockUmlBuilder(List<String> config, String charset, Defines defines, Reader reader) throws IOException {
		this(config, charset, defines, reader, null, null);
	}

	private void init(ReadLineNumbered includer) throws IOException {
		StringLocated s = null;
		List<StringLocated> current2 = null;
		boolean paused = false;

		while ((s = includer.readLine()) != null) {
			if (StartUtils.isArobaseStartDiagram(s.getString())) {
				current2 = new ArrayList<StringLocated>();
				paused = false;
			}
			if (StartUtils.isArobasePauseDiagram(s.getString())) {
				paused = true;
				reader2.setPaused(true);
			}
			if (StartUtils.isExit(s.getString())) {
				paused = true;
				reader2.setPaused(true);
			}
			if (current2 != null && paused == false) {
				current2.add(s);
			} else if (paused) {
				final StringLocated append = StartUtils.getPossibleAppend(s);
				if (append != null) {
					current2.add(append);
				}
			}

			if (StartUtils.isArobaseUnpauseDiagram(s.getString())) {
				paused = false;
				reader2.setPaused(false);
			}
			if (StartUtils.isArobaseEndDiagram(s.getString()) && current2 != null) {
				if (paused) {
					current2.add(s);
				}
				blocks.add(new BlockUml(current2, defines.cloneMe(), null, this));
				current2 = null;
				reader2.setPaused(false);
			}
		}
	}

	public List<BlockUml> getBlockUmls() {
		return Collections.unmodifiableList(blocks);
	}

	public final Set<FileWithSuffix> getIncludedFiles() {
		return Collections.unmodifiableSet(usedFiles);
	}

	public List<String> getDefinition1(String name) {
		for (BlockUml block : blocks) {
			if (block.isStartDef(name)) {
				this.defines.importFrom(block.getLocalDefines());
				return block.getDefinition(false);
			}
		}
		return Collections.emptyList();
	}

	public List<String> getDefinition2(String name) {
		for (BlockUml block : blocks) {
			if (block.isStartDef(name)) {
				return block.getDefinition2(false);
			}
		}
		return Collections.emptyList();
	}

	public PreprocessorMode getPreprocessorMode() {
		return mode;
	}

	public void setPreprocessorMode(PreprocessorMode mode) {
		this.mode = mode;
	}

	public final ImportedFiles getImportedFiles() {
		return importedFiles;
	}

	public final String getCharset() {
		return charset;
	}

}
