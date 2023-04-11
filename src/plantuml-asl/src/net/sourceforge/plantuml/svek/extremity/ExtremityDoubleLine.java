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

import java.awt.geom.AffineTransform;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.ULine;

class ExtremityDoubleLine extends Extremity {

	private final XPoint2D contact;
	private final double angle;
	private final double lineHeight = 4;

	@Override
	public XPoint2D somePoint() {
		return contact;
	}

	public ExtremityDoubleLine(XPoint2D p1, double angle) {
		this.contact = new XPoint2D(p1.getX(), p1.getY());
		this.angle = manageround(angle + Math.PI / 2);
	}

	public void drawU(UGraphic ug) {
		final int xWing = 4;
		final AffineTransform rotate = AffineTransform.getRotateInstance(this.angle);
		XPoint2D firstLineTop = new XPoint2D(-xWing, -lineHeight);
		XPoint2D firstLineBottom = new XPoint2D(-xWing, lineHeight);
		XPoint2D secondLineTop = new XPoint2D(-xWing - 3, -lineHeight);
		XPoint2D secondLineBottom = new XPoint2D(-xWing - 3, lineHeight);

		XPoint2D middle = new XPoint2D(0, 0);
		XPoint2D base = new XPoint2D(-xWing - 4, 0);

		middle = middle.transform(rotate);
		base = base.transform(rotate);
		firstLineTop = firstLineTop.transform(rotate);
		firstLineBottom = firstLineBottom.transform(rotate);
		secondLineTop = secondLineTop.transform(rotate);
		secondLineBottom = secondLineBottom.transform(rotate);

		drawLine(ug, contact.getX(), contact.getY(), firstLineTop, firstLineBottom);
		drawLine(ug, contact.getX(), contact.getY(), secondLineTop, secondLineBottom);
		drawLine(ug, contact.getX(), contact.getY(), base, middle);
	}

	static private void drawLine(UGraphic ug, double x, double y, XPoint2D p1, XPoint2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(x + p1.getX(), y + p1.getY())).draw(new ULine(dx, dy));
	}

}
