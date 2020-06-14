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
package net.sourceforge.plantuml.geom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class Singularity {

	private final TreeSet<Double> angles = new TreeSet<Double>();

	final private Point2DInt center;

	public Singularity(Point2DInt center) {
		this.center = center;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(center.toString());
		for (Double a : angles) {
			final int degree = (int) (a * 180 / Math.PI);
			sb.append(' ');
			sb.append(degree);
		}
		return sb.toString();
	}

	public void addLineSegment(LineSegmentInt seg) {
		if (seg.getP1().equals(center)) {
			angles.add(convertAngle(seg.getAngle()));
		} else if (seg.getP2().equals(center)) {
			angles.add(convertAngle(seg.getOppositeAngle()));
		} else {
			assert seg.side(center) == 0 : "side=" + seg.side(center) + " center=" + center + " seg=" + seg;
			assert LineSegmentInt.isBetween(center, seg.getP1(), seg.getP2());
			addLineSegment(new LineSegmentInt(center, seg.getP1()));
			addLineSegment(new LineSegmentInt(center, seg.getP2()));
		}
		assert betweenZeroAndTwoPi();

	}

	static double convertAngle(double a) {
		if (a < 0) {
			return a + 2 * Math.PI;
		}
		return a;
	}

	private boolean betweenZeroAndTwoPi() {
		for (Double d : angles) {
			assert d >= 0;
			assert d < 2 * Math.PI;
		}
		return true;
	}

	List<Double> getAngles() {
		return new ArrayList<Double>(angles);
	}

	public boolean crossing(Point2DInt direction1, Point2DInt direction2) {
		final boolean result = crossingInternal(direction1, direction2);
		assert result == crossingInternal(direction2, direction1);
		return result;
	}

	private boolean crossingInternal(Point2DInt direction1, Point2DInt direction2) {
		if (angles.size() < 2) {
			return false;
		}
		final double angle1 = convertAngle(new LineSegmentInt(center, direction1).getAngle());
		final double angle2 = convertAngle(new LineSegmentInt(center, direction2).getAngle());

		Double last = null;
		for (Double current : angles) {
			if (last != null) {
				assert last < current;
				if (isBetween(angle1, last, current) && isBetween(angle2, last, current)) {
					return false;
				}
			}
			last = current;
		}
		final double first = angles.first();
		if ((angle1 <= first || angle1 >= last) && (angle2 <= first || angle2 >= last)) {
			return false;
		}
		return true;
	}

	private boolean isBetween(double test, double v1, double v2) {
		assert v1 < v2;
		return test >= v1 && test <= v2;
	}

	protected final Point2DInt getCenter() {
		return center;
	}

	public void merge(Singularity other) {
		this.angles.addAll(other.angles);
	}

	public List<Neighborhood> getNeighborhoods() {
		if (angles.size() == 0) {
			return Collections.singletonList(new Neighborhood(center));
		}
		final List<Neighborhood> result = new ArrayList<Neighborhood>();
		double last = angles.last();
		for (Double currentAngle : angles) {
			result.add(new Neighborhood(center, last, currentAngle));
			last = currentAngle;
		}
		return Collections.unmodifiableList(result);
	}

}
