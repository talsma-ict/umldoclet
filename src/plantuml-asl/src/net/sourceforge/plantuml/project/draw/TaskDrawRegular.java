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

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.project.GanttConstraint;
import net.sourceforge.plantuml.project.LabelStrategy;
import net.sourceforge.plantuml.project.ToTaskDraw;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.core.TaskAttribute;
import net.sourceforge.plantuml.project.core.TaskImpl;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.graphic.Segment;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.style.Value;
import net.sourceforge.plantuml.svek.image.Opale;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class TaskDrawRegular extends AbstractTaskDraw {

	private final Day end;
	private final boolean oddStart;
	private final boolean oddEnd;
	private final Collection<Day> paused;
	private final Collection<GanttConstraint> constraints;
	private final ISkinParam skinParam;

	// private final double margin = 2;

	public TaskDrawRegular(TimeScale timeScale, Real y, String prettyDisplay, Day start, Day end, boolean oddStart,
			boolean oddEnd, ISkinParam skinParam, Task task, ToTaskDraw toTaskDraw,
			Collection<GanttConstraint> constraints, StyleBuilder styleBuilder, HColorSet colorSet) {
		super(timeScale, y, prettyDisplay, start, skinParam, task, toTaskDraw, styleBuilder, colorSet);
		this.skinParam = skinParam;
		this.constraints = constraints;
		this.end = end;
		this.oddStart = oddStart;
		this.oddEnd = oddEnd;
		this.paused = new TreeSet<>(((TaskImpl) task).getAllPaused());
		for (Day tmp = start; tmp.compareTo(end) <= 0; tmp = tmp.increment()) {
			final int load = toTaskDraw.getDefaultPlan().getLoadAt(tmp);
			if (load == 0) {
				this.paused.add(tmp);
			}

		}
	}

	@Override
	protected double getShapeHeight(StringBounder stringBounder) {
		final Style style = getStyle();
		final ClockwiseTopRightBottomLeft padding = style.getPadding();
		return padding.getTop() + getTitle().calculateDimension(stringBounder).getHeight() + padding.getBottom();
	}

	@Override
	public void drawTitle(UGraphic ug, LabelStrategy labelStrategy, double colTitles, double colBars) {
		final TextBlock title = getTitle();
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dim = title.calculateDimension(stringBounder);

		final Style style = getStyleSignature().getMergedStyle(getStyleBuilder());
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		final ClockwiseTopRightBottomLeft padding = style.getPadding();

		ug = ug.apply(UTranslate.dy(margin.getTop() + padding.getTop()));

		if (labelStrategy.titleInFirstColumn()) {
			if (labelStrategy.rightAligned())
				title.drawU(ug.apply(UTranslate.dx(colTitles - dim.getWidth() - margin.getRight())));
			else
				title.drawU(ug.apply(UTranslate.dx(margin.getLeft())));
			return;
		} else if (labelStrategy.titleInLastColumn()) {
			title.drawU(ug.apply(UTranslate.dx(colBars + margin.getLeft())));
			return;
		}

		final double pos1 = timeScale.getStartingPosition(start) + 6;
		final double pos2 = timeScale.getEndingPosition(end) - 6;
		final double pos;
		if (pos2 - pos1 > dim.getWidth())
			pos = pos1;
		else
			pos = getOutPosition(pos2);
		title.drawU(ug.apply(UTranslate.dx(pos)));
	}

	@Override
	protected TextBlock getTitle() {
		return Display.getWithNewlines(prettyDisplay).create(getFontConfiguration(), HorizontalAlignment.LEFT,
				new SpriteContainerEmpty());
	}

	private double getOutPosition(double pos2) {
		if (isThereRightArrow()) {
			return pos2 + 18;
		}
		return pos2 + 8;
	}

	private boolean isThereRightArrow() {
		for (GanttConstraint constraint : constraints) {
			if (constraint.isThereRightArrow(getTask())) {
				return true;
			}
		}
		return false;
	}

	@Override
	StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.ganttDiagram, SName.task);
	}

	public void drawU(UGraphic ug) {
		final double startPos = timeScale.getStartingPosition(start);
		drawNote(ug.apply((new UTranslate(startPos, getYNotePosition(ug.getStringBounder())))));

		ug = applyColors(ug);
		drawShape(ug);
	}

	private double getYNotePosition(StringBounder stringBounder) {
		final Style style = getStyle();
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		return margin.getTop() + getShapeHeight(stringBounder) + margin.getBottom();
	}

	private void drawNote(UGraphic ug) {
		if (note == null) {
			return;
		}
		getOpaleNote().drawU(ug);

	}

	public double getHeightMax(StringBounder stringBounder) {
		if (note == null) {
			return getFullHeightTask(stringBounder);
		}
		return getYNotePosition(stringBounder) + getOpaleNote().calculateDimension(stringBounder).getHeight();
	}

	private Opale getOpaleNote() {
		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.ganttDiagram, SName.note)
				.getMergedStyle(getStyleBuilder());

		final FontConfiguration fc = style.getFontConfiguration(skinParam.getThemeStyle(), getColorSet());

		final HorizontalAlignment horizontalAlignment = style.value(PName.HorizontalAlignment).asHorizontalAlignment();
		final Sheet sheet = Parser.build(fc, horizontalAlignment, skinParam, CreoleMode.FULL).createSheet(note);
		final double padding = style.value(PName.Padding).asDouble();
		final SheetBlock1 sheet1 = new SheetBlock1(sheet, LineBreakStrategy.NONE, padding);

		final HColor noteBackgroundColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
				getColorSet());
		final HColor borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(), getColorSet());
		final double shadowing = style.value(PName.Shadowing).asDouble();

		return new Opale(shadowing, borderColor, noteBackgroundColor, sheet1, false, style.getStroke());
	}

	public FingerPrint getFingerPrint(StringBounder stringBounder) {
		final double h = getFullHeightTask(stringBounder);
		final double startPos = timeScale.getStartingPosition(start);
		final double endPos = timeScale.getEndingPosition(end);
		return new FingerPrint(startPos, getY(stringBounder).getCurrentValue(), endPos - startPos, h);
	}

	public FingerPrint getFingerPrintNote(StringBounder stringBounder) {
		if (note == null) {
			return null;
		}
		final Dimension2D dim = getOpaleNote().calculateDimension(stringBounder);
		final double startPos = timeScale.getStartingPosition(start);
		// final double endPos = timeScale.getEndingPosition(end);
		return new FingerPrint(startPos, getY(stringBounder).getCurrentValue() + getYNotePosition(stringBounder),
				dim.getWidth(), dim.getHeight());
	}

	private UGraphic applyColors(UGraphic ug) {
		final CenterBorderColor col = this.getColors();
		if (col != null && col.isOk()) {
			return col.apply(ug);
		}
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

		if (url != null) {
			ug.startUrl(url);
		}

		ug = ug.apply(UTranslate.dy(margin.getTop()));

		final StringBounder stringBounder = ug.getStringBounder();

		final double round = style.value(PName.RoundCorner).asDouble();

		final Collection<Segment> off = new ArrayList<>();
		for (Day pause : paused) {
			final double x1 = timeScale.getStartingPosition(pause);
			final double x2 = timeScale.getEndingPosition(pause);
			off.add(new Segment(x1, x2));
		}

		final HColor backUndone = StyleSignatureBasic.of(SName.root, SName.element, SName.ganttDiagram, SName.undone)
				.getMergedStyle(getStyleBuilder()).value(PName.BackGroundColor)
				.asColor(skinParam.getThemeStyle(), getColorSet());

		final RectangleTask rectangleTask = new RectangleTask(startPos, endPos, round, getCompletion(), off);

		rectangleTask.draw(ug, getShapeHeight(stringBounder), backUndone, oddStart, oddEnd);

		if (url != null) {
			ug.closeUrl();
		}

	}

	private void drawShapeOld(UGraphic ug) {
		final Style style = getStyleSignature().getMergedStyle(getStyleBuilder());
		final ClockwiseTopRightBottomLeft margin = style.getMargin();

		final double startPos = timeScale.getStartingPosition(start) + margin.getLeft();
		final double endPos = timeScale.getEndingPosition(end) - margin.getRight();

		double fullLength = endPos - startPos;
		if (fullLength < 3) {
			fullLength = 3;
		}
		if (url != null) {
			ug.startUrl(url);
		}

		ug = ug.apply(UTranslate.dy(margin.getTop()));

		final StringBounder stringBounder = ug.getStringBounder();

		final double round = style.value(PName.RoundCorner).asDouble();

		if (oddStart && !oddEnd) {
			ug.apply(UTranslate.dx(startPos))
					.draw(PathUtils.UtoRight(fullLength, getShapeHeight(stringBounder), round));
		} else if (!oddStart && oddEnd) {
			ug.apply(UTranslate.dx(startPos)).draw(PathUtils.UtoLeft(fullLength, getShapeHeight(stringBounder), round));
		} else {
			final URectangle full = new URectangle(fullLength, getShapeHeight(stringBounder)).rounded(round);
			if (getCompletion() == 100) {
				ug.apply(UTranslate.dx(startPos)).draw(full);
			} else {
				final double partialLength = fullLength * getCompletion() / 100.;
				ug.apply(UTranslate.dx(startPos)).apply(HColorUtils.WHITE).apply(HColorUtils.WHITE.bg()).draw(full);
				if (partialLength > 2) {
					final URectangle partial = new URectangle(partialLength, getShapeHeight(stringBounder))
							.rounded(round);
					ug.apply(UTranslate.dx(startPos)).apply(new HColorNone()).draw(partial);
				}
				if (partialLength > 10 && partialLength < fullLength - 10) {
					final URectangle patch = new URectangle(round, getShapeHeight(stringBounder));
					ug.apply(UTranslate.dx(startPos)).apply(new HColorNone())
							.apply(UTranslate.dx(partialLength - round)).draw(patch);
				}
				ug.apply(UTranslate.dx(startPos)).apply(new HColorNone().bg()).draw(full);
			}
		}
		if (url != null) {
			ug.closeUrl();
		}
		Day begin = null;
		for (Day pause : paused) {
			if (paused.contains(pause.increment())) {
				if (begin == null)
					begin = pause;
			} else {
				if (begin == null)
					drawPause(ug, pause, pause);
				else
					drawPause(ug, begin, pause);
				begin = null;
			}
		}
	}

	private void drawPause(UGraphic ug, Day start1, Day end) {
		final double x1 = timeScale.getStartingPosition(start1);
		final double x2 = timeScale.getEndingPosition(end);
		final StringBounder stringBounder = ug.getStringBounder();
		final URectangle small = new URectangle(x2 - x1 - 1, getShapeHeight(stringBounder) + 1);
		final ULine line = ULine.hline(x2 - x1 - 1);
		ug = ug.apply(UTranslate.dx(x1 - 1));
		ug.apply(HColorUtils.WHITE).apply(HColorUtils.WHITE.bg()).draw(small);
		final UGraphic ugLine = ug.apply(new UStroke(2, 3, 1));
		ugLine.draw(line);
		ugLine.apply(UTranslate.dy(getShapeHeight(stringBounder))).draw(line);
	}

}
