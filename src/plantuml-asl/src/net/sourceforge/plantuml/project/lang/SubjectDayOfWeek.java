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
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.time.DayOfWeek;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class SubjectDayOfWeek implements Subject {

	public static final Subject ME = new SubjectDayOfWeek();

	private SubjectDayOfWeek() {
	}

	public IRegex toRegex() {
		return new RegexLeaf("SUBJECT", "(" + DayOfWeek.getRegexString() + ")");
	}

	public Failable<? extends Object> getMe(GanttDiagram project, RegexResult arg) {
		final String s = arg.get("SUBJECT", 0);
		return Failable.ok(DayOfWeek.fromString(s));
	}

	public Collection<? extends SentenceSimple> getSentences() {
		return Arrays.asList(new AreClose(), new AreOpen(), new InColor());
	}

	class AreOpen extends SentenceSimple {
		public AreOpen() {
			super(SubjectDayOfWeek.this, Verbs.are, new ComplementOpen());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final DayOfWeek day = (DayOfWeek) subject;
			project.openDayOfWeek(day, (String) complement);
			return CommandExecutionResult.ok();
		}
	}

	class AreClose extends SentenceSimple {

		public AreClose() {
			super(SubjectDayOfWeek.this, Verbs.are, new ComplementClose());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final DayOfWeek day = (DayOfWeek) subject;
			project.closeDayOfWeek(day, (String) complement);
			return CommandExecutionResult.ok();
		}

	}

	class InColor extends SentenceSimple {

		public InColor() {
			super(SubjectDayOfWeek.this, Verbs.isOrAre, new ComplementInColors2());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final HColor color = ((CenterBorderColor) complement).getCenter();
			final DayOfWeek day = (DayOfWeek) subject;
			project.colorDay(day, color);

			return CommandExecutionResult.ok();
		}

	}

}
