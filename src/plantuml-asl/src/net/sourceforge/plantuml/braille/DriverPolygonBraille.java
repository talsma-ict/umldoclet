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
package net.sourceforge.plantuml.braille;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;

public class DriverPolygonBraille implements UDriver<UPolygon, BrailleGrid> {

	private final ClipContainer clipContainer;

	public DriverPolygonBraille(ClipContainer clipContainer) {
		this.clipContainer = clipContainer;
	}

	public void draw(UPolygon shape, double x, double y, ColorMapper mapper, UParam param, BrailleGrid grid) {
		final List<Point2D> points = new ArrayList<>();
		int i = 0;

		for (Point2D pt : shape.getPoints()) {
			points.add(new UTranslate(x, y).getTranslated(pt));
		}

		final UClip clip = clipContainer.getClip();
		if (clip != null) {
			for (Point2D pt : points) {
				if (clip.isInside(pt) == false) {
					return;
				}
			}
		}

		grid.drawPolygon(points);
	}
}
