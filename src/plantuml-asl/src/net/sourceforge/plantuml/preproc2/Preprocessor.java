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
package net.sourceforge.plantuml.preproc2;

import java.io.IOException;
import java.util.List;

import net.sourceforge.plantuml.preproc.ReadLine;
import net.sourceforge.plantuml.preproc.ReadLineNumbered;
import net.sourceforge.plantuml.utils.StringLocated;

public class Preprocessor implements ReadLineNumbered {

	private final ReadLine source;

	public Preprocessor(List<String> config, ReadLine reader) throws IOException {
		final ReadFilterAnd filters = new ReadFilterAnd();
		// filters.add(new ReadLineQuoteComment(true));
		filters.add(new ReadFilterAddConfig(config));
		filters.add(new ReadFilterMergeLines());
		this.source = filters.applyFilter(reader);
	}

	public StringLocated readLine() throws IOException {
		return source.readLine();
	}

	public void close() throws IOException {
		this.source.close();
	}

//	public Set<FileWithSuffix> getFilesUsedTOBEREMOVED() {
//		// System.err.println("************************** WARNING **************************");
//		return Collections.emptySet();
//		// return Collections.unmodifiableSet(include.getFilesUsedGlobal());
//	}
}
