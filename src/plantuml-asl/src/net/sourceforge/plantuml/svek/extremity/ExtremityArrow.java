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

import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class ExtremityArrow extends Extremity {

	private UPolygon polygon = new UPolygon();
	private final ULine line;
	private final XPoint2D contact;

	@Override
	public XPoint2D somePoint() {
		return contact;
	}

	public ExtremityArrow(XPoint2D p1, double angle, XPoint2D center) {
		angle = manageround(angle);
		final int xContact = buildPolygon();
		polygon.rotate(angle + Math.PI / 2);
		polygon = polygon.translate(p1.getX(), p1.getY());
		contact = new XPoint2D(p1.getX() - xContact * Math.cos(angle + Math.PI / 2),
				p1.getY() - xContact * Math.sin(angle + Math.PI / 2));
		this.line = new ULine(center.getX() - contact.getX(), center.getY() - contact.getY());
	}

	public ExtremityArrow(XPoint2D p0, double angle) {
		this.line = null;
		angle = manageround(angle);
		buildPolygon();
		polygon.rotate(angle);
		polygon = polygon.translate(p0.getX(), p0.getY());
		contact = p0;
	}

	private int buildPolygon() {
		polygon.addPoint(0, 0);
		final int xWing = 9;
		final int yAperture = 4;
		polygon.addPoint(-xWing, -yAperture);
		final int xContact = 5;
		polygon.addPoint(-xContact, 0);
		polygon.addPoint(-xWing, yAperture);
		polygon.addPoint(0, 0);
		return xContact;
	}

	public void drawU(UGraphic ug) {
		final HColor color = ug.getParam().getColor();
		if (color == null)
			ug = ug.apply(HColors.none().bg());
		else
			ug = ug.apply(color.bg());

		ug.draw(polygon);
		if (line != null && line.getLength() > 2)
			ug.apply(new UTranslate(contact)).draw(line);

	}

	public void drawLineIfTransparent(UGraphic ug) {
		final XPoint2D pt1 = polygon.getPoint(0);
		final XPoint2D pt2 = polygon.getPoint(2);
		final ULine line = new ULine(pt1, pt2);
		ug.apply(new UTranslate(pt1)).draw(line);

	}

}
