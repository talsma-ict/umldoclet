/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
import net.sourceforge.plantuml.project.DaysAsDates;
import net.sourceforge.plantuml.project.Failable;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.core.Resource;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.DayOfWeek;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;

public class SubjectResource implements Subject {

	public static final Subject ME = new SubjectResource();

	private SubjectResource() {
	}

	public Failable<Resource> getMe(GanttDiagram project, RegexResult arg) {
		final String s = arg.get("RESOURCE", 0);
		return Failable.ok(project.getResource(s));
	}

	public Collection<? extends SentenceSimple> getSentences() {
		return Arrays.asList(new IsOffDate(), new IsOffDates(), new IsOffDayOfWeek(), new IsOnDate(), new IsOnDates());
	}

	public IRegex toRegex() {
		return new RegexConcat( //
				new RegexLeaf("RESOURCE", "\\{([^{}]+)\\}") //
		);
	}

	public class IsOffDate extends SentenceSimple {

		public IsOffDate() {
			super(SubjectResource.this, Verbs.isOff, new ComplementDate());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final Resource resource = (Resource) subject;
			final Day when = (Day) complement;
			resource.addCloseDay(when);
			return CommandExecutionResult.ok();
		}

	}

	public class IsOffDates extends SentenceSimple {

		public IsOffDates() {
			super(SubjectResource.this, Verbs.isOff, new ComplementDates());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final Resource resource = (Resource) subject;
			for (Day when : (DaysAsDates) complement) {
				resource.addCloseDay(when);
			}
			return CommandExecutionResult.ok();
		}

	}

	public class IsOffDayOfWeek extends SentenceSimple {

		public IsOffDayOfWeek() {
			super(SubjectResource.this, Verbs.isOff, new ComplementDayOfWeek());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final Resource resource = (Resource) subject;
			resource.addCloseDay(((DayOfWeek) complement));
			return CommandExecutionResult.ok();
		}

	}

	public class IsOnDate extends SentenceSimple {

		public IsOnDate() {
			super(SubjectResource.this, Verbs.isOn, new ComplementDate());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final Resource resource = (Resource) subject;
			final Day when = (Day) complement;
			resource.addForceOnDay(when);
			return CommandExecutionResult.ok();
		}

	}

	public class IsOnDates extends SentenceSimple {

		public IsOnDates() {
			super(SubjectResource.this, Verbs.isOn, new ComplementDates());
		}

		@Override
		public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
			final Resource resource = (Resource) subject;
			for (Day when : (DaysAsDates) complement) {
				resource.addForceOnDay(when);
			}
			return CommandExecutionResult.ok();
		}

	}

}
