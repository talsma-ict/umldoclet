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
package net.sourceforge.plantuml.ugraphic.hand;

import java.util.Random;

import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;

public class URectangleHand {

	final private UPolygon poly;

	public URectangleHand(URectangle rectangle, Random rnd) {
		final double width = rectangle.getWidth();
		final double height = rectangle.getHeight();
		final HandJiggle jiggle;
		final double rx = Math.min(rectangle.getRx() / 2, width / 2);
		final double ry = Math.min(rectangle.getRy() / 2, height / 2);
		// System.err.println("rx=" + rx + " ry=" + ry);
		if (rx == 0 && ry == 0) {
			jiggle = new HandJiggle(0, 0, 1.5, rnd);
			jiggle.lineTo(width, 0);
			jiggle.lineTo(width, height);
			jiggle.lineTo(0, height);
			jiggle.lineTo(0, 0);
		} else {
			jiggle = new HandJiggle(rx, 0, 1.5, rnd);
			jiggle.lineTo(width - rx, 0);
			jiggle.arcTo(-Math.PI / 2, 0, width - rx, ry, rx, ry);
			jiggle.lineTo(width, height - ry);
			jiggle.arcTo(0, Math.PI / 2, width - rx, height - ry, rx, ry);
			jiggle.lineTo(rx, height);
			jiggle.arcTo(Math.PI / 2, Math.PI, rx, height - ry, rx, ry);
			jiggle.lineTo(0, ry);
			jiggle.arcTo(Math.PI, 3 * Math.PI / 2, rx, ry, rx, ry);
		}

		this.poly = jiggle.toUPolygon();
		this.poly.setDeltaShadow(rectangle.getDeltaShadow());
	}

	public Shadowable getHanddrawn() {
		return this.poly;
	}

}
