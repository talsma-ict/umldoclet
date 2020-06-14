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
package net.sourceforge.plantuml.preproc2;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.DefinitionsContainer;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.DefinesGet;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc.IfManagerFilter;
import net.sourceforge.plantuml.preproc.ImportedFiles;
import net.sourceforge.plantuml.preproc.ReadLine;
import net.sourceforge.plantuml.preproc.ReadLineNumbered;

public class Preprocessor implements ReadLineNumbered {

	private final ReadLine source;
	private final PreprocessorInclude include;
	private final PreprocessorModeSet mode;
	private final ReadLine sourceV2;

	public Preprocessor(List<String> config, ReadLine reader, String charset, Defines defines,
			DefinitionsContainer definitionsContainer, ImportedFiles importedFiles) throws IOException {
		this(config, reader, charset, new DefinesGet(defines), definitionsContainer, new HashSet<FileWithSuffix>(),
				importedFiles, true);
	}

	Preprocessor(List<String> config, ReadLine reader, String charset, DefinesGet defines,
			DefinitionsContainer definitionsContainer, Set<FileWithSuffix> filesUsedGlobal,
			ImportedFiles importedFiles, boolean doSaveState) throws IOException {
		this.mode = definitionsContainer;
		if (doSaveState) {
			defines.saveState();
		}
		final ReadFilterAnd filtersV2 = new ReadFilterAnd();
		filtersV2.add(new ReadLineQuoteComment(true));
		filtersV2.add(new ReadLineAddConfig(config));
		this.sourceV2 = filtersV2.applyFilter(reader);

		final ReadFilterAnd filters = new ReadFilterAnd();
		filters.add(new ReadLineQuoteComment(false));
		include = new PreprocessorInclude(config, charset, defines, definitionsContainer, importedFiles,
				filesUsedGlobal);
		filters.add(new ReadLineAddConfig(config));
		filters.add(new IfManagerFilter(defines));
		filters.add(new PreprocessorDefineApply(defines));
		filters.add(new SubPreprocessor(charset, definitionsContainer));
		filters.add(new PreprocessorDefineLearner(defines, importedFiles.getCurrentDir()));
		filters.add(include);

		this.source = filters.applyFilter(reader);
	}

	private boolean isV2() {
		return mode != null && mode.getPreprocessorMode() == PreprocessorMode.V2_NEW_TIM;
	}

	public StringLocated readLine() throws IOException {
		if (isV2()) {
			return sourceV2.readLine();
		}
		return source.readLine();
	}

	public void close() throws IOException {
		this.source.close();
	}

	public Set<FileWithSuffix> getFilesUsed() {
		// System.err.println("************************** WARNING **************************");
		// return Collections.emptySet();
		return Collections.unmodifiableSet(include.getFilesUsedGlobal());
	}
}
