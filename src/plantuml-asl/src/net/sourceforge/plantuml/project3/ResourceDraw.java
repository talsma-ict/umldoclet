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

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ResourceDraw implements UDrawable {

	private final Resource res;
	private final TimeScale timeScale;
	private final double y;
	private final Instant min;
	private final Instant max;
	private final GanttDiagram gantt;

	public ResourceDraw(GanttDiagram gantt, Resource res, TimeScale timeScale, double y, Instant min, Instant max) {
		this.res = res;
		this.timeScale = timeScale;
		this.y = y;
		this.min = min;
		this.max = max;
		this.gantt = gantt;
	}

	public void drawU(UGraphic ug) {
		final TextBlock title = Display.getWithNewlines(res.getName()).create(getFontConfiguration(13),
				HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		title.drawU(ug);
		final ULine line = new ULine(timeScale.getEndingPosition(max) - timeScale.getStartingPosition(min), 0);
		ug.apply(new UChangeColor(HtmlColorUtils.BLACK))
				.apply(new UTranslate(0, title.calculateDimension(ug.getStringBounder()).getHeight())).draw(line);
		for (Instant i = min; i.compareTo(max) <= 0; i = i.increment()) {
			final int load = gantt.getLoadForResource(res, i);
			if (load > 0) {
				final FontConfiguration fontConfiguration = getFontConfiguration(9, load > 100 ? HtmlColorUtils.RED
						: HtmlColorUtils.BLACK);
				final TextBlock value = Display.getWithNewlines("" + load).create(fontConfiguration,
						HorizontalAlignment.LEFT, new SpriteContainerEmpty());
				final double start = (timeScale.getStartingPosition(i) + timeScale.getEndingPosition(i)) / 2
						- value.calculateDimension(ug.getStringBounder()).getWidth() / 2;
				value.drawU(ug.apply(new UTranslate(start, 16)));
			}

		}

	}

	private FontConfiguration getFontConfiguration(int size) {
		return getFontConfiguration(size, HtmlColorUtils.BLACK);
	}

	private FontConfiguration getFontConfiguration(int size, HtmlColor color) {
		final UFont font = UFont.serif(size);
		return new FontConfiguration(font, color, color, false);
	}

	// public void setColors(ComplementColors colors);
	//
	// public double getY();
	//
	// public double getY(Direction direction);
	//
	// public void drawTitle(UGraphic ug);

	public double getHeight() {
		return 16 * 2;
	}

	public double getY() {
		return y;
	}

}
