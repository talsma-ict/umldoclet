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
package net.sourceforge.plantuml.svek.extremity;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class ExtremityDoubleLine extends Extremity {

	private final Point2D contact;
	private final double angle;
	private final double lineHeight = 4;

	@Override
	public Point2D somePoint() {
		return contact;
	}

	public ExtremityDoubleLine(Point2D p1, double angle) {
		this.contact = new Point2D.Double(p1.getX(), p1.getY());
		this.angle = manageround(angle + Math.PI / 2);
	}

	public void drawU(UGraphic ug) {
		final int xWing = 4;
		final AffineTransform rotate = AffineTransform.getRotateInstance(this.angle);
		Point2D firstLineTop = new Point2D.Double(-xWing, -lineHeight);
		Point2D firstLineBottom = new Point2D.Double(-xWing, lineHeight);
		Point2D secondLineTop = new Point2D.Double(-xWing - 3, -lineHeight);
		Point2D secondLineBottom = new Point2D.Double(-xWing - 3, lineHeight);

		Point2D middle = new Point2D.Double(0, 0);
		Point2D base = new Point2D.Double(-xWing - 4, 0);

		rotate.transform(middle, middle);
		rotate.transform(base, base);

		rotate.transform(firstLineTop, firstLineTop);
		rotate.transform(firstLineBottom, firstLineBottom);
		rotate.transform(secondLineTop, secondLineTop);
		rotate.transform(secondLineBottom, secondLineBottom);

		drawLine(ug, contact.getX(), contact.getY(), firstLineTop, firstLineBottom);
		drawLine(ug, contact.getX(), contact.getY(), secondLineTop, secondLineBottom);
		drawLine(ug, contact.getX(), contact.getY(), base, middle);
	}

	static private void drawLine(UGraphic ug, double x, double y, Point2D p1, Point2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(x + p1.getX(), y + p1.getY())).draw(new ULine(dx, dy));
	}

}
