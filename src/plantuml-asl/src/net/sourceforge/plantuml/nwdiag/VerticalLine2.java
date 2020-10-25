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
package net.sourceforge.plantuml.nwdiag;

import java.util.Set;

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;

public class VerticalLine2 implements UDrawable {

	private final double y1;
	private final double y2;
	private final Set<Double> skip;

	public VerticalLine2(double y1, double y2, Set<Double> skip) {
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.skip = skip;
	}

	public void drawU(UGraphic ug) {
		boolean drawn = false;
		double current = y1;
		for (Double step : skip) {
			if (step < y1) {
				continue;
			}
			assert step >= y1;
			drawn = true;
			if (step == y2) {
				drawVLine(ug, current, y2);
			} else {
				drawVLine(ug, current, Math.min(y2, step - 3));
				if (y2 > step) {
					drawArc(ug, step - 3);
				}
			}
			current = step + 9;
			if (current >= y2) {
				break;
			}
		}
		if (drawn == false) {
			drawVLine(ug, y1, y2);
		}

	}

	private void drawArc(UGraphic ug, double y) {
		final UEllipse arc = new UEllipse(11, 11, 90, -180);
		ug.apply(new HColorNone().bg()).apply(new UTranslate(-5, y)).draw(arc);

	}

	private void drawVLine(UGraphic ug, double start, double end) {
		final ULine line = ULine.vline(end - start);
		ug.apply(UTranslate.dy(start)).draw(line);
	}

}
