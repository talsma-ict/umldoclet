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
import java.util.List;

public class WorkLoadVariable implements WorkLoad {

	private final List<Slice> slices = new ArrayList<Slice>();

	public void add(Slice slice) {
		if (slices.size() > 0) {
			final Slice last = slices.get(slices.size() - 1);
			if (slice.getStart() <= last.getEnd()) {
				throw new IllegalArgumentException();
			}
		}
		slices.add(slice);
	}

	public IteratorSlice slices(long timeBiggerThan) {
		for (int i = 0; i < slices.size(); i++) {
			final Slice current = slices.get(i);
			if (current.getEnd() <= timeBiggerThan) {
				continue;
			}
			assert current.getEnd() > timeBiggerThan;
			assert current.getStart() >= timeBiggerThan;
			final List<Slice> tmp = slices.subList(i, slices.size());
			assert tmp.get(0).getStart() >= timeBiggerThan;
			return new ListIteratorSlice(tmp);
		}
		throw new UnsupportedOperationException();
	}

}
