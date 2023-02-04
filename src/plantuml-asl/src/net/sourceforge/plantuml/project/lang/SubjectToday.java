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

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.Today;
import net.sourceforge.plantuml.project.time.Day;

public class SubjectToday implements Subject {

	public static final Subject ME = new SubjectToday();

	private SubjectToday() {
	}

	public IRegex toRegex() {
		return new RegexConcat( //
				new RegexLeaf("today") //
		);
	}

	public Failable<Today> getMe(GanttDiagram project, RegexResult arg) {
		return Failable.ok(new Today());
	}

	public Collection<? extends SentenceSimple> getSentences() {
		return Arrays.asList(new InColor(), new IsDate());
	}

	class InColor extends SentenceSimple {

		public InColor() {
			super(SubjectToday.this, Verbs.isColored, new ComplementInColors());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final Today task = (Today) subject;
			final CenterBorderColor colors = (CenterBorderColor) complement;
			project.setTodayColors(colors);
			return CommandExecutionResult.ok();

		}

	}

	class IsDate extends SentenceSimple {

		public IsDate() {
			super(SubjectToday.this, Verbs.is, new ComplementDate());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final Day date = (Day) complement;
			return project.setToday(date);
		}

	}

}
