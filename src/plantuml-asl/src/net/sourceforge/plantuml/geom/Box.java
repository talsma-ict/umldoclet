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

public class Box implements XMoveable, Pointable {

	private int x;
	private int y;
	final private int width;
	final private int height;

	public Box(int x, int y, int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return "Box [" + x + "," + y + "] " + width + "," + height;
	}

	public Point2DInt[] getCorners() {
		final Point2DInt p1 = new Point2DInt(getX(), getY());
		final Point2DInt p2 = new Point2DInt(getX() + getWidth(), getY());
		final Point2DInt p3 = new Point2DInt(getX() + getWidth(), getY() + getHeight());
		final Point2DInt p4 = new Point2DInt(getX(), getY() + getHeight());
		return new Point2DInt[] { p1, p2, p3, p4 };
	}

	public Point2DInt[] getCornersOfOneSide(LineSegmentInt seg, int sgn) {
		final Point2DInt[] corners = getCorners();
		final double sgn0 = seg.side(corners[0]);
		final double sgn1 = seg.side(corners[1]);
		final double sgn2 = seg.side(corners[2]);
		final double sgn3 = seg.side(corners[3]);
		int nb = 0;
		if (Math.signum(sgn0) == sgn) {
			nb++;
		}
		if (Math.signum(sgn1) == sgn) {
			nb++;
		}
		if (Math.signum(sgn2) == sgn) {
			nb++;
		}
		if (Math.signum(sgn3) == sgn) {
			nb++;
		}
		final Point2DInt[] result = new Point2DInt[nb];
		int i = 0;
		if (Math.signum(sgn0) == sgn) {
			result[i++] = corners[0];
		}
		if (Math.signum(sgn1) == sgn) {
			result[i++] = corners[1];
		}
		if (Math.signum(sgn2) == sgn) {
			result[i++] = corners[2];
		}
		if (Math.signum(sgn3) == sgn) {
			result[i++] = corners[3];
		}
		assert nb == i;
		return result;
	}

	public boolean doesIntersect(LineSegmentInt seg) {
		return intersect(seg).length > 0;
	}

	public Point2DInt[] intersect(LineSegmentInt seg) {
		if (seg.side(this) != 0) {
			return new Point2DInt[0];
		}
		// Log.println("THIS=" + this);
		// Log.println("LineSegment=" + seg);
		final Point2DInt corners[] = getCorners();
		final LineSegmentInt seg1 = new LineSegmentInt(corners[0], corners[1]);
		final LineSegmentInt seg2 = new LineSegmentInt(corners[1], corners[2]);
		final LineSegmentInt seg3 = new LineSegmentInt(corners[2], corners[3]);
		final LineSegmentInt seg4 = new LineSegmentInt(corners[3], corners[0]);
		final Point2DInt i1 = seg.getSegIntersection(seg1);
		Point2DInt i2 = seg.getSegIntersection(seg2);
		Point2DInt i3 = seg.getSegIntersection(seg3);
		Point2DInt i4 = seg.getSegIntersection(seg4);

		// Log.println("i1="+i1);
		// Log.println("i2="+i2);
		// Log.println("i3="+i3);
		// Log.println("i4="+i4);

		if (i2 != null && i2.equals(i1)) {
			i2 = null;
		}
		if (i3 != null && (i3.equals(i1) || i3.equals(i2))) {
			i3 = null;
		}
		if (i4 != null && (i4.equals(i1) || i4.equals(i2) || i4.equals(i3))) {
			i4 = null;
		}

		final int nb = countNotNull(i1, i2, i3, i4);
		assert nb >= 0 && nb <= 3 : nb;
		int i = 0;
		final Point2DInt result[] = new Point2DInt[nb];
		if (i1 != null) {
			result[i++] = i1;
		}
		if (i2 != null) {
			result[i++] = i2;
		}
		if (i3 != null) {
			result[i++] = i3;
		}
		if (i4 != null) {
			result[i++] = i4;
		}
		assert i == nb;
		assert getCornersOfOneSide(seg, 0).length + getCornersOfOneSide(seg, 1).length
				+ getCornersOfOneSide(seg, -1).length == 4;
		return result;
	}

	private int countNotNull(Point2DInt i1, Point2DInt i2, Point2DInt i3, Point2DInt i4) {
		int n = 0;
		if (i1 != null) {
			n++;
		}
		if (i2 != null) {
			n++;
		}
		if (i3 != null) {
			n++;
		}
		if (i4 != null) {
			n++;
		}
		return n;
	}

	public Box outerBox(int margin) {
		return new Box(x - margin, y - margin, width + 2 * margin, height + 2 * margin);
	}

	public Point2DInt getCenterPoint() {
		return new Point2DInt(x + width / 2, y + height / 2);
	}

	public void moveX(int delta) {
		this.x += delta;
	}

	public boolean intersectBox(Box other) {
		return other.x + other.width > this.x && other.y + other.height > this.y && other.x < this.x + this.width
				&& other.y < this.y + this.height;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public int getMinX() {
		return x;
	}

	public int getMinY() {
		return y;
	}

	public int getMaxX() {
		return x + width;
	}

	public int getMaxY() {
		return y + height;
	}

	public int getCenterX() {
		return x + width / 2;
	}

	public int getCenterY() {
		return y + height / 2;
	}

	public Point2DInt getPosition() {
		return getCenterPoint();
	}

}
