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
package net.sourceforge.plantuml.nwdiag.next;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NTetris<S extends Staged> {

	private final Map<S, Integer> all = new LinkedHashMap<>();
	private final BooleanGrid grid = new BooleanGrid();

	public void add(S element) {
		int x = 0;
		while (true) {
			if (grid.isSpaceAvailable(element, x)) {
				all.put(element, x);
				grid.useSpace(element, x);
				return;
			}
			x++;
			if (x > 100) {
				throw new IllegalStateException();
			}
		}
	}

	public final Map<S, Integer> getPositions() {
		return Collections.unmodifiableMap(all);
	}

	public int getNWidth() {
		int max = 0;
		for (Entry<S, Integer> ent : all.entrySet()) {
			max = Math.max(max, ent.getValue() + ent.getKey().getNWidth());
		}
		return max;
	}

}
