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

import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.URectangle;

class ExtremitySquare extends Extremity {
    // ::remove folder when __HAXE__

	private final HColor backgroundColor;
	private final XPoint2D dest;
	private final double radius = 5;

	@Override
	public XPoint2D somePoint() {
		return dest;
	}

	public ExtremitySquare(XPoint2D p1, HColor backgroundColor) {
		this.dest = new XPoint2D(p1.getX(), p1.getY());
		this.backgroundColor = backgroundColor;
	}

	public void drawU(UGraphic ug) {
		ug.apply(UStroke.withThickness(1.5)).apply(backgroundColor.bg())
				.apply(new UTranslate(dest.getX() - radius, dest.getY() - radius))
				.draw(URectangle.build(radius * 2, radius * 2));
	}

}
