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
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.Load;

public class ComplementSeveralDays implements Something {

	public IRegex toRegex(String suffix) {
		return new RegexConcat( //
				new RegexLeaf("COMPLEMENT" + suffix, "(\\d+)[%s]+(day|week)s?" + //
						"(?:[%s]+and[%s]+(\\d+)[%s]+(day|week)s?)?" //
				)); //
	}

	public Failable<Load> getMe(GanttDiagram system, RegexResult arg, String suffix) {
		final String nb1 = arg.get("COMPLEMENT" + suffix, 0);
		final int factor1 = arg.get("COMPLEMENT" + suffix, 1).startsWith("w") ? system.daysInWeek() : 1;
		final int days1 = Integer.parseInt(nb1) * factor1;

		final String nb2 = arg.get("COMPLEMENT" + suffix, 2);
		int days2 = 0;
		if (nb2 != null) {
			final int factor2 = arg.get("COMPLEMENT" + suffix, 3).startsWith("w") ? system.daysInWeek() : 1;
			days2 = Integer.parseInt(nb2) * factor2;
		}

		return Failable.ok(Load.inWinks(days1 + days2));
	}

}
