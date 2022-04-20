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
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.svek.Side;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class ExtremityCrowfoot extends Extremity {

	private final Point2D contact;
	private double angle;
	private final Side side;

	@Override
	public Point2D somePoint() {
		return contact;
	}

	public ExtremityCrowfoot(Point2D p1, double angle, Side side) {
		this.contact = new Point2D.Double(p1.getX(), p1.getY());
		this.angle = manageround(angle + Math.PI / 2);
		this.side = side;
	}

	public void drawU(UGraphic ug) {
		final int xWing = 8;
		final int yAperture = 8;
		final AffineTransform rotate = AffineTransform.getRotateInstance(this.angle);
		Point2D middle = new Point2D.Double(0, 0);
		Point2D left = new Point2D.Double(0, -yAperture);
		Point2D base = new Point2D.Double(-xWing, 0);
		Point2D right = new Point2D.Double(0, yAperture);
		rotate.transform(left, left);
		rotate.transform(base, base);
		rotate.transform(right, right);

		if (side == Side.WEST || side == Side.EAST) {
			left.setLocation(middle.getX(), left.getY());
			right.setLocation(middle.getX(), right.getY());
		}
		if (side == Side.SOUTH || side == Side.NORTH) {
			left.setLocation(left.getX(), middle.getY());
			right.setLocation(right.getX(), middle.getY());
		}

		drawLine(ug, contact.getX(), contact.getY(), base, left);
		drawLine(ug, contact.getX(), contact.getY(), base, right);
		drawLine(ug, contact.getX(), contact.getY(), base, middle);
	}

	static private void drawLine(UGraphic ug, double x, double y, Point2D p1, Point2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(x + p1.getX(), y + p1.getY())).draw(new ULine(dx, dy));

	}

}
