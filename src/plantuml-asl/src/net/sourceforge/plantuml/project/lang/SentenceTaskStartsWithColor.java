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

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.project.GanttConstraint;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.core.TaskAttribute;
import net.sourceforge.plantuml.project.core.TaskInstant;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class SentenceTaskStartsWithColor extends SentenceSimple {

	public SentenceTaskStartsWithColor() {
		super(new SubjectTask(), Verbs.starts2(),
				new PairOfSomething(new ComplementBeforeOrAfterOrAtTaskStartOrEnd(), new ComplementWithColorLink()));
	}

	@Override
	public CommandExecutionResult execute(GanttDiagram diagram, Object subject, Object complement) {
		final Task task = (Task) subject;
		final TaskInstant when;

		final Object[] pairs = (Object[]) complement;
		when = (TaskInstant) pairs[0];
		final CenterBorderColor complement22 = (CenterBorderColor) pairs[1];

		task.setStart(when.getInstantPrecise());
		if (when.isTask()) {
			final HColor color = complement22.getCenter();
			final GanttConstraint link = new GanttConstraint(diagram.getIHtmlColorSet(),
					diagram.getCurrentStyleBuilder(), when, new TaskInstant(task, TaskAttribute.START), color);
			link.applyStyle(complement22.getStyle());
			diagram.addContraint(link);
		}
		return CommandExecutionResult.ok();

	};
}
