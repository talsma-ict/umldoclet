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
package net.sourceforge.plantuml.project.core2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.project.time.DayOfWeek;

public class Slice {

	private final long start;
	private final long end;
	private final int workLoad;

	public Slice(long start, long end, int workLoad) {
		if (end <= start) {
			throw new IllegalArgumentException();
		}
		this.start = start;
		this.end = end;
		this.workLoad = workLoad;
	}

	@Override
	public String toString() {
		return DayOfWeek.timeToString(start) + " --> " + DayOfWeek.timeToString(end) + " <" + workLoad + ">";
	}

	public boolean containsTime(long time) {
		return time >= start && time <= end;
	}

	public final long getStart() {
		return start;
	}

	public final long getEnd() {
		return end;
	}

	public final int getWorkLoad() {
		return workLoad;
	}

	public List<Slice> intersectWith(HolesList holes) {
		final List<Slice> result = new ArrayList<Slice>();
		for (Hole hole : holes.negate()) {
			final Slice inter = intersectWith(hole);
			if (inter != null) {
				result.add(inter);
			}
		}
		return Collections.unmodifiableList(result);
	}

	private Slice intersectWith(Hole hole) {
//		if (hole.getEnd() <= start || hole.getStart() <= end) {
//			return null;
//		}
		final long newStart = Math.max(start, hole.getStart());
		final long newEnd = Math.min(end, hole.getEnd());
		if (newEnd <= newStart) {
			return null;
		}
		return new Slice(newStart, newEnd, workLoad);
	}

}
