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
package net.sourceforge.plantuml.tim;

import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.DefinitionsContainer;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc.ImportedFiles;

public class TimLoader {

	private final TContext context;
	private final TMemory global = new TMemoryGlobal();
	private boolean preprocessorError;
	private List<StringLocated> resultList;

	public TimLoader(ImportedFiles importedFiles, Defines defines, String charset,
			DefinitionsContainer definitionsContainer) {
		this.context = new TContext(importedFiles, defines, charset, definitionsContainer);
		try {
			defines.copyTo(global);
		} catch (EaterException e) {
			e.printStackTrace();
		}
	}

	public Set<FileWithSuffix> load(List<StringLocated> list) {
//		CodeIteratorImpl.indentNow(list);
		try {
			context.executeLines(global, list, null, false);
		} catch (EaterExceptionLocated e) {
			context.getResultList().add(e.getLocation().withErrorPreprocessor(e.getMessage()));
			changeLastLine(context.getDebug(), e.getMessage());
			this.preprocessorError = true;
		}
		this.resultList = context.getResultList();
		return context.getFilesUsedCurrent();
	}

	private void changeLastLine(List<StringLocated> list, String message) {
		final int num = list.size() - 1;
		final StringLocated last = list.get(num);
		list.set(num, last.withErrorPreprocessor(message));
	}

	public final List<StringLocated> getResultList() {
		return resultList;
	}

	public final List<StringLocated> getDebug() {
		return context.getDebug();
	}

	public final boolean isPreprocessorError() {
		return preprocessorError;
	}

}
