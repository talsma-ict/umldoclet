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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Kingdom extends AbstractFigure {

	private Set<ClosedArea> buildClosedArea(ClosedArea area) {
		if (area.isClosed()) {
			throw new IllegalArgumentException();
		}
		final Set<ClosedArea> result = new HashSet<ClosedArea>();
		for (LineSegmentInt seg : getSegmentsWithExtremity(area.getFreePoint(), area.getSegments())) {
			final ClosedArea newArea = area.append(seg);
			if (newArea != null) {
				result.add(newArea);
			}
		}
		return Collections.unmodifiableSet(result);
	}

	private void grow(Set<ClosedArea> areas) {
		for (ClosedArea area : new HashSet<ClosedArea>(areas)) {
			if (area.isClosed() == false) {
				areas.addAll(buildClosedArea(area));
			}
		}
	}

	public Set<ClosedArea> getAllClosedArea() {
		final Set<ClosedArea> result = new HashSet<ClosedArea>();
		for (LineSegmentInt seg : getSegments()) {
			result.add(new ClosedArea().append(seg));
		}
		int lastSize;
		do {
			lastSize = result.size();
			grow(result);
		} while (result.size() != lastSize);
		for (final Iterator<ClosedArea> it = result.iterator(); it.hasNext();) {
			final ClosedArea area = it.next();
			if (area.isClosed() == false) {
				it.remove();
			}
		}
		return Collections.unmodifiableSet(result);
	}

	// public Set<ClosedArea> getAllSmallClosedArea() {
	// final Set<ClosedArea> all = getAllClosedArea();
	// final Set<ClosedArea> result = new HashSet<ClosedArea>(all);
	//
	// for (final Iterator<ClosedArea> it = result.iterator(); it.hasNext();) {
	// final ClosedArea area = it.next();
	// if (containsAnotherArea(area, all)) {
	// it.remove();
	// }
	// }
	//
	// return Collections.unmodifiableSet(result);
	// }

	// static private boolean containsAnotherArea(ClosedArea area,
	// Set<ClosedArea> all) {
	// for (ClosedArea another : all) {
	// if (another == area) {
	// continue;
	// }
	// if (area.contains(another)) {
	// return true;
	// }
	// }
	// return false;
	// }

	@Override
	public boolean arePointsConnectable(Point2DInt p1, Point2DInt p2) {
		for (ClosedArea area : getAllClosedArea()) {
			if (area.arePointsConnectable(p1, p2) == false) {
				return false;
			}
		}
		return true;
	}

}
