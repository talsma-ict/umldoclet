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
package net.sourceforge.plantuml.wbs;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XLine2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.awt.geom.XRectangle2D;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.svek.extremity.ExtremityArrow;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColors;

class WBSLink implements UDrawable {

	private final WElement element1;
	private final WElement element2;

	public WBSLink(WElement element1, WElement element2) {
		this.element1 = element1;
		this.element2 = element2;
	}

	public final WElement getElement1() {
		return element1;
	}

	public final WElement getElement2() {
		return element2;
	}

	public void drawU(UGraphic ug) {
		final WElement element1 = getElement1();
		final WElement element2 = getElement2();
		final UTranslate position1 = element1.getPosition();
		final UTranslate position2 = element2.getPosition();
		final XDimension2D dim1 = element1.getDimension();
		final XDimension2D dim2 = element2.getDimension();
		if (position1 != null && position2 != null) {

			final XRectangle2D rect1 = new XRectangle2D(position1.getDx(), position1.getDy(), dim1.getWidth(),
					dim1.getHeight());
			final XRectangle2D rect2 = new XRectangle2D(position2.getDx(), position2.getDy(), dim2.getWidth(),
					dim2.getHeight());

			XLine2D line = new XLine2D(rect1.getCenterX(), rect1.getCenterY(), rect2.getCenterX(), rect2.getCenterY());

			final XPoint2D c1 = rect1.intersect(line);
			final XPoint2D c2 = rect2.intersect(line);

			line = new XLine2D(c1, c2);
			ug = ug.apply(HColors.RED);
			line.drawU(ug);

			final double angle = line.getAngle();
			final ExtremityArrow arrow = new ExtremityArrow(c2, angle);
			arrow.drawU(ug);

		}

	}

}
