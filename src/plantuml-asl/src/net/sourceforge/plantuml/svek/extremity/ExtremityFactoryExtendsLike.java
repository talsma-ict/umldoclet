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
 * Creator:  Hisashi Miyashita
 */
package net.sourceforge.plantuml.svek.extremity;

import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.geom.Side;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.svek.AbstractExtremityFactory;

public class ExtremityFactoryExtendsLike extends AbstractExtremityFactory implements ExtremityFactory {

	private final HColor backgroundColor;
	private final boolean definedBy;

	public ExtremityFactoryExtendsLike(HColor backgroundColor, boolean definedBy) {
		this.backgroundColor = backgroundColor;
		this.definedBy = definedBy;
	}

	@Override
	public UDrawable createUDrawable(XPoint2D p0, double angle, Side side) {
		if (definedBy) {
			return new ExtremityExtendsLike.DefinedBy(p0, angle, backgroundColor);
		} else {
			return new ExtremityExtendsLike.Redefines(p0, angle, backgroundColor);
		}
	}

	@Override
	public UDrawable createUDrawable(XPoint2D p0, XPoint2D p1, XPoint2D p2, Side side) {
		final double ortho = atan2(p0, p2) + (Math.PI / 2.0);
		if (definedBy) {
			return new ExtremityExtendsLike.DefinedBy(p1, ortho, backgroundColor);
		} else {
			return new ExtremityExtendsLike.Redefines(p1, ortho, backgroundColor);
		}
	}
}
