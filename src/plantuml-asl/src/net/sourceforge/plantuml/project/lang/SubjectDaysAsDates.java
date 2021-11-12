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
import net.sourceforge.plantuml.project.DaysAsDates;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class SubjectDaysAsDates implements Subject {

	public IRegex toRegex() {
		return new RegexOr(toRegexB(), toRegexE(), andRegex(), thenRegex());
	}

	private IRegex toRegexB() {
		return new RegexConcat( //
				new RegexLeaf("BYEAR1", "([\\d]{4})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BMONTH1", "([\\d]{1,2})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BDAY1", "([\\d]{1,2})"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("to"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("BYEAR2", "([\\d]{4})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BMONTH2", "([\\d]{1,2})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BDAY2", "([\\d]{1,2})") //
		);
	}

	private IRegex toRegexE() {
		return new RegexConcat( //
				new RegexLeaf("[dD]\\+"), //
				new RegexLeaf("ECOUNT1", "([\\d]+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("to"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("[dD]\\+"), //
				new RegexLeaf("ECOUNT2", "([\\d]+)") //
		);
	}

	private IRegex andRegex() {
		return new RegexConcat( //
				new RegexLeaf("BYEAR3", "([\\d]{4})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BMONTH3", "([\\d]{1,2})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BDAY3", "([\\d]{1,2})"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("and"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("COUNT_AND", "([\\d]+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("days?") //

		);
	}

	private IRegex thenRegex() {
		return new RegexConcat( //
				new RegexLeaf("then"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("COUNT_THEN", "([\\d]+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("days?") //

		);
	}

	public Failable<DaysAsDates> getMe(GanttDiagram project, RegexResult arg) {
		final String countAnd = arg.get("COUNT_AND", 0);
		if (countAnd != null) {
			final Day date3 = getDate(project, arg, "3");
			final int nb = Integer.parseInt(countAnd);
			return Failable.ok(new DaysAsDates(project, date3, nb));
		}
		final String countThen = arg.get("COUNT_THEN", 0);
		if (countThen != null) {
			final Day date3 = project.getThenDate();
			final int nb = Integer.parseInt(countThen);
			return Failable.ok(new DaysAsDates(project, date3, nb));
		}
		final Day date1 = getDate(project, arg, "1");
		final Day date2 = getDate(project, arg, "2");
		return Failable.ok(new DaysAsDates(date1, date2));
	}

	private Day getDate(GanttDiagram project, RegexResult arg, String suffix) {
		if (arg.get("BDAY" + suffix, 0) != null) {
			final int day = Integer.parseInt(arg.get("BDAY" + suffix, 0));
			final int month = Integer.parseInt(arg.get("BMONTH" + suffix, 0));
			final int year = Integer.parseInt(arg.get("BYEAR" + suffix, 0));
			return Day.create(year, month, day);
		}
		if (arg.get("ECOUNT" + suffix, 0) != null) {
			final int day = Integer.parseInt(arg.get("ECOUNT" + suffix, 0));
			return project.getStartingDate().addDays(day);
		}
		throw new IllegalStateException();
	}

	public Collection<? extends SentenceSimple> getSentences() {
		return Arrays.asList(new Close(), new Open(), new InColor(), new Named());
	}

	class Close extends SentenceSimple {

		public Close() {
			super(SubjectDaysAsDates.this, Verbs.isOrAre(), new ComplementClose());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			for (Day d : (DaysAsDates) subject) {
				project.closeDayAsDate(d);
			}
			return CommandExecutionResult.ok();

		}
	}

	class Open extends SentenceSimple {

		public Open() {
			super(SubjectDaysAsDates.this, Verbs.isOrAre(), new ComplementOpen());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			for (Day d : (DaysAsDates) subject) {
				project.openDayAsDate(d);
			}
			return CommandExecutionResult.ok();

		}

	}

	class InColor extends SentenceSimple {

		public InColor() {
			super(SubjectDaysAsDates.this, Verbs.isOrAre(), new ComplementInColors2());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final HColor color = ((CenterBorderColor) complement).getCenter();
			for (Day d : (DaysAsDates) subject) {
				project.colorDay(d, color);
			}
			return CommandExecutionResult.ok();

		}

	}

	class Named extends SentenceSimple {

		public Named() {
			super(SubjectDaysAsDates.this, Verbs.isOrAreNamed(), new ComplementNamed());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final String name = (String) complement;
			final DaysAsDates days = (DaysAsDates) subject;
			for (Day d : days) {
				project.nameDay(d, name);
			}
			return CommandExecutionResult.ok();
		}

	}

}
