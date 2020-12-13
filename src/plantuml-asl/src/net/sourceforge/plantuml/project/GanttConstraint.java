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
package net.sourceforge.plantuml.project;

import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.WithLinkType;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.core.TaskAttribute;
import net.sourceforge.plantuml.project.core.TaskInstant;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class GanttConstraint extends WithLinkType {

	private final TaskInstant source;
	private final TaskInstant dest;

	public GanttConstraint(TaskInstant source, TaskInstant dest, HColor forcedColor) {
		this.source = source;
		this.dest = dest;
		this.type = new LinkType(LinkDecor.NONE, LinkDecor.NONE);
		this.setSpecificColor(forcedColor);
	}

	public boolean isOn(Task task) {
		return source.getMoment() == task || dest.getMoment() == task;
	}

	public boolean isThereRightArrow(Task task) {
		if (dest.getMoment() == task && dest.getAttribute() == TaskAttribute.END) {
			return true;
		}
		if (source.getMoment() == task && dest.getAttribute() == TaskAttribute.END
				&& source.getAttribute() == TaskAttribute.END) {
			return true;
		}
		return false;
	}

	public GanttConstraint(TaskInstant source, TaskInstant dest) {
		this(source, dest, null);
	}

	@Override
	public String toString() {
		return source.toString() + " --> " + dest.toString();
	}

	public UDrawable getUDrawable(TimeScale timeScale, HColor color, ToTaskDraw toTaskDraw) {
		if (getSpecificColor() == null) {
			return new GanttArrow(timeScale, source, dest, color, getType(), toTaskDraw);
		}
		return new GanttArrow(timeScale, source, dest, getSpecificColor(), getType(), toTaskDraw);
	}

	public boolean isHidden(Day min, Day max) {
		if (isHidden(source.getInstantPrecise(), min, max)) {
			return true;
		}
		if (isHidden(dest.getInstantPrecise(), min, max)) {
			return true;
		}
		return false;
	}

	private boolean isHidden(Day now, Day min, Day max) {
		if (now.compareTo(min) < 0) {
			return true;
		}
		if (now.compareTo(max) > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void goNorank() {
	}

}
