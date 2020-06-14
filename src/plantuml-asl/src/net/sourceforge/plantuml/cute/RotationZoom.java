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

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ugraphic.UTranslate;

public class RotationZoom {

	private final double angle;
	private final double zoom;

	private RotationZoom(double angle, double zoom) {
		if (zoom < 0) {
			throw new IllegalArgumentException();
		}
		this.angle = angle;
		this.zoom = zoom;
	}

	public RotationZoom compose(RotationZoom other) {
		return new RotationZoom(this.angle + other.angle, this.zoom * other.zoom);
	}

	@Override
	public String toString() {
		return "Rotation=" + Math.toDegrees(angle) + " Zoom=" + zoom;
	}

	public static RotationZoom fromVarArgs(VarArgs varArgs) {
		final double radians = Math.toRadians(varArgs.getAsDouble("rotation", 0));
		final double scale = varArgs.getAsDouble("scale", 1);
		return new RotationZoom(radians, scale);
	}

	public static RotationZoom rotationInDegrees(double angle) {
		return new RotationZoom(Math.toRadians(angle), 1);
	}

	public static RotationZoom rotationInRadians(double angle) {
		return new RotationZoom(angle, 1);
	}

	public static RotationZoom zoom(double zoom) {
		return new RotationZoom(0, zoom);
	}

	public RotationZoom inverse() {
		return new RotationZoom(-angle, 1 / zoom);
	}

	public double getAngleDegree() {
		return Math.toDegrees(angle);
	}

	static public RotationZoom builtRotationOnYaxis(Point2D toRotate) {
		final double a = Math.atan2(toRotate.getX(), toRotate.getY());
		return new RotationZoom(a, 1);
	}

	public Point2D.Double getPoint(double x, double y) {
		if (angle == 0) {
			return new Point2D.Double(x * zoom, y * zoom);
		}
		final double x1 = Math.cos(angle) * x - Math.sin(angle) * y;
		final double y1 = Math.sin(angle) * x + Math.cos(angle) * y;
		return new Point2D.Double(x1 * zoom, y1 * zoom);
	}

	public Point2D getPoint(Point2D p) {
		return getPoint(p.getX(), p.getY());
	}

	public UTranslate getUTranslate(UTranslate translate) {
		return new UTranslate(getPoint(translate.getDx(), translate.getDy()));

	}

	public static RotationZoom none() {
		return new RotationZoom(0, 1);
	}

	public boolean isNone() {
		return angle == 0 && zoom == 1;
	}

	public double applyZoom(double value) {
		return value * zoom;
	}

	public double applyRotation(double alpha) {
		return angle + alpha;
	}

}
