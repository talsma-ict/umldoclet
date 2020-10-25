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
package net.sourceforge.plantuml.project.draw;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.project.ToTaskDraw;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public abstract class AbstractTaskDraw implements TaskDraw {

	protected CenterBorderColor colors;
	protected int completion = 100;
	protected Url url;
	protected Display note;
	protected final TimeScale timeScale;
	private double y;
	protected final String prettyDisplay;
	protected final Day start;
	protected final ISkinParam skinParam;
	private final Task task;
	private final ToTaskDraw toTaskDraw;

	protected final double margin = 2;

	@Override
	final public String toString() {
		return super.toString() + " " + task;
	}

	final public void setColorsAndCompletion(CenterBorderColor colors, int completion, Url url, Display note) {
		this.colors = colors;
		this.completion = completion;
		this.url = url;
		this.note = note;
	}

	public AbstractTaskDraw(TimeScale timeScale, double y, String prettyDisplay, Day start, ISkinParam skinParam,
			Task task, ToTaskDraw toTaskDraw) {
		this.y = y;
		this.toTaskDraw = toTaskDraw;
		this.start = start;
		this.prettyDisplay = prettyDisplay;
		this.timeScale = timeScale;
		this.skinParam = skinParam;
		this.task = task;
	}

	final protected HColor getLineColor() {
		return getStyle().value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
	}

	final protected HColor getBackgroundColor() {
		return getStyle().value(PName.BackGroundColor).asColor(skinParam.getIHtmlColorSet());
	}

	final protected FontConfiguration getFontConfiguration() {
		return getStyle().getFontConfiguration(skinParam.getIHtmlColorSet());
	}

	abstract protected Style getStyle();

	final protected double getShapeHeight() {
		return getHeightTask() - 2 * margin;
	}

	final public double getHeightTask() {
		return getFontConfiguration().getFont().getSize2D() + 5;
	}

	public TaskDraw getTrueRow() {
		return toTaskDraw.getTaskDraw(task.getRow());
	}

	final public double getY() {
		if (task.getRow() == null) {
			return y;
		}
		return getTrueRow().getY();
	}

	public void pushMe(double deltaY) {
		if (task.getRow() == null) {
			this.y += deltaY;
		}
	}

	public final Task getTask() {
		return task;
	}

	public final double getY(Direction direction) {
		if (direction == Direction.UP) {
			return getY();
		}
		if (direction == Direction.DOWN) {
			return getY() + getHeightTask();
		}
		return getY() + getHeightTask() / 2;
	}

}
