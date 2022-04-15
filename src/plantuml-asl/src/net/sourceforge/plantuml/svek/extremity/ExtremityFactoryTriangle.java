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

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.svek.AbstractExtremityFactory;
import net.sourceforge.plantuml.svek.Side;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class ExtremityFactoryTriangle extends AbstractExtremityFactory implements ExtremityFactory {

	private final HColor backgroundColor;
	private final int xWing;
	private final int yAperture;

	public ExtremityFactoryTriangle(HColor backgroundColor, int xWing, int yAperture) {
		this.backgroundColor = backgroundColor;
		this.xWing = xWing;
		this.yAperture = yAperture;
	}

	@Override
	public UDrawable createUDrawable(Point2D p0, double angle, Side side) {
		return new ExtremityTriangle(p0, angle - Math.PI / 2, false, backgroundColor, xWing, yAperture);
	}

	public UDrawable createUDrawable(Point2D p0, Point2D p1, Point2D p2, Side side) {
		final double ortho = atan2(p0, p2);
		return new ExtremityTriangle(p1, ortho, true, backgroundColor, xWing, yAperture);
	}

}
