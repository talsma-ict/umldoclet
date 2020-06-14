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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class Zad {

	private final List<MinMax> rectangles = new ArrayList<MinMax>();

	public void add(MinMax rect) {
		// System.err.println("add " + rect);
		this.rectangles.add(rect);

	}

	public void drawDebug(UGraphic ug) {
		ug = ug.apply(new UChangeBackColor(HtmlColorUtils.BLUE)).apply(new UChangeColor(HtmlColorUtils.RED_LIGHT));
		for (MinMax minMax : rectangles) {
			System.err.println("minmax=" + minMax);
			minMax.drawGrey(ug);
		}

	}

	public boolean doesHorizontalCross(Snake snake) {
		for (MinMax minMax : rectangles) {
			if (snake.doesHorizontalCross(minMax)) {
				return true;
			}
		}
		return false;
	}
}
