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
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.project.ToTaskDraw;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public abstract class AbstractTaskDraw implements TaskDraw {

	private CenterBorderColor colors;

	private int completion = 100;
	protected Url url;
	protected Display note;
	protected final TimeScale timeScale;
	private Real y;
	protected final String prettyDisplay;
	protected final Day start;
	private final StyleBuilder styleBuilder;
	private final HColorSet colorSet;
	private final Task task;
	private final ToTaskDraw toTaskDraw;

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

	public AbstractTaskDraw(TimeScale timeScale, Real y, String prettyDisplay, Day start, ISkinParam skinParam,
			Task task, ToTaskDraw toTaskDraw, StyleBuilder styleBuilder, HColorSet colorSet) {
		this.y = y;
		this.colorSet = colorSet;
		this.styleBuilder = styleBuilder;
		this.toTaskDraw = toTaskDraw;
		this.start = start;
		this.prettyDisplay = prettyDisplay;
		this.timeScale = timeScale;
		this.task = task;
	}

	abstract StyleSignature getStyleSignature();

	private StyleSignature getStyleSignatureUnstarted() {
		return StyleSignature.of(SName.root, SName.element, SName.ganttDiagram, SName.unstartedTask);
	}

	final protected HColor getLineColor() {
		final HColor unstarted = getStyleSignatureUnstarted().getMergedStyle(styleBuilder).value(PName.LineColor)
				.asColor(getStyleBuilder().getSkinParam().getThemeStyle(), colorSet);
		final HColor regular = getStyle().value(PName.LineColor)
				.asColor(getStyleBuilder().getSkinParam().getThemeStyle(), colorSet);
		return HColorUtils.unlinear(unstarted, regular, completion);
	}

	final protected HColor getBackgroundColor() {
		final HColor unstarted = getStyleSignatureUnstarted().getMergedStyle(styleBuilder).value(PName.BackGroundColor)
				.asColor(getStyleBuilder().getSkinParam().getThemeStyle(), colorSet);
		final HColor regular = getStyle().value(PName.BackGroundColor)
				.asColor(getStyleBuilder().getSkinParam().getThemeStyle(), colorSet);
		return HColorUtils.unlinear(unstarted, regular, completion);
	}

	final protected FontConfiguration getFontConfiguration() {
		return getStyle().getFontConfiguration(styleBuilder.getSkinParam().getThemeStyle(), colorSet);
	}

	final protected Style getStyle() {
		return getStyleSignature().getMergedStyle(styleBuilder);
	}

	final public double getTitleWidth(StringBounder stringBounder) {
		final Style style = getStyleSignature().getMergedStyle(getStyleBuilder());
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		return margin.getLeft() + getTitle().calculateDimension(stringBounder).getWidth() + margin.getRight();
	}

	protected abstract TextBlock getTitle();

	abstract protected double getShapeHeight(StringBounder stringBounder);

	final public double getFullHeightTask(StringBounder stringBounder) {
		final Style style = getStyle();
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		return margin.getTop() + getShapeHeight(stringBounder) + margin.getBottom();
	}

	public TaskDraw getTrueRow() {
		return toTaskDraw.getTaskDraw(task.getRow());
	}

	@Override
	final public Real getY(StringBounder stringBounder) {
		if (task.getRow() == null) {
			return y;
		}
		return getTrueRow().getY(stringBounder);
	}

	public final Task getTask() {
		return task;
	}

	@Override
	public final double getY(StringBounder stringBounder, Direction direction) {
		final Style style = getStyle();
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		final ClockwiseTopRightBottomLeft padding = style.getPadding();

		final double y1 = margin.getTop() + getY(stringBounder).getCurrentValue();
		final double y2 = y1 + getShapeHeight(stringBounder);

		if (direction == Direction.UP) {
			return y1;
		}
		if (direction == Direction.DOWN) {
			return y2;
		}
		return (y1 + y2) / 2;

	}

	protected final StyleBuilder getStyleBuilder() {
		return styleBuilder;
	}

	protected final HColorSet getColorSet() {
		return colorSet;
	}

	protected CenterBorderColor getColors() {
		return colors;
	}

	protected int getCompletion() {
		return completion;
	}

}
