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
package net.sourceforge.plantuml.project.core;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.project.Load;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.DayOfWeek;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.url.Url;

public class TaskGroup extends AbstractTask implements Task {

	private final TaskGroup parent;
	private final List<Task> children = new ArrayList<>();

	public TaskGroup(TaskGroup parent, StyleBuilder styleBuilder, String name) {
		super(styleBuilder, new TaskCode(name));
		this.parent = parent;
	}

	public Day getStart() {
		Day min = null;
		for (Task child : children)
			if (min == null || min.compareTo(child.getStart()) > 0)
				min = child.getStart();

		if (min != null)
			return min;

		throw new UnsupportedOperationException();
	}

	public Day getEnd() {
		Day max = null;
		for (Task child : children)
			if (max == null || max.compareTo(child.getEnd()) < 0)
				max = child.getEnd();

		if (max != null)
			return max;

		throw new UnsupportedOperationException();
	}

	public void setStart(Day start) {
		throw new UnsupportedOperationException();
	}

	public void setEnd(Day end) {
		throw new UnsupportedOperationException();
	}

	public void setColors(CenterBorderColor... colors) {
		throw new UnsupportedOperationException();
	}

	public void addResource(Resource resource, int percentage) {
		throw new UnsupportedOperationException();
	}

	public Load getLoad() {
		throw new UnsupportedOperationException();
	}

	public void setLoad(Load load) {
		throw new UnsupportedOperationException();
	}

	public void setDiamond(boolean diamond) {
		throw new UnsupportedOperationException();
	}

	public boolean isDiamond() {
		throw new UnsupportedOperationException();
	}

	public void setCompletion(int completion) {
		throw new UnsupportedOperationException();
	}

	public void setUrl(Url url) {
		throw new UnsupportedOperationException();
	}

	public void addPause(Day pause) {
		throw new UnsupportedOperationException();
	}

	public void addPause(DayOfWeek pause) {
		throw new UnsupportedOperationException();
	}

	public void setNote(Display note) {
	}

	public void addTask(Task child) {
		children.add(child);
	}

	public final TaskGroup getParent() {
		return parent;
	}

}
