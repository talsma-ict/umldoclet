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
package net.sourceforge.plantuml.svek;

import net.sourceforge.plantuml.awt.geom.XCubicCurve2D;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.posimo.BezierUtils;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ClusterPosition {

	private final double minX;
	private final double minY;
	private final double maxX;
	private final double maxY;

	public ClusterPosition(double minX, double minY, double maxX, double maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public ClusterPosition(XPoint2D min, XPoint2D max) {
		this(min.x, min.y, max.x, max.y);
	}

	public ClusterPosition move(double deltaX, double deltaY) {
		return new ClusterPosition(minX + deltaX, minY + deltaY, maxX + deltaX, maxY + deltaY);
	}

	public double getWidth() {
		return maxX - minX;
	}

	public double getHeight() {
		return maxY - minY;
	}

	public boolean contains(double x, double y) {
		return x >= minX && x < maxX && y >= minY && y < maxY;
	}

	public ClusterPosition merge(ClusterPosition other) {
		return new ClusterPosition(Math.min(this.minX, other.minX), Math.min(this.minY, other.minY),
				Math.max(this.maxX, other.maxX), Math.max(this.maxY, other.maxY));
	}

	public ClusterPosition merge(XPoint2D point) {
		final double x = point.getX();
		final double y = point.getY();
		return new ClusterPosition(Math.min(this.minX, x), Math.min(this.minY, y), Math.max(this.maxX, x),
				Math.max(this.maxY, y));
	}

	public boolean contains(XPoint2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public String toString() {
		return "minX=" + minX + " maxX=" + maxX + " minY=" + minY + " maxY=" + maxY;
	}

	public final double getMinX() {
		return minX;
	}

	public final double getMinY() {
		return minY;
	}

	public final double getMaxX() {
		return maxX;
	}

	public final double getMaxY() {
		return maxY;
	}

	public PointDirected getIntersection(XCubicCurve2D bez) {
		if (contains(bez.x1, bez.y1) == contains(bez.x2, bez.y2))
			return null;

		final double dist = bez.getP1().distance(bez.getP2());
		if (dist < 2) {
			final double angle = BezierUtils.getStartingAngle(bez);
			return new PointDirected(bez.getP1(), angle);
		}
		final XCubicCurve2D left = new XCubicCurve2D();
		final XCubicCurve2D right = new XCubicCurve2D();
		bez.subdivide(left, right);
		final PointDirected int1 = getIntersection(left);
		if (int1 != null)
			return int1;

		final PointDirected int2 = getIntersection(right);
		if (int2 != null)
			return int2;

		throw new IllegalStateException();
	}

	public XPoint2D getPointCenter() {
		return new XPoint2D((minX + maxX) / 2, (minY + maxY) / 2);
	}

	public ClusterPosition withMinX(double d) {
		return new ClusterPosition(d, minY, maxX, maxY);
	}

	public ClusterPosition withMaxX(double d) {
		return new ClusterPosition(minX, minY, d, maxY);
	}

	public ClusterPosition addMaxX(double d) {
		return new ClusterPosition(minX, minY, maxX + d, maxY);
	}

	public ClusterPosition addMaxY(double d) {
		return new ClusterPosition(minX, minY, maxX, maxY + d);
	}

	public ClusterPosition addMinX(double d) {
		return new ClusterPosition(minX + d, minY, maxX, maxY);
	}

	public ClusterPosition addMinY(double d) {
		return new ClusterPosition(minX, minY + d, maxX, maxY);
	}

	public ClusterPosition withMinY(double d) {
		return new ClusterPosition(minX, d, maxX, maxY);
	}

	public ClusterPosition withMaxY(double d) {
		return new ClusterPosition(minX, minY, maxX, d);
	}

	public XPoint2D getProjectionOnFrontier(XPoint2D pt) {
		final double x = pt.getX();
		final double y = pt.getY();
		if (x > maxX && y >= minY && y <= maxY)
			return new XPoint2D(maxX - 1, y);

		if (x < minX && y >= minY && y <= maxY)
			return new XPoint2D(minX + 1, y);

		if (y > maxY && x >= minX && x <= maxX)
			return new XPoint2D(x, maxY - 1);

		if (y < minY && x >= minX && x <= maxX)
			return new XPoint2D(x, minY + 1);

		return new XPoint2D(x, y);
	}

	public ClusterPosition delta(double m1, double m2) {
		return new ClusterPosition(minX, minY, maxX + m1, maxY + m2);
	}

	public XDimension2D getDimension() {
		return new XDimension2D(maxX - minX, maxY - minY);
	}

	public UTranslate getPosition() {
		return new UTranslate(getMinX(), getMinY());
	}

	public boolean isPointJustUpper(XPoint2D pt) {
		if (pt.getX() >= minX && pt.getX() <= maxX && pt.getY() <= minY) {
			return true;
		}
		return false;
	}

	public Side getClosestSide(XPoint2D pt) {
		final double distNorth = Math.abs(minY - pt.getY());
		final double distSouth = Math.abs(maxY - pt.getY());
		final double distWest = Math.abs(minX - pt.getX());
		final double distEast = Math.abs(maxX - pt.getX());
		if (isSmallerThan(distNorth, distWest, distEast, distSouth))
			return Side.NORTH;

		if (isSmallerThan(distSouth, distNorth, distWest, distEast))
			return Side.SOUTH;

		if (isSmallerThan(distEast, distNorth, distWest, distSouth))
			return Side.EAST;

		if (isSmallerThan(distWest, distNorth, distEast, distSouth))
			return Side.WEST;

		return null;
	}

	private boolean isSmallerThan(double value, double a, double b, double c) {
		return value <= a && value <= b && value <= c;
	}

}
