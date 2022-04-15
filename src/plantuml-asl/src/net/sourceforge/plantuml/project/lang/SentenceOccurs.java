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

public class SentenceOccurs extends SentenceSimple {

	public SentenceOccurs() {
		super(new SubjectTask(), Verbs.occurs(), new ComplementFromTo());
	}

	@Override
	public CommandExecutionResult execute(GanttDiagram project, Object subject, Object complement) {
		final Task task = (Task) subject;
		final TwoNames bothNames = (TwoNames) complement;
		final String name1 = bothNames.getName1();
		final String name2 = bothNames.getName2();
		final Task from = project.getExistingTask(name1);
		if (from == null) {
			return CommandExecutionResult.error("No such " + name1 + " task");
		}
		final Task to = project.getExistingTask(name2);
		if (to == null) {
			return CommandExecutionResult.error("No such " + name2 + " task");
		}
		task.setStart(from.getEnd());
		task.setEnd(to.getEnd());
		project.addContraint(new GanttConstraint(project.getIHtmlColorSet(), project.getCurrentStyleBuilder(),
				new TaskInstant(from, TaskAttribute.START), new TaskInstant(task, TaskAttribute.START)));
		project.addContraint(new GanttConstraint(project.getIHtmlColorSet(), project.getCurrentStyleBuilder(),
				new TaskInstant(to, TaskAttribute.END), new TaskInstant(task, TaskAttribute.END)));
		return CommandExecutionResult.ok();
	}

}
