/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import java.util.HashSet;
import java.util.Set;

public class BooleanGrid {

	private final Set<Integer> burned = new HashSet<>();

	private int merge(int x, int y) {
		return x + (y << 16);
	}

	public void burn(int x, int y) {
		final boolean added = burned.add(merge(x, y));
		if (added == false)
			throw new IllegalArgumentException("Already present");

	}

	public boolean isBurned(int x, int y) {
		return burned.contains(merge(x, y));
	}

	public void burnRect(int x1, int y1, int x2, int y2) {
		check(x1, y1, x2, y2);
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				burn(x, y);
	}

	public boolean isBurnRect(int x1, int y1, int x2, int y2) {
		check(x1, y1, x2, y2);
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				if (isBurned(x, y))
					return true;
		return false;
	}

	private void check(int x1, int y1, int x2, int y2) {
		if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0) {
			throw new IllegalArgumentException();
		}
		if (x2 < x1) {
			throw new IllegalArgumentException();
		}
		if (y2 < y1) {
			throw new IllegalArgumentException();
		}
	}

	// -----------------

	public boolean isSpaceAvailable(Staged element, int x) {
		if (isBurnRect(x, element.getStart().getNumber(), x + element.getNWidth() - 1, element.getEnd().getNumber()))
			return false;

		return true;
	}

	public void useSpace(Staged element, int x) {
		burnRect(x, element.getStart().getNumber(), x + element.getNWidth() - 1, element.getEnd().getNumber());
	}

}
