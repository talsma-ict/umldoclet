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
package net.sourceforge.plantuml.cute;

import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Circle implements CuteShape {

	private final double radius;

	public Circle(VarArgs varArgs) {
		this.radius = varArgs.getAsDouble("radius");
	}

	private Circle(double radius) {
		this.radius = radius;
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(new UTranslate(-radius, -radius));
		ug.draw(new UEllipse(2 * radius, 2 * radius));
	}

	public Circle rotateZoom(RotationZoom rotationZoom) {
		if (rotationZoom.isNone()) {
			return this;
		}
		return new Circle(rotationZoom.applyZoom(radius));
	}

}
