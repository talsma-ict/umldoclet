/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.project3;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;

public class VerbIsOff implements VerbPattern {

	public Collection<ComplementPattern> getComplements() {
		return Arrays.<ComplementPattern> asList(new ComplementDate(), new ComplementDates());
	}

	public IRegex toRegex() {
		return new RegexConcat(new RegexLeaf("is"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("off"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("on"));
	}

	public Verb getVerb(final GanttDiagram project, RegexResult arg) {
		return new Verb() {
			public CommandExecutionResult execute(Subject subject, Complement complement) {
				final Resource resource = (Resource) subject;
				if (complement instanceof DaysAsDates) {
					for (DayAsDate when : (DaysAsDates) complement) {
						resource.addCloseDay(project.convert(when));
					}
				} else {
					final DayAsDate when = (DayAsDate) complement;
					resource.addCloseDay(project.convert(when));
				}
				return CommandExecutionResult.ok();
			}

		};
	}

}
