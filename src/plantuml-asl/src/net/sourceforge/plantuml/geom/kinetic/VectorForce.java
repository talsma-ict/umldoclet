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
package net.sourceforge.plantuml.geom.kinetic;

import java.awt.geom.Point2D;

public class VectorForce {

	private final double x;
	private final double y;

	public VectorForce(double x, double y) {
		if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) {
			throw new IllegalArgumentException();
		}
		this.x = x;
		this.y = y;
	}

	public VectorForce(Point2D src, Point2D dest) {
		this(dest.getX() - src.getX(), dest.getY() - src.getY());
	}

	public VectorForce plus(VectorForce other) {
		return new VectorForce(this.x + other.x, this.y + other.y);
	}

	public VectorForce multiply(double v) {
		return new VectorForce(x * v, y * v);
	}

	@Override
	public String toString() {
		return String.format("{%8.2f %8.2f}", x, y);
	}

	public VectorForce negate() {
		return new VectorForce(-x, -y);
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public VectorForce normaliseTo(double newLength) {
		if (Double.isInfinite(newLength) || Double.isNaN(newLength)) {
			throw new IllegalArgumentException();
		}
		final double actualLength = length();
		if (actualLength == 0) {
			return this;
		}
		final double f = newLength / actualLength;
		return new VectorForce(x * f, y * f);

	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public double getLength() {
		return Math.sqrt(x * x + y * y);
	}
}
