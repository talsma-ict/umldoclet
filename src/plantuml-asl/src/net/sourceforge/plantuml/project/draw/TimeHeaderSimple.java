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

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.project.time.Wink;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.project.timescale.TimeScaleWink;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class TimeHeaderSimple extends TimeHeader {

	@Override
	public double getFullHeaderHeight() {
		return getTimeHeaderHeight() + getHeaderNameDayHeight();
	}

	private double getTimeHeaderHeight() {
		return 16;
	}

	private double getHeaderNameDayHeight() {
		return 0;
	}

	public TimeHeaderSimple(Wink min, Wink max) {
		super(min, max, new TimeScaleWink());
	}

	@Override
	public void drawTimeHeader(final UGraphic ug, double totalHeight) {
		final double xmin = getTimeScale().getStartingPosition(min);
		final double xmax = getTimeScale().getEndingPosition(max);
		drawSimpleDayCounter(ug, getTimeScale(), totalHeight);
		ug.apply(HColorUtils.LIGHT_GRAY).draw(ULine.hline(xmax - xmin));
		ug.apply(HColorUtils.LIGHT_GRAY).apply(UTranslate.dy(getFullHeaderHeight() - 3))
				.draw(ULine.hline(xmax - xmin));

	}

	private void drawSimpleDayCounter(final UGraphic ug, TimeScale timeScale, double totalHeight) {
		final ULine vbar = ULine.vline(totalHeight);
		for (Wink i = min; i.compareTo(max.increment()) <= 0; i = i.increment()) {
			final TextBlock num = Display.getWithNewlines(i.toShortString()).create(getFontConfiguration(10, false, HColorUtils.BLACK),
					HorizontalAlignment.LEFT, new SpriteContainerEmpty());
			final double x1 = timeScale.getStartingPosition(i);
			final double x2 = timeScale.getEndingPosition(i);
			final double width = num.calculateDimension(ug.getStringBounder()).getWidth();
			final double delta = (x2 - x1) - width;
			if (i.compareTo(max.increment()) < 0) {
				num.drawU(ug.apply(UTranslate.dx(x1 + delta / 2)));
			}
			ug.apply(HColorUtils.LIGHT_GRAY).apply(UTranslate.dx(x1)).draw(vbar);
		}
	}

}
