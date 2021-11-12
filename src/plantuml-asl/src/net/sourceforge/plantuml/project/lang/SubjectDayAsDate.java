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
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class SubjectDayAsDate implements Subject {

	public Failable<Day> getMe(GanttDiagram project, RegexResult arg) {
		if (arg.get("BDAY", 0) != null) {
			return Failable.ok(resultB(arg));
		}
		if (arg.get("ECOUNT", 0) != null) {
			return Failable.ok(resultE(project, arg));
		}
		throw new IllegalStateException();

	}

	private Day resultB(RegexResult arg) {
		final int day = Integer.parseInt(arg.get("BDAY", 0));
		final int month = Integer.parseInt(arg.get("BMONTH", 0));
		final int year = Integer.parseInt(arg.get("BYEAR", 0));
		return Day.create(year, month, day);
	}

	private Day resultE(GanttDiagram system, RegexResult arg) {
		final int day = Integer.parseInt(arg.get("ECOUNT", 0));
		return system.getStartingDate().addDays(day);
	}

	public Collection<? extends SentenceSimple> getSentences() {
		return Arrays.asList(new Close(), new Open(), new InColor());
	}

	class Close extends SentenceSimple {

		public Close() {
			super(SubjectDayAsDate.this, Verbs.isOrAre(), new ComplementClose());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			project.closeDayAsDate((Day) subject);
			return CommandExecutionResult.ok();
		}
	}

	class Open extends SentenceSimple {
		public Open() {
			super(SubjectDayAsDate.this, Verbs.isOrAre(), new ComplementOpen());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			project.openDayAsDate((Day) subject);
			return CommandExecutionResult.ok();
		}
	}

	class InColor extends SentenceSimple {

		public InColor() {
			super(SubjectDayAsDate.this, Verbs.isOrAre(), new ComplementInColors2());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final HColor color = ((CenterBorderColor) complement).getCenter();
			project.colorDay((Day) subject, color);
			return CommandExecutionResult.ok();
		}

	}

	public IRegex toRegex() {
		return new RegexOr(toRegexB(), toRegexE());
	}

	private IRegex toRegexB() {
		return new RegexConcat( //
				new RegexLeaf("BYEAR", "([\\d]{4})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BMONTH", "([\\d]{1,2})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BDAY", "([\\d]{1,2})"));
	}

	private IRegex toRegexE() {
		return new RegexConcat( //
				new RegexLeaf("[dD]\\+"), //
				new RegexLeaf("ECOUNT", "([\\d]+)") //
		);
	}

}
