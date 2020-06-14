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
import java.util.List;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.preproc.DefinesGet;
import net.sourceforge.plantuml.preproc.ReadLine;
import net.sourceforge.plantuml.preproc.ReadLineList;

public class PreprocessorDefineApply implements ReadFilter {

	private final DefinesGet defines;

	public PreprocessorDefineApply(DefinesGet defines) throws IOException {
		this.defines = defines;
	}

	public ReadLine applyFilter(final ReadLine source) {
		return new Inner(source);
	}

	class Inner extends ReadLineInsertable {

		final ReadLine source;

		Inner(ReadLine source) {
			this.source = source;
		}

		@Override
		void closeInternal() throws IOException {
			source.close();
		}

		@Override
		StringLocated readLineInternal() throws IOException {
			final StringLocated s = this.source.readLine();
			if (s == null || s.getPreprocessorError() != null) {
				return s;
			}
			if (PreprocessorDefineLearner.isLearningLine(s)) {
				return s;
			}
			final List<String> result = defines.get().applyDefines(s.getString());
			if (result.size() > 1) {
				insert(new ReadLineList(result, s.getLocation()));
				return readLine();
			}
			String tmp = result.get(0);
			return new StringLocated(tmp, s.getLocation(), s.getPreprocessorError());
		}

	}

}
