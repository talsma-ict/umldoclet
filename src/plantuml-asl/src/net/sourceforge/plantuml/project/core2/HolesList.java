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
import java.util.Iterator;
import java.util.List;

public class HolesList implements Iterable<Hole> {

	private final List<Hole> list = new ArrayList<Hole>();

	public void addHole(Hole tooth) {
		list.add(tooth);
		Collections.sort(list);
	}

	public int size() {
		return list.size();
	}

	@Override
	public String toString() {
		return list.toString();
	}

	public long getStart() {
		return list.get(0).getStart();
	}

	public long getEnd() {
		return list.get(list.size() - 1).getEnd();
	}

	public Iterator<Hole> iterator() {
		return Collections.unmodifiableList(list).iterator();
	}

	public HolesList negate() {
		final HolesList result = new HolesList();
		long i = 0;
		for (Hole hole : list) {
			result.addHole(new Hole(i, hole.getStart()));
			i = hole.getEnd();
		}
		result.addHole(new Hole(i, 1000L * Integer.MAX_VALUE));
		return result;
	}

}
