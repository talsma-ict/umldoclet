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
package net.sourceforge.plantuml.project.lang;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;

public abstract class SentenceSimple implements Sentence {

	protected final Subject subjectii;
	private final IRegex verb;
	protected final Something complementii;

	public SentenceSimple(Subject subject, IRegex verb, Something complement) {
		this.subjectii = subject;
		this.verb = verb;
		this.complementii = complement;
	}

	public final IRegex toRegex() {
		if (complementii instanceof ComplementEmpty)
			return new RegexConcat(//
					RegexLeaf.start(), //
					subjectii.toRegex(), //
					RegexLeaf.spaceOneOrMore(), //
					verb, //
					RegexLeaf.end());

		return new RegexConcat(//
				RegexLeaf.start(), //
				subjectii.toRegex(), //
				RegexLeaf.spaceOneOrMore(), //
				verb, //
				RegexLeaf.spaceOneOrMore(), //
				complementii.toRegex("0"), //
				RegexLeaf.end());
	}

	public final CommandExecutionResult execute(GanttDiagram project, RegexResult arg) {
		final Failable<? extends Object> subject = subjectii.getMe(project, arg);
		if (subject.isFail())
			return CommandExecutionResult.error(subject.getError());

		final Failable<? extends Object> complement = complementii.getMe(project, arg, "0");
		if (complement.isFail())
			return CommandExecutionResult.error(complement.getError());

		return execute(project, subject.get(), complement.get());

	}

	public abstract CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement);

	public final String getVerbPattern() {
		return verb.getPattern();
	}

	public IRegex getVerbRegex() {
		return verb;
	}

}
