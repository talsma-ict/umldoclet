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
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.ULine;

class ExtremityHalfArrow extends Extremity {

	private final ULine line;
	private final ULine otherLine;
	private final XPoint2D contact;

	@Override
	public XPoint2D somePoint() {
		return contact;
	}

	public ExtremityHalfArrow(XPoint2D p1, double angle, XPoint2D center) {
		angle = manageround(angle);
		final AffineTransform rotate = AffineTransform.getRotateInstance(angle + Math.PI / 2);
		final int xWing = 9;
		final int yAperture = 4;
		XPoint2D other = new XPoint2D(-xWing, -yAperture);
		other = other.transform(rotate);

		this.contact = p1;
		this.line = new ULine(center.getX() - contact.getX(), center.getY() - contact.getY());
		this.otherLine = new ULine(other.getX(), other.getY());
	}

	public ExtremityHalfArrow(XPoint2D p0, double angle) {
		throw new UnsupportedOperationException();
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(HColors.changeBack(ug));
		if (line != null && line.getLength() > 2) {
			ug.apply(new UTranslate(contact.getX(), contact.getY())).draw(line);
			ug.apply(new UTranslate(contact.getX(), contact.getY())).draw(otherLine);
		}
	}

}
