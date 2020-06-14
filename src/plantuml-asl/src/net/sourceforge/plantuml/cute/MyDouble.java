/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import java.util.StringTokenizer;

public class MyDouble {

	private static final double NO_CURVE = java.lang.Double.MIN_VALUE;
	private final double value;
	private final double curvation;

	public MyDouble(String s) {
		final StringTokenizer st = new StringTokenizer(s, ",");
		this.value = java.lang.Double.parseDouble(st.nextToken());
		if (st.hasMoreTokens()) {
			this.curvation = java.lang.Double.parseDouble(st.nextToken());
		} else {
			this.curvation = NO_CURVE;
		}
	}

	@Override
	public String toString() {
		return value + "[" + curvation + "]";
	}

	private MyDouble(double value, double curvation) {
		this.value = value;
		this.curvation = curvation;
	}

	public double getCurvation(double def) {
		if (curvation == NO_CURVE) {
			return def;
		}
		return curvation;
	}

	public double getValue() {
		return value;
	}

	public boolean hasCurvation() {
		return curvation != NO_CURVE;
	}

	public MyDouble rotateZoom(RotationZoom rotationZoom) {
		final double newValue = rotationZoom.applyZoom(value);
		final double curvation = this.curvation == NO_CURVE ? NO_CURVE : rotationZoom.applyZoom(this.curvation);
		return new MyDouble(newValue, curvation);
	}

	public MyDouble toRadians() {
		return new MyDouble(Math.toRadians(value), curvation);
	}

}
