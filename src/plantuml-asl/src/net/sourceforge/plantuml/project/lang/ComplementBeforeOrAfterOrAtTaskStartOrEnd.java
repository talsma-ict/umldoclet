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
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.core.Moment;
import net.sourceforge.plantuml.project.core.TaskAttribute;
import net.sourceforge.plantuml.project.core.TaskInstant;

public class ComplementBeforeOrAfterOrAtTaskStartOrEnd implements ComplementPattern {

	public IRegex toRegex(String suffix) {
		return new RegexLeaf("COMPLEMENT" + suffix,
				"(?:at|with|after|(\\d+)[%s]+days?[%s]+(before|after))[%s]+\\[([^\\[\\]]+?)\\].?s[%s]+(start|end)");
	}

	public Failable<Complement> getComplement(GanttDiagram system, RegexResult arg, String suffix) {
		final String code = arg.get("COMPLEMENT" + suffix, 2);
		final String position = arg.get("COMPLEMENT" + suffix, 3);
		final Moment task = system.getExistingMoment(code);
		if (task == null) {
			return Failable.<Complement> error("No such task " + code);
		}
		final String days = arg.get("COMPLEMENT" + suffix, 0);
		TaskInstant result = new TaskInstant(task, TaskAttribute.fromString(position));
		if (days != null) {
			int delta = Integer.parseInt(days);
			if ("before".equalsIgnoreCase(arg.get("COMPLEMENT" + suffix, 1))) {
				delta = -delta;
			}
			result = result.withDelta(delta);
		}
		return Failable.<Complement> ok(result);
	}
}
