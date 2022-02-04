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

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.core.TaskAttribute;
import net.sourceforge.plantuml.project.core.TaskInstant;
import net.sourceforge.plantuml.project.draw.TaskDraw;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class GanttArrow implements UDrawable {

	private final TimeScale timeScale;
	private final Direction atStart;
	private final TaskInstant source;
	private final Direction atEnd;
	private final TaskInstant dest;

	private final HColorSet colorSet;
	private final Style style;
	private final ToTaskDraw toTaskDraw;
	private final StyleBuilder styleBuilder;

	public GanttArrow(HColorSet colorSet, Style style, TimeScale timeScale, TaskInstant source, TaskInstant dest,
			ToTaskDraw toTaskDraw, StyleBuilder styleBuilder) {
		this.styleBuilder = styleBuilder;
		this.toTaskDraw = toTaskDraw;
		this.style = style;
		this.colorSet = colorSet;
		this.timeScale = timeScale;
		this.source = source;
		this.dest = dest;
		if (source.getAttribute() == TaskAttribute.END && dest.getAttribute() == TaskAttribute.START) {
			this.atStart = source.sameRowAs(dest) ? Direction.LEFT : Direction.DOWN;
			this.atEnd = Direction.RIGHT;
		} else if (source.getAttribute() == TaskAttribute.END && dest.getAttribute() == TaskAttribute.END) {
			this.atStart = Direction.RIGHT;
			this.atEnd = Direction.LEFT;
		} else if (source.getAttribute() == TaskAttribute.START && dest.getAttribute() == TaskAttribute.START) {
			this.atStart = Direction.LEFT;
			this.atEnd = Direction.RIGHT;
		} else if (source.getAttribute() == TaskAttribute.START && dest.getAttribute() == TaskAttribute.END) {
			this.atStart = source.sameRowAs(dest) ? Direction.RIGHT : Direction.DOWN;
			this.atEnd = Direction.LEFT;
		} else {
			throw new IllegalArgumentException();
		}
	}

	private TaskDraw getSource() {
		if (source.getMoment() instanceof Task)
			return toTaskDraw.getTaskDraw((Task) source.getMoment());

		return null;

	}

	private TaskDraw getDestination() {
		if (dest.getMoment() instanceof Task)
			return toTaskDraw.getTaskDraw((Task) dest.getMoment());

		return null;
	}

	public void drawU(UGraphic ug) {
		ug = style.applyStrokeAndLineColor(ug, colorSet, styleBuilder.getSkinParam().getThemeStyle());

		final TaskDraw start = getSource();
		final TaskDraw end = getDestination();
		if (start == null || end == null)
			return;

		double x1 = getX(source.getAttribute(), start, atStart);
		final StringBounder stringBounder = ug.getStringBounder();
		double y1 = start.getY(stringBounder, atStart);

		final double x2 = getX(dest.getAttribute(), end, atEnd.getInv());
		final double y2 = end.getY(stringBounder, atEnd);

		if (atStart == Direction.DOWN && y2 < y1)
			y1 = start.getY(stringBounder, atStart.getInv());

		final double minimalWidth = 8;

		if (this.atStart == Direction.DOWN && this.atEnd == Direction.RIGHT) {
			if (x2 > x1) {
				if (x2 - x1 < minimalWidth)
					x1 = x2 - minimalWidth;

				drawLine(ug, x1, y1, x1, y2, x2, y2);
			} else {
				x1 = getX(source.getAttribute(), start, Direction.RIGHT);
				y1 = start.getY(stringBounder, Direction.RIGHT);
				final double y1b = end.getY(stringBounder).getCurrentValue();
				drawLine(ug, x1, y1, x1 + 6, y1, x1 + 6, y1b, x2 - 8, y1b, x2 - 8, y2, x2, y2);
			}
		} else if (this.atStart == Direction.RIGHT && this.atEnd == Direction.LEFT) {
			final double xmax = Math.max(x1, x2) + 8;
			drawLine(ug, x1, y1, xmax, y1, xmax, y2, x2, y2);
		} else if (this.atStart == Direction.LEFT && this.atEnd == Direction.RIGHT) {
			final double xmin = Math.min(x1, x2) - 8;
			drawLine(ug, x1, y1, xmin, y1, xmin, y2, x2, y2);
		} else if (this.atStart == Direction.DOWN && this.atEnd == Direction.LEFT) {
			drawLine(ug, x1, y1, x1, y2, x2, y2);
		} else {
			throw new IllegalArgumentException();
		}

		ug = ug.apply(new UStroke(1.5)).apply(
				style.value(PName.LineColor).asColor(styleBuilder.getSkinParam().getThemeStyle(), colorSet).bg());
		ug.apply(new UTranslate(x2, y2)).draw(Arrows.asTo(atEnd));

	}

	private void drawLine(UGraphic ug, double... coord) {
		for (int i = 0; i < coord.length - 2; i += 2) {
			final double x1 = coord[i];
			final double y1 = coord[i + 1];
			final double x2 = coord[i + 2];
			final double y2 = coord[i + 3];
			ug.apply(new UTranslate(x1, y1)).draw(new ULine(x2 - x1, y2 - y1));
		}

	}

	private StyleSignature getStyleSignatureTask() {
		return StyleSignature.of(SName.root, SName.element, SName.ganttDiagram, SName.task);
	}

	private double getX(TaskAttribute taskAttribute, TaskDraw task, Direction direction) {
		if (direction == Direction.LEFT) {
			return task.getX1(taskAttribute) - 1;
		}
		if (direction == Direction.RIGHT) {
			return task.getX2(taskAttribute) + 1;
		}
		return (task.getX1(taskAttribute) + (task.getX2(taskAttribute))) / 2;
	}
}
