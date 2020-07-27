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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuadrantMapper {

	private final Map<Point2DCharge, Quadrant> quadrants = new HashMap<Point2DCharge, Quadrant>();
	private final Map<Quadrant, HashSet<Point2DCharge>> setOfPoints = new HashMap<Quadrant, HashSet<Point2DCharge>>();

	public void addPoint(Point2DCharge pt) {
		if (quadrants.containsKey(pt)) {
			throw new IllegalArgumentException();
		}
		final Quadrant q = new Quadrant(pt);
		quadrants.put(pt, q);
		getSetOfPoints(q).add(pt);
		assert getSetOfPoints(q).contains(pt);
		assert getSetOfPoints(new Quadrant(pt)).contains(pt);
	}

	public Set<Point2DCharge> getAllPoints(Quadrant qt) {
		return Collections.unmodifiableSet(getSetOfPoints(qt));
	}

	public Set<Point2DCharge> getAllPoints() {
		assert quadrants.keySet().equals(mergeOfSetOfPoints());
		return Collections.unmodifiableSet(quadrants.keySet());
	}

	private Set<Point2DCharge> mergeOfSetOfPoints() {
		final Set<Point2DCharge> result = new HashSet<Point2DCharge>();
		for (Set<Point2DCharge> set : setOfPoints.values()) {
			assert Collections.disjoint(set, result);
			result.addAll(set);
		}
		return result;
	}

	public void updatePoint(Point2DCharge pt) {
		final Quadrant newQ = new Quadrant(pt);
		final Quadrant old = quadrants.get(pt);
		assert getSetOfPoints(old).contains(pt);
		if (old.equals(newQ) == false) {
			assert getSetOfPoints(newQ).contains(pt) == false;
			assert getSetOfPoints(old).contains(pt);
			final boolean remove = getSetOfPoints(old).remove(pt);
			assert remove;
			final boolean add = getSetOfPoints(newQ).add(pt);
			assert add;
			assert getSetOfPoints(newQ).contains(pt);
			assert getSetOfPoints(old).contains(pt) == false;
			quadrants.put(pt, newQ);
		}
		assert getSetOfPoints(new Quadrant(pt)).contains(pt);
	}

	private HashSet<Point2DCharge> getSetOfPoints(Quadrant q) {
		HashSet<Point2DCharge> result = setOfPoints.get(q);
		if (result == null) {
			result = new HashSet<Point2DCharge>();
			setOfPoints.put(q, result);
		}
		return result;

	}

}
