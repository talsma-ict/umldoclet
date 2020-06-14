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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AParentFolder;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.preproc.DefinesGet;
import net.sourceforge.plantuml.preproc.ReadLine;
import net.sourceforge.plantuml.utils.StartUtils;

public class PreprocessorDefineLearner implements ReadFilter {

	private static final String END_DEFINE_LONG = "!enddefinelong";
	private static final String ID = "[A-Za-z_][A-Za-z_0-9]*";
	private static final String ID_ARG = "\\s*[A-Za-z_][A-Za-z_0-9]*\\s*(?:=\\s*(?:\"[^\"]*\"|'[^']*')\\s*)?";
	private static final String ARG = "(?:\\(" + ID_ARG + "(?:," + ID_ARG + ")*?\\))?";
	private static final Pattern2 defineShortPattern = MyPattern.cmpile("^[%s]*!define[%s]+(" + ID + ARG + ")"
			+ "(?:[%s]+(.*))?$");
	private static final Pattern2 filenamePattern = MyPattern.cmpile("^[%s]*!filename[%s]+(.+)$");
	private static final Pattern2 undefPattern = MyPattern.cmpile("^[%s]*!undef[%s]+(" + ID + ")$");
	private static final Pattern2 definelongPattern = MyPattern.cmpile("^[%s]*!definelong[%s]+(" + ID + ARG + ")");
	private static final Pattern2 enddefinelongPattern = MyPattern.cmpile("^[%s]*" + END_DEFINE_LONG + "[%s]*$");

	private final DefinesGet defines;
	private final AParentFolder currentDir;

	public PreprocessorDefineLearner(DefinesGet defines, AParentFolder currentDir) {
		this.defines = defines;
		this.currentDir = currentDir;
	}

	public static boolean isLearningLine(StringLocated s) {
		Matcher2 m = defineShortPattern.matcher(s.getString());
		if (m.find()) {
			return true;
		}
		m = definelongPattern.matcher(s.getString());
		if (m.find()) {
			return true;
		}
		m = undefPattern.matcher(s.getString());
		if (m.find()) {
			return true;
		}
		return false;
	}

	public ReadLine applyFilter(final ReadLine source) {
		return new ReadLine() {

			public void close() throws IOException {
				source.close();
			}

			public StringLocated readLine() throws IOException {
				while (true) {
					final StringLocated s = source.readLine();
					if (s == null || s.getPreprocessorError() != null) {
						return s;
					}
					if (StartUtils.isArobaseStartDiagram(s.getString())) {
						defines.restoreState();
						return s;
					}

					Matcher2 m = filenamePattern.matcher(s.getString());
					if (m.find()) {
						manageFilename(m);
						continue;
					}
					m = defineShortPattern.matcher(s.getString());
					if (m.find()) {
						manageDefineShort(source, m, s.getString().trim().endsWith("()"));
						continue;
					}
					m = definelongPattern.matcher(s.getString());
					if (m.find()) {
						manageDefineLong(source, m, s.getString().trim().endsWith("()"));
						continue;
					}

					m = undefPattern.matcher(s.getString());
					if (m.find()) {
						manageUndef(m);
						continue;
					}
					return s;
				}
			}
		};
	}

	private void manageUndef(Matcher2 m) throws IOException {
		defines.get().undefine(m.group(1));
	}

	private void manageDefineLong(ReadLine source, Matcher2 m, boolean emptyParentheses) throws IOException {
		final String group1 = m.group(1);
		final List<String> def = new ArrayList<String>();
		while (true) {
			final StringLocated read = source.readLine();
			if (read == null) {
				return;
			}
			if (enddefinelongPattern.matcher(read.getString()).find()) {
				defines.get().define(group1, def, emptyParentheses, currentDir);
				return;
			}
			def.add(read.getString());
		}
	}

	private void manageFilename(Matcher2 m) {
		final String group1 = m.group(1);
		this.defines.get().overrideFilename(group1);
	}

	private void manageDefineShort(ReadLine source, Matcher2 m, boolean emptyParentheses) throws IOException {
		final String group1 = m.group(1);
		final String group2 = m.group(2);
		if (group2 == null) {
			defines.get().define(group1, null, emptyParentheses, null);
		} else {
			final List<String> strings = defines.get().applyDefines(group2);
			if (strings.size() > 1) {
				defines.get().define(group1, strings, emptyParentheses, null);
			} else {
				final StringBuilder value = new StringBuilder(strings.get(0));
				while (StringUtils.endsWithBackslash(value.toString())) {
					value.setLength(value.length() - 1);
					final StringLocated read = source.readLine();
					value.append(read.getString());
				}
				final List<String> li = new ArrayList<String>();
				li.add(value.toString());
				defines.get().define(group1, li, emptyParentheses, null);
			}
		}
	}

}
