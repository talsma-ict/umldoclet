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
package net.sourceforge.plantuml.project.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.core.Task;

public class SubjectTask implements Subject {

	public Failable<Task> getMe(GanttDiagram project, RegexResult arg) {
		final String s = arg.get("SUBJECT", 0);
		final String shortName = arg.get("SUBJECT", 1);
		final String then = arg.get("THEN", 0);
		final String resource = arg.get("RESOURCE", 0);
		final Task result = project.getOrCreateTask(s, shortName, then != null);
		if (result == null) {
			throw new IllegalStateException();
		}
		if (resource != null) {
			for (final StringTokenizer st = new StringTokenizer(resource, "{}"); st.hasMoreTokens();) {
				final String part = st.nextToken().trim();
				if (part.length() > 0) {
					project.affectResource(result, part);
				}
			}

		}
		return Failable.ok(result);
	}

	public Collection<? extends SentenceSimple> getSentences() {
		return Arrays.asList(new SentenceLasts(), new SentenceTaskStarts(), new SentenceTaskStartsWithColor(),
				new SentenceTaskStartsAbsolute(), new SentenceHappens(), new SentenceHappensDate(), new SentenceEnds(),
				new SentenceTaskEndsAbsolute(), new SentenceIsColored(), new SentenceIsDeleted(),
				new SentenceIsForTask(), new SentenceLinksTo(), new SentenceOccurs(), new SentenceDisplayOnSameRowAs(),
				new SentencePausesDate(), new SentencePausesDayOfWeek());
	}

	public IRegex toRegex() {
		return new RegexConcat( //
				new RegexLeaf("THEN", "(then[%s]+)?"), //
				new RegexLeaf("SUBJECT", "\\[([^\\[\\]]+?)\\](?:[%s]+as[%s]+\\[([^\\[\\]]+?)\\])?"), //
				new RegexOptional( //
						new RegexConcat( //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("on"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("RESOURCE", "((?:\\{[^{}]+\\}[%s]*)+)") //
						)) //
		);
	}

}
