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

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;

public class SentenceAnd implements Sentence {

	private final SentenceSimple sentence1;
	private final SentenceSimple sentence2;

	public SentenceAnd(SentenceSimple sentence1, SentenceSimple sentence2) {
		this.sentence1 = sentence1;
		this.sentence2 = sentence2;
	}

	public IRegex toRegex() {
		return new RegexConcat(//
				RegexLeaf.start(), //
				sentence1.subjectii.toRegex(), //
				RegexLeaf.spaceOneOrMore(), //
				sentence1.getVerbRegex(), //
				RegexLeaf.spaceOneOrMore(), //
				sentence1.complementii.toRegex("1"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("and"), //
				RegexLeaf.spaceOneOrMore(), //
				sentence2.getVerbRegex(), //
				RegexLeaf.spaceOneOrMore(), //
				sentence2.complementii.toRegex("2"), //
				RegexLeaf.end());
	}

	public final CommandExecutionResult execute(GanttDiagram project, RegexResult arg) {
		final Failable<? extends Object> subject = sentence1.subjectii.getMe(project, arg);
		if (subject.isFail()) {
			return CommandExecutionResult.error(subject.getError());
		}
		final Failable<? extends Object> complement1 = sentence1.complementii.getMe(project, arg, "1");
		if (complement1.isFail()) {
			return CommandExecutionResult.error(complement1.getError());
		}
		final CommandExecutionResult result1 = sentence1.execute(project, subject.get(), complement1.get());
		if (result1.isOk() == false) {
			return result1;
		}
		final Failable<? extends Object> complement2 = sentence2.complementii.getMe(project, arg, "2");
		if (complement2.isFail()) {
			return CommandExecutionResult.error(complement2.getError());
		}
		return sentence2.execute(project, subject.get(), complement2.get());

	}

}
