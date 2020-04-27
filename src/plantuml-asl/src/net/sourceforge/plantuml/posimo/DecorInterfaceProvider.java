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
package net.sourceforge.plantuml.posimo;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.cucadiagram.LinkStyle;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class DecorInterfaceProvider implements Decor {

	private final double radius = 5;
	private final double radius2 = 9;
	private final LinkStyle style;

	// private final double distanceCircle = 16;

	public DecorInterfaceProvider(LinkStyle style) {
//		if (style != LinkStyle.__toremove_INTERFACE_PROVIDER && style != LinkStyle.__toremove_INTERFACE_USER) {
//			throw new IllegalArgumentException();
//		}
		this.style = style;
	}

	public void drawDecor(UGraphic ug, Point2D start, double direction) {
		final double cornerX = start.getX() - radius;
		final double cornerY = start.getY() - radius;
		final double cornerX2 = start.getX() - radius2 - 0 * Math.sin(direction * Math.PI / 180.0);
		final double cornerY2 = start.getY() - radius2 - 0 * Math.cos(direction * Math.PI / 180.0);

//		if (style == LinkStyle.__toremove_INTERFACE_USER) {
//			direction += 180;
//		}
		if (direction >= 360) {
			direction -= 360;
		}

		final UEllipse arc = new UEllipse(2 * radius2, 2 * radius2, direction + 15, 180 - 30);
		ug = ug.apply(new UStroke(1.5));
		ug.apply(new UTranslate(cornerX2, cornerY2)).draw(arc);
		ug.apply(new UTranslate(cornerX, cornerY)).draw(new UEllipse(2 * radius, 2 * radius));
	}

}
