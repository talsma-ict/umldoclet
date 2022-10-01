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
package net.sourceforge.plantuml.svek.extremity;

import java.awt.geom.AffineTransform;

import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.svek.Side;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class ExtremityCrowfoot extends Extremity {

	private final XPoint2D contact;
	private double angle;
	private final Side side;

	@Override
	public XPoint2D somePoint() {
		return contact;
	}

	public ExtremityCrowfoot(XPoint2D p1, double angle, Side side) {
		this.contact = new XPoint2D(p1.getX(), p1.getY());
		this.angle = manageround(angle + Math.PI / 2);
		this.side = side;
	}

	public void drawU(UGraphic ug) {
		final int xWing = 8;
		final int yAperture = 8;
		final AffineTransform rotate = AffineTransform.getRotateInstance(this.angle);
		XPoint2D middle = new XPoint2D(0, 0);
		XPoint2D left = new XPoint2D(0, -yAperture);
		XPoint2D base = new XPoint2D(-xWing, 0);
		XPoint2D right = new XPoint2D(0, yAperture);
		left = left.transform(rotate);
		base = base.transform(rotate);
		right = right.transform(rotate);

		if (side == Side.WEST || side == Side.EAST) {
			left = new XPoint2D(middle.getX(), left.getY());
			right = new XPoint2D(middle.getX(), right.getY());
		}
		if (side == Side.SOUTH || side == Side.NORTH) {
			left = new XPoint2D(left.getX(), middle.getY());
			right = new XPoint2D(right.getX(), middle.getY());
		}

		drawLine(ug, contact.getX(), contact.getY(), base, left);
		drawLine(ug, contact.getX(), contact.getY(), base, right);
		drawLine(ug, contact.getX(), contact.getY(), base, middle);
	}

	static private void drawLine(UGraphic ug, double x, double y, XPoint2D p1, XPoint2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(x + p1.getX(), y + p1.getY())).draw(new ULine(dx, dy));

	}

}
