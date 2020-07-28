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
package net.sourceforge.plantuml.geom;

public class Neighborhood {

	final private double angle1;
	final private double angle2;
	final private Point2DInt center;

	public Neighborhood(Point2DInt center) {
		this(center, 0, 0);
	}

	public boolean is360() {
		return angle1 == angle2;
	}

	public Neighborhood(Point2DInt center, double angle1, double angle2) {
		this.center = center;
		this.angle1 = angle1;
		this.angle2 = angle2;
	}

	@Override
	public String toString() {
		final int a1 = (int) (angle1 * 180 / Math.PI);
		final int a2 = (int) (angle2 * 180 / Math.PI);
		return center + " " + a1 + " " + a2;
	}

	public final Point2DInt getCenter() {
		return center;
	}

	public final double getMiddle() {
		if (is360()) {
			return angle1 + Math.PI;
		}
		double result = (angle1 + angle2) / 2;
		if (angle2 < angle1) {
			result += Math.PI;
		}
		return result;
	}

	public boolean isInAngleStrict(double angle) {
		if (angle < 0) {
			throw new IllegalArgumentException();
		}
		if (angle2 > angle1) {
			return angle > angle1 && angle < angle2;
		}
		return angle > angle1 || angle < angle2;
	}

	public boolean isInAngleLarge(double angle) {
		if (angle < 0) {
			throw new IllegalArgumentException();
		}
		if (angle2 > angle1) {
			return angle >= angle1 && angle <= angle2;
		}
		return angle >= angle1 || angle <= angle2;
	}

	public boolean isAngleLimit(double angle) {
		return angle == angle1 || angle == angle2;
	}

	public Orientation getOrientationFrom(double angle) {
		if (angle1 == angle2) {
			throw new IllegalStateException();
		}
		if (angle != angle1 && angle != angle2) {
			throw new IllegalArgumentException("this=" + this + " angle=" + (int) (angle * 180 / Math.PI));
		}
		assert angle == angle1 || angle == angle2;

		if (angle == angle1) {
			return Orientation.MATH;
		}
		return Orientation.CLOCK;

	}
}
