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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SmallestEnclosingCircle {

	private final List<Point2D> all = new ArrayList<Point2D>();
	private Circle lastSolution;

	public void append(Point2D pt) {
		if (all.contains(pt) == false) {
			all.add(pt);
		}
		this.lastSolution = null;
	}

	public Circle getCircle() {
		if (lastSolution == null) {
			lastSolution = findSec(all.size(), all, 0, new ArrayList<Point2D>(all));
		}
		return lastSolution;
	}

	private Circle findSec(int n, List<Point2D> p, int m, List<Point2D> b) {
		Circle sec = new Circle();

		// Compute the Smallest Enclosing Circle defined by B
		if (m == 1) {
			sec = new Circle(b.get(0));
		} else if (m == 2) {
			sec = new Circle(b.get(0), b.get(1));
		} else if (m == 3) {
			return Circle.getCircle(b.get(0), b.get(1), b.get(2));
		}

		// Check if all the points in p are enclosed
		for (int i = 0; i < n; i++) {
			if (sec.isOutside(p.get(i))) {
				// Compute B <--- B union P[i].
				b.set(m, p.get(i));
				// Recurse
				sec = findSec(i, p, m + 1, b);
			}
		}

		return sec;
	}

}
