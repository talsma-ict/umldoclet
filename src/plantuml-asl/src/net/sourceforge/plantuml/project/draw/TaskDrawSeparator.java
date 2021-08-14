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
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.project.LabelStrategy;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.core.TaskAttribute;
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
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class TaskDrawSeparator implements TaskDraw {

	private final TimeScale timeScale;
	private Real y;
	private final Day min;
	private final Day max;
	private final String name;
	private final StyleBuilder styleBuilder;
	private final HColorSet colorSet;

	public TaskDrawSeparator(String name, TimeScale timeScale, Real y, Day min, Day max, StyleBuilder styleBuilder,
			HColorSet colorSet) {
		this.styleBuilder = styleBuilder;
		this.colorSet = colorSet;
		this.name = name;
		this.y = y;
		this.timeScale = timeScale;
		this.min = min;
		this.max = max;
	}

	@Override
	public void drawTitle(UGraphic ug, LabelStrategy labelStrategy, double colTitles, double colBars) {
		final ClockwiseTopRightBottomLeft padding = getStyle().getPadding();
		final ClockwiseTopRightBottomLeft margin = getStyle().getMargin();
		final double dx = margin.getLeft() + padding.getLeft();
		final double dy = margin.getTop() + padding.getTop();
		final double x;
		if (labelStrategy.titleInFirstColumn()) {
			x = colTitles;
		} else {
			x = 0;
		}
		getTitle().drawU(ug.apply(new UTranslate(x + dx, dy)));
	}

	@Override
	public double getTitleWidth(StringBounder stringBounder) {
		// Never used in first column
		return 0;
	}

	private StyleSignature getStyleSignature() {
		return StyleSignature.of(SName.root, SName.element, SName.ganttDiagram, SName.separator);
	}

	private Style getStyle() {
		return getStyleSignature().getMergedStyle(styleBuilder);
	}

	private TextBlock getTitle() {
		if (name == null) {
			return TextBlockUtils.empty(0, 0);
		}
		return Display.getWithNewlines(this.name).create(getFontConfiguration(), HorizontalAlignment.LEFT,
				new SpriteContainerEmpty());
	}

	private FontConfiguration getFontConfiguration() {
		return getStyle().getFontConfiguration(styleBuilder.getSkinParam().getThemeStyle(), colorSet);
	}

	@Override
	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double widthTitle = getTitle().calculateDimension(stringBounder).getWidth();
		final double start = timeScale.getStartingPosition(min);
		// final double start2 = start1 + widthTitle;
		final double end = timeScale.getEndingPosition(max);

		final ClockwiseTopRightBottomLeft padding = getStyle().getPadding();
		final ClockwiseTopRightBottomLeft margin = getStyle().getMargin();
		ug = ug.apply(new UTranslate(0, margin.getTop()));

		final HColor backColor = getStyle().value(PName.BackGroundColor)
				.asColor(styleBuilder.getSkinParam().getThemeStyle(), colorSet);

		if (HColorUtils.isTransparent(backColor) == false) {
			final double height = padding.getTop() + getTextHeight(stringBounder) + padding.getBottom();
			if (height > 0) {
				final URectangle rect = new URectangle(end - start, height);
				ug.apply(backColor.bg()).draw(rect);
			}
		}

		final HColor lineColor = getStyle().value(PName.LineColor).asColor(styleBuilder.getSkinParam().getThemeStyle(),
				colorSet);
		ug = ug.apply(lineColor);
		ug = ug.apply(UTranslate.dy(padding.getTop() + getTextHeight(stringBounder) / 2));

		if (widthTitle == 0) {
			final ULine line = ULine.hline(end - start);
			ug.draw(line);
		} else {
			if (padding.getLeft() > 1) {
				final ULine line1 = ULine.hline(padding.getLeft());
				ug.draw(line1);
			}
			final double x1 = padding.getLeft() + margin.getLeft() + widthTitle + margin.getRight();
			final double x2 = end - 1;
			final ULine line2 = ULine.hline(x2 - x1);
			ug.apply(UTranslate.dx(x1)).draw(line2);
		}
	}

	@Override
	public FingerPrint getFingerPrint(StringBounder stringBounder) {
		final double h = getFullHeightTask(stringBounder);
		final double end = timeScale.getEndingPosition(max);
		return new FingerPrint(0, getY(stringBounder).getCurrentValue(), end,
				getY(stringBounder).getCurrentValue() + h);
	}

	@Override
	public FingerPrint getFingerPrintNote(StringBounder stringBounder) {
		return null;
	}

	@Override
	public double getFullHeightTask(StringBounder stringBounder) {
		final ClockwiseTopRightBottomLeft padding = getStyle().getPadding();
		final ClockwiseTopRightBottomLeft margin = getStyle().getMargin();
		return margin.getTop() + padding.getTop() + getTextHeight(stringBounder) + padding.getBottom()
				+ margin.getBottom();
	}

	private double getTextHeight(StringBounder stringBounder) {
		return getTitle().calculateDimension(stringBounder).getHeight();
	}

	@Override
	public Real getY(StringBounder stringBounder) {
		return y;
	}

	@Override
	public TaskDraw getTrueRow() {
		return null;
	}

	@Override
	public void setColorsAndCompletion(CenterBorderColor colors, int completion, Url url, Display note) {
	}

	@Override
	public Task getTask() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getY(StringBounder stringBounder, Direction direction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getHeightMax(StringBounder stringBounder) {
		return getFullHeightTask(stringBounder);
	}

	@Override
	public double getX1(TaskAttribute taskAttribute) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getX2(TaskAttribute taskAttribute) {
		throw new UnsupportedOperationException();
	}

}
