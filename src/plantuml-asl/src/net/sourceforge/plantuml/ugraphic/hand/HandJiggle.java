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
package net.sourceforge.plantuml.ugraphic.hand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.sourceforge.plantuml.awt.geom.XCubicCurve2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;

public class HandJiggle {
	private final Collection<XPoint2D> points = new ArrayList<>();

	private double startX;
	private double startY;
	private final double defaultVariation;
	private final Random rnd;

	private double randomMe() {
		return rnd.nextDouble();
	}

	public HandJiggle(double startX, double startY, double defaultVariation, Random rnd) {
		this.startX = startX;
		this.startY = startY;
		this.defaultVariation = defaultVariation;
		this.rnd = rnd;
		points.add(new XPoint2D(startX, startY));
	}

	public static HandJiggle create(XPoint2D start, double defaultVariation, Random rnd) {
		return new HandJiggle(start.getX(), start.getY(), defaultVariation, rnd);
	}

	public void lineTo(XPoint2D end) {
		lineTo(end.getX(), end.getY());
	}

	public void arcTo(double angle0, double angle1, double centerX, double centerY, double rx, double ry) {
		lineTo(pointOnCircle(centerX, centerY, (angle0 + angle1) / 2, rx, ry));
		lineTo(pointOnCircle(centerX, centerY, angle1, rx, ry));
	}

	private static XPoint2D pointOnCircle(double centerX, double centerY, double angle, double rx, double ry) {
		final double x = centerX + Math.cos(angle) * rx;
		final double y = centerY + Math.sin(angle) * ry;
		return new XPoint2D(x, y);

	}

	public void lineTo(final double endX, final double endY) {

		final double diffX = Math.abs(endX - startX);
		final double diffY = Math.abs(endY - startY);
		final double distance = Math.sqrt(diffX * diffX + diffY * diffY);
		if (distance < 0.001)
			return;

		int segments = (int) Math.round(distance / 10);
		double variation = defaultVariation;
		if (segments < 5) {
			segments = 5;
			variation /= 3;
		}

		final double stepX = Math.signum(endX - startX) * diffX / segments;
		final double stepY = Math.signum(endY - startY) * diffY / segments;

		final double fx = diffX / distance;
		final double fy = diffY / distance;

		for (int s = 0; s < segments; s++) {
			double x = stepX * s + startX;
			double y = stepY * s + startY;

			final double offset = (randomMe() - 0.5) * variation;
			points.add(new XPoint2D(x - offset * fy, y - offset * fx));
		}
		points.add(new XPoint2D(endX, endY));

		this.startX = endX;
		this.startY = endY;
	}

	public void curveTo(XCubicCurve2D curve) {
		final double flatness = curve.getFlatness();
		final double dist = curve.getP1().distance(curve.getP2());
		if (flatness > 0.1 && dist > 20) {
			final XCubicCurve2D left = new XCubicCurve2D();
			final XCubicCurve2D right = new XCubicCurve2D();
			curve.subdivide(left, right);
			curveTo(left);
			curveTo(right);
			return;
		}
		lineTo(curve.getP2());
	}

	public UPolygon toUPolygon() {
		final UPolygon result = new UPolygon();
		for (XPoint2D p : points)
			result.addPoint(p.getX(), p.getY());

		return result;
	}

	public UPath toUPath() {
		UPath path = null;
		for (XPoint2D p : points) {
			if (path == null) {
				path = new UPath();
				path.moveTo(p);
			} else {
				path.lineTo(p);
			}
		}
		if (path == null)
			throw new IllegalStateException();

		return path;
	}

	public void appendTo(UPath result) {
		for (XPoint2D p : points)
			result.lineTo(p);

	}

}
