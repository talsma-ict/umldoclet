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

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.time.Day;

public class VerbProjectStarts implements VerbPattern {

	public Collection<ComplementPattern> getComplements() {
		return Arrays.<ComplementPattern> asList(new ComplementDate());
	}

	public IRegex toRegexOld() {
		return new RegexLeaf("starts[%s]*(the[%s]*|on[%s]*)*");
	}

	public IRegex toRegex() {
		return new RegexConcat(new RegexLeaf("start"), //
				new RegexOptional(new RegexLeaf("s")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexOr(//
						new RegexLeaf("on"),//
						new RegexLeaf("for"),//
						new RegexLeaf("the"),//
						new RegexLeaf("at") //
				)) //
		);
	}

	public Verb getVerb(final GanttDiagram project, RegexResult arg) {
		return new Verb() {
			public CommandExecutionResult execute(Subject subject, Complement complement) {
				final Day start = (Day) complement;
				assert project == subject;
				project.setStartingDate(start);
				return CommandExecutionResult.ok();
			}

		};
	}
}
