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

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UDrawable;

public abstract class Extremity implements UDrawable {

	protected double manageround(double angle) {
		final double deg = angle * 180.0 / Math.PI;
		if (isCloseTo(0, deg))
			return 0;

		if (isCloseTo(90, deg))
			return 90.0 * Math.PI / 180.0;

		if (isCloseTo(180, deg))
			return 180.0 * Math.PI / 180.0;

		if (isCloseTo(270, deg))
			return 270.0 * Math.PI / 180.0;

		if (isCloseTo(360, deg))
			return 0;

		return angle;
	}

	private boolean isCloseTo(double value, double variable) {
		if (Math.abs(value - variable) < 0.05)
			return true;

		return false;
	}

	public abstract XPoint2D somePoint();

	public XPoint2D isTooSmallSoGiveThePointCloserToThisOne(XPoint2D pt) {
		return null;
	}

	public UTranslate getDeltaForKal() {
		return UTranslate.none();
	}

}
