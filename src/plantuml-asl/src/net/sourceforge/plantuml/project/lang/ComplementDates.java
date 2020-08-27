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
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.DaysAsDates;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.time.Day;

public class ComplementDates implements Something {

	public IRegex toRegex(String suffix) {
		return new RegexConcat( //
				new RegexLeaf("YEAR1" + suffix, "([\\d]{4})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("MONTH1" + suffix, "([\\d]{1,2})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("DAY1" + suffix, "([\\d]{1,2})"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("to"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("YEAR2" + suffix, "([\\d]{4})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("MONTH2" + suffix, "([\\d]{1,2})"), //
				new RegexLeaf("\\D"), //
				new RegexLeaf("DAY2" + suffix, "([\\d]{1,2})") //

		);
	}

	public Failable<DaysAsDates> getMe(GanttDiagram project, RegexResult arg, String suffix) {

		final int day1 = Integer.parseInt(arg.get("DAY1" + suffix, 0));
		final int month1 = Integer.parseInt(arg.get("MONTH1" + suffix, 0));
		final int year1 = Integer.parseInt(arg.get("YEAR1" + suffix, 0));
		final Day date1 = Day.create(year1, month1, day1);

		final int day2 = Integer.parseInt(arg.get("DAY2" + suffix, 0));
		final int month2 = Integer.parseInt(arg.get("MONTH2" + suffix, 0));
		final int year2 = Integer.parseInt(arg.get("YEAR2" + suffix, 0));
		final Day date2 = Day.create(year2, month2, day2);

		return Failable.ok(new DaysAsDates(date1, date2));
	}

}
