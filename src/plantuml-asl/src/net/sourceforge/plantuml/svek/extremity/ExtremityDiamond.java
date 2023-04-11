/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.svek.extremity;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UPolygon;

class ExtremityDiamond extends Extremity {

	private UPolygon polygon = new UPolygon();
	private final boolean fill;
	private final XPoint2D contact;
	private final UTranslate deltaForKal;

	@Override
	public XPoint2D somePoint() {
		return contact;
	}

	public ExtremityDiamond(XPoint2D p1, double angle, boolean fill) {
		this.fill = fill;
		this.contact = new XPoint2D(p1.getX(), p1.getY());
		angle = manageround(angle);
		polygon.addPoint(0, 0);
		final int xWing = 6;
		final int yAperture = 4;
		polygon.addPoint(-xWing, -yAperture);
		polygon.addPoint(-xWing * 2, 0);
		polygon.addPoint(-xWing, yAperture);
		polygon.addPoint(0, 0);
		polygon.rotate(angle + Math.PI / 2);
		this.deltaForKal = UTranslate.point(polygon.getPoint(2)).reverse();
		polygon = polygon.translate(p1.getX(), p1.getY());
	}

	@Override
	public UTranslate getDeltaForKal() {
		return deltaForKal;
	}

	public void drawU(UGraphic ug) {
		if (fill)
			ug = ug.apply(HColors.changeBack(ug));
		else
			ug = ug.apply(HColors.none().bg());

		ug.draw(polygon);
	}

	@Override
	public XPoint2D isTooSmallSoGiveThePointCloserToThisOne(XPoint2D pt) {
		XPoint2D result = null;
		for (XPoint2D p : polygon.getPoints())
			if (result == null || p.distance(pt) < result.distance(pt))
				result = p;

		return result;
	}

}
