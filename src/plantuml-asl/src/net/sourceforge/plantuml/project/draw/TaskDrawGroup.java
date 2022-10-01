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
package net.sourceforge.plantuml.project.draw;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.project.LabelStrategy;
import net.sourceforge.plantuml.project.ToTaskDraw;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.core.TaskAttribute;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class TaskDrawGroup extends AbstractTaskDraw {

	private final Day end;

	private final ISkinParam skinParam;

	public TaskDrawGroup(TimeScale timeScale, Real y, String prettyDisplay, Day start, Day end, ISkinParam skinParam,
			Task task, ToTaskDraw toTaskDraw, StyleBuilder styleBuilder) {
		super(timeScale, y, prettyDisplay, start, skinParam, task, toTaskDraw, styleBuilder);
		this.skinParam = skinParam;
		this.end = end;
	}

	@Override
	protected double getShapeHeight(StringBounder stringBounder) {
		final Style style = getStyle();
		final ClockwiseTopRightBottomLeft padding = style.getPadding();
		// return padding.getTop() +
		// getTitle().calculateDimension(stringBounder).getHeight() +
		// padding.getBottom() + 8;
		final double pos1 = timeScale.getStartingPosition(start) + 6;
		final double pos2 = timeScale.getEndingPosition(end) - 6;
		final TextBlock title = getTitle();
		final XDimension2D dim = title.calculateDimension(stringBounder);
		if (pos2 - pos1 > dim.getWidth())
			return dim.getHeight() + 2;
		else
			return dim.getHeight();
	}

	@Override
	public void drawTitle(UGraphic ug, LabelStrategy labelStrategy, double colTitles, double colBars) {
		final TextBlock title = getTitle();
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dim = title.calculateDimension(stringBounder);

		final Style style = getStyleSignature().getMergedStyle(getStyleBuilder());
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		final ClockwiseTopRightBottomLeft padding = style.getPadding();

		final double pos1 = timeScale.getStartingPosition(start) + 6;
		final double pos2 = timeScale.getEndingPosition(end) - 6;
		final double pos;
		final double y;
		if (pos2 - pos1 > dim.getWidth()) {
			pos = pos1 + (pos2 - pos1 - dim.getWidth()) / 2;
			// y = margin.getTop() + padding.getTop();
			y = 0;
		} else {
			pos = pos2 + 6;
			y = (getFullHeightTask(stringBounder) - dim.getHeight());
		}
		title.drawU(ug.apply(new UTranslate(pos, y)));
	}

	@Override
	protected TextBlock getTitle() {
		return Display.getWithNewlines(prettyDisplay).create(getFontConfiguration(), HorizontalAlignment.LEFT,
				new SpriteContainerEmpty());
	}

	@Override
	StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.ganttDiagram, SName.task);
	}

	public void drawU(UGraphic ug) {
		// final double startPos = timeScale.getStartingPosition(start);
		ug = applyColors(ug);
		drawShape(ug);
	}

	@Override
	public double getHeightMax(StringBounder stringBounder) {
		return getFullHeightTask(stringBounder);
	}

	public FingerPrint getFingerPrint(StringBounder stringBounder) {
		final double h = getFullHeightTask(stringBounder);
		final double startPos = timeScale.getStartingPosition(start);
		final double endPos = timeScale.getEndingPosition(end);
		return new FingerPrint(startPos, getY(stringBounder).getCurrentValue(), endPos - startPos, h);
	}

	public FingerPrint getFingerPrintNote(StringBounder stringBounder) {
		return null;
	}

	private UGraphic applyColors(UGraphic ug) {
		final CenterBorderColor col = this.getColors();
		if (col != null && col.isOk())
			return col.apply(ug);

		return ug.apply(getLineColor()).apply(getBackgroundColor().bg());
	}

	public double getX1(TaskAttribute taskAttribute) {
		final Style style = getStyleSignature().getMergedStyle(getStyleBuilder());
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		final double startPos = taskAttribute == TaskAttribute.START ? timeScale.getStartingPosition(start)
				: timeScale.getStartingPosition(end) + margin.getLeft();
		return startPos;
	}

	public double getX2(TaskAttribute taskAttribute) {
		final Style style = getStyleSignature().getMergedStyle(getStyleBuilder());
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		final double endPos = taskAttribute == TaskAttribute.START ? timeScale.getEndingPosition(start)
				: timeScale.getEndingPosition(end) - margin.getLeft();
		return endPos;
	}

	private void drawShape(UGraphic ug) {
		final Style style = getStyleSignature().getMergedStyle(getStyleBuilder());
		final ClockwiseTopRightBottomLeft margin = style.getMargin();

		final double startPos = timeScale.getStartingPosition(start) + margin.getLeft();
		final double endPos = timeScale.getEndingPosition(end) - margin.getRight();

		if (url != null)
			ug.startUrl(url);

		// ug = ug.apply(UTranslate.dy(margin.getTop() + 7));
		ug = ug.apply(UTranslate.dy(getFullHeightTask(ug.getStringBounder()) - height));

		ug = ug.apply(HColors.BLACK).apply(HColors.BLACK.bg());
		ug.draw(getShape(startPos, endPos));

		if (url != null)
			ug.closeUrl();

	}

	final private double height = 10;

	private UPath getShape(final double startPos, final double endPos) {
		final UPath rect = new UPath();

		final double thick = 2;
		final double y1 = (height - thick) / 2;
		final double y2 = height - (height - thick) / 2;
		final double dx = 6;

		rect.moveTo(startPos, 0);
		rect.lineTo(startPos + dx, y1);
		rect.lineTo(endPos - dx, y1);
		rect.lineTo(endPos, 0);
		rect.lineTo(endPos, height);
		rect.lineTo(endPos - dx, y2);
		rect.lineTo(startPos + dx, y2);
		rect.lineTo(startPos, height);
		rect.lineTo(startPos, 0);

		rect.closePath();
		return rect;
	}

	private UPath getShapeOld2(final double startPos, final double endPos) {
		final UPath rect = new UPath();

		final double height1 = 9;
		final double height2 = 7;
		final double dx = 6;

		rect.moveTo(startPos, 0);
		rect.lineTo(startPos + dx, height2);
		rect.lineTo(endPos - dx, height2);
		rect.lineTo(endPos, 0);
		rect.lineTo(endPos, height1);
		rect.lineTo(startPos, height1);
		rect.lineTo(startPos, 0);

		rect.closePath();
		return rect;
	}

	private UPath getShapeOld(final double startPos, final double endPos) {
		final UPath rect = new UPath();

		final double height1 = 9;
		final double height2 = 2;
		final double dx = 6;

		rect.moveTo(startPos, 0);
		rect.lineTo(endPos, 0);
		rect.lineTo(endPos, height1);
		rect.lineTo(endPos - dx, height2);
		rect.lineTo(startPos + dx, height2);
		rect.lineTo(startPos, height1);
		rect.lineTo(startPos, 0);
		rect.closePath();
		return rect;
	}

}
