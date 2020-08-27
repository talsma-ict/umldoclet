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

import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.Month;

public class ComplementDate implements Something {

	public IRegex toRegex(String suffix) {
		return new RegexOr(toRegexA(suffix), toRegexB(suffix), toRegexC(suffix), toRegexD(suffix));
	}

	private IRegex toRegexA(String suffix) {
		return new RegexConcat( //
				new RegexLeaf("ADAY" + suffix, "([\\d]+)"), //
				new RegexLeaf("[\\w, ]*?"), //
				new RegexLeaf("AMONTH" + suffix, "(" + Month.getRegexString() + ")"), //
				new RegexLeaf("[\\w, ]*?"), //
				new RegexLeaf("AYEAR" + suffix, "([\\d]{4})"));
	}

	private IRegex toRegexB(String suffix) {
		return new RegexConcat( //
				new RegexLeaf("BYEAR" + suffix, "([\\d]{4})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BMONTH" + suffix, "([\\d]{1,2})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("BDAY" + suffix, "([\\d]{1,2})"));
	}

	private IRegex toRegexC(String suffix) {
		return new RegexConcat( //
				new RegexLeaf("CMONTH" + suffix, "(" + Month.getRegexString() + ")"), //
				new RegexLeaf("[\\w, ]*?"), //
				new RegexLeaf("CDAY" + suffix, "([\\d]+)"), //
				new RegexLeaf("[\\w, ]*?"), //
				new RegexLeaf("CYEAR" + suffix, "([\\d]{4})"));
	}

	private IRegex toRegexD(String suffix) {
		return new RegexConcat( //
				new RegexLeaf("DCOUNT" + suffix, "([\\d]+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("days?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("after"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("start") //
		);
	}

	public Failable<Day> getMe(GanttDiagram system, RegexResult arg, String suffix) {
		if (arg.get("ADAY" + suffix, 0) != null) {
			return Failable.ok(resultA(arg, suffix));
		}
		if (arg.get("BDAY" + suffix, 0) != null) {
			return Failable.ok(resultB(arg, suffix));
		}
		if (arg.get("CDAY" + suffix, 0) != null) {
			return Failable.ok(resultC(arg, suffix));
		}
		if (arg.get("DCOUNT" + suffix, 0) != null) {
			return Failable.ok(resultD(system, arg, suffix));
		}
		throw new IllegalStateException();
	}

	private Day resultD(GanttDiagram system, RegexResult arg, String suffix) {
		final int day = Integer.parseInt(arg.get("DCOUNT" + suffix, 0));
		return system.getStartingDate(day);
	}

	private Day resultA(RegexResult arg, String suffix) {
		final int day = Integer.parseInt(arg.get("ADAY" + suffix, 0));
		final String month = arg.get("AMONTH" + suffix, 0);
		final int year = Integer.parseInt(arg.get("AYEAR" + suffix, 0));
		return Day.create(year, month, day);
	}

	private Day resultB(RegexResult arg, String suffix) {
		final int day = Integer.parseInt(arg.get("BDAY" + suffix, 0));
		final int month = Integer.parseInt(arg.get("BMONTH" + suffix, 0));
		final int year = Integer.parseInt(arg.get("BYEAR" + suffix, 0));
		return Day.create(year, month, day);
	}

	private Day resultC(RegexResult arg, String suffix) {
		final int day = Integer.parseInt(arg.get("CDAY" + suffix, 0));
		final String month = arg.get("CMONTH" + suffix, 0);
		final int year = Integer.parseInt(arg.get("CYEAR" + suffix, 0));
		return Day.create(year, month, day);
	}
}
