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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc.ImportedFiles;
import net.sourceforge.plantuml.preproc.ReadLineNumbered;
import net.sourceforge.plantuml.preproc.ReadLineReader;
import net.sourceforge.plantuml.preproc.UncommentReadLine;
import net.sourceforge.plantuml.preproc2.Preprocessor;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.utils.StartUtils;

public final class BlockUmlBuilder implements DefinitionsContainer {

	private final List<BlockUml> blocks = new ArrayList<>();
	private Set<FileWithSuffix> usedFiles = new HashSet<>();
	private final UncommentReadLine reader;
	private final Defines defines;
	private final ImportedFiles importedFiles;
	private final String charset;

	public BlockUmlBuilder(List<String> config, String charset, Defines defines, Reader readerInit, SFile newCurrentDir,
			String desc) throws IOException {
		ReadLineNumbered includer = null;
		this.defines = defines;
		this.charset = charset;
		try {
			this.reader = new UncommentReadLine(ReadLineReader.create(readerInit, desc));
			this.importedFiles = ImportedFiles.createImportedFiles(new AParentFolderRegular(newCurrentDir));
			includer = new Preprocessor(config, reader);
			init(includer);
		} finally {
			if (includer != null) {
				includer.close();
				// usedFiles = includer.getFilesUsedTOBEREMOVED();
			}
			readerInit.close();
		}
	}

	public BlockUmlBuilder(List<String> config, String charset, Defines defines, Reader reader) throws IOException {
		this(config, charset, defines, reader, null, null);
	}

	private void init(ReadLineNumbered includer) throws IOException {
		StringLocated s = null;
		List<StringLocated> current = null;
		boolean paused = false;

		while ((s = includer.readLine()) != null) {
			if (StartUtils.isArobaseStartDiagram(s.getString())) {
				current = new ArrayList<>();
				paused = false;
			}
			if (StartUtils.isArobasePauseDiagram(s.getString())) {
				paused = true;
				reader.setPaused(true);
			}
			if (StartUtils.isExit(s.getString())) {
				paused = true;
				reader.setPaused(true);
			}
			if (current != null && paused == false) {
				current.add(s);
			} else if (paused) {
				final StringLocated append = StartUtils.getPossibleAppend(s);
				if (append != null) {
					current.add(append);
				}
			}

			if (StartUtils.isArobaseUnpauseDiagram(s.getString())) {
				paused = false;
				reader.setPaused(false);
			}
			if (StartUtils.isArobaseEndDiagram(s.getString()) && current != null) {
				if (paused) {
					current.add(s);
				}
				final BlockUml uml = new BlockUml(current, defines.cloneMe(), null, this);
				usedFiles.addAll(uml.getIncluded());
				blocks.add(uml);
				current = null;
				reader.setPaused(false);
			}
		}
	}

	public List<BlockUml> getBlockUmls() {
		return Collections.unmodifiableList(blocks);
	}

	public final Set<FileWithSuffix> getIncludedFiles() {
		return Collections.unmodifiableSet(usedFiles);
	}

	public List<String> getDefinition(String name) {
		for (BlockUml block : blocks) {
			if (block.isStartDef(name)) {
				return block.getDefinition(false);
			}
		}
		return Collections.emptyList();
	}

	public final ImportedFiles getImportedFiles() {
		return importedFiles;
	}

	public final String getCharset() {
		return charset;
	}

}
