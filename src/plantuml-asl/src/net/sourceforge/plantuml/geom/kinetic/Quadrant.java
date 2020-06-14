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
package net.sourceforge.plantuml.geom.kinetic;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class Quadrant {

	static final private int SIZE = 100;

	private final int x;
	private final int y;

	public Quadrant(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Quadrant(Point2DCharge pt) {
		this((int) pt.getX() / SIZE, (int) pt.getY() / SIZE);
	}

	@Override
	public boolean equals(Object obj) {
		final Quadrant other = (Quadrant) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public int hashCode() {
		return x * 3571 + y;
	}

	@Override
	public String toString() {
		return "" + x + "-" + y;
	}

	public Collection<Quadrant> neighbourhood() {
		final Collection<Quadrant> result = Arrays.asList(new Quadrant(x - 1, y - 1), new Quadrant(x, y - 1),
				new Quadrant(x + 1, y - 1), new Quadrant(x - 1, y), this, new Quadrant(x + 1, y), new Quadrant(x - 1,
						y + 1), new Quadrant(x, y + 1), new Quadrant(x + 1, y + 1));
		assert new HashSet<Quadrant>(result).size() == 9;
		return result;
	}

}
