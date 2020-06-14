/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.project3;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorSetSimple;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TaskDrawRegular implements TaskDraw {

	// private static final HtmlColor defaultColor = HtmlColorUtils.COL_84BE84;
	private static final HtmlColor defaultColor = new HtmlColorSetSimple().getColorIfValid("GreenYellow");
	private final TaskImpl task;
	private final TimeScale timeScale;
	private final double y;
	private ComplementColors colors;

	private final double margin = 2;

	public TaskDrawRegular(TaskImpl task, TimeScale timeScale, double y) {
		this.y = y;
		this.task = task;
		this.timeScale = timeScale;
	}

	public void drawTitle(UGraphic ug) {
		final TextBlock title = Display.getWithNewlines(task.getPrettyDisplay()).create(getFontConfiguration(),
				HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		final double shapeHeight = getShapeHeight(100);
		final double titleHeight = title.calculateDimension(ug.getStringBounder()).getHeight();
		final double h = (margin + shapeHeight - titleHeight) / 2;
		title.drawU(ug.apply(new UTranslate(timeScale.getEndingPosition(task.getStart()), h)));
	}

	private FontConfiguration getFontConfiguration() {
		final UFont font = UFont.serif(11);
		return new FontConfiguration(font, HtmlColorUtils.BLACK, HtmlColorUtils.BLACK, false);
	}

	public void drawU(UGraphic ug1) {
		final double start = timeScale.getStartingPosition(task.getStart());
		ug1 = applyColors(ug1);
		UGraphic ug2 = ug1.apply(new UTranslate(start + margin, margin));
		final UShape shapeFull = getShape(100);
		if (shapeFull instanceof UPolygon) {
			ug2.draw(shapeFull);
		} else {
			ug2.draw(shapeFull);
		}
	}

	private UGraphic applyColors(UGraphic ug) {
		if (colors != null && colors.isOk()) {
			return colors.apply(ug);
		}
		if (isDiamond()) {
			return ug.apply(new UChangeColor(HtmlColorUtils.BLACK)).apply(new UChangeBackColor(HtmlColorUtils.BLACK));
		}
		return ug.apply(new UChangeColor(HtmlColorUtils.BLUE)).apply(new UChangeBackColor(defaultColor));
	}

	private UShape getShape(int load) {
		if (isDiamond()) {
			return getDiamond();
		}
		final Instant instantStart = task.getStart();
		final Instant instantEnd = task.getEnd();
		final double start = timeScale.getStartingPosition(instantStart);
		final double end = timeScale.getEndingPosition(instantEnd);
		return new URectangle(end - start - 2 * margin, getShapeHeight(load), 8, 8);
	}

	private double getShapeHeight(int load) {
		return (getHeight() - 2 * margin) * load / 100.0;
	}

	private boolean isDiamond() {
		if (task.isDiamond()) {
			final Instant instantStart = task.getStart();
			final Instant instantEnd = task.getEnd();
			return instantStart.compareTo(instantEnd) == 0;
		}
		return false;
	}

	private UShape getDiamond() {
		final double h = getHeight() - 2 * margin;
		final UPolygon result = new UPolygon();
		result.addPoint(h / 2, 0);
		result.addPoint(h, h / 2);
		result.addPoint(h / 2, h);
		result.addPoint(0, h / 2);
		return result;
	}

	public double getHeight() {
		return 16;
	}

	public double getY() {
		return y;
	}

	public double getY(Direction direction) {
		if (direction == Direction.UP) {
			return y;
		}
		if (direction == Direction.DOWN) {
			return y + getHeight();
		}
		return y + getHeight() / 2;
	}

	public void setColors(ComplementColors colors) {
		this.colors = colors;
	}
}
