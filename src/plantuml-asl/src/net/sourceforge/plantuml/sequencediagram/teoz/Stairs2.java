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
package net.sourceforge.plantuml.sequencediagram.teoz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.graphic.SymbolContext;

public class Stairs2 {

	private final List<StairsPosition> ys = new ArrayList<StairsPosition>();
	private final List<IntegerColored> values = new ArrayList<IntegerColored>();
	private final Map<Double, IntegerColored> cache = new HashMap<Double, IntegerColored>();

	@Override
	public String toString() {
		return ys.toString() + " " + values;
	}

	public void addStep(StairsPosition position, int value, SymbolContext color) {
		if (value < 0) {
			throw new IllegalArgumentException();
		}
		// System.err.println("Stairs2::addStep " + position + " " + value + " color=" + color);
		assert ys.size() == values.size();
		if (ys.size() > 0) {
			final double lastY = ys.get(ys.size() - 1).getValue();
			if (position.getValue() <= lastY) {
				// throw new IllegalArgumentException();
				return;
			}
			if (lastY == position.getValue()) {
				values.set(ys.size() - 1, new IntegerColored(value, color));
				cache.clear();
				return;
			}
		}
		ys.add(position);
		values.add(new IntegerColored(value, color));
		cache.clear();
	}

	public int getMaxValue() {
		int max = Integer.MIN_VALUE;
		for (IntegerColored vc : values) {
			final int v = vc.getValue();
			if (v > max) {
				max = v;
			}
		}
		return max;
	}

	public List<StairsPosition> getYs() {
		return Collections.unmodifiableList(ys);
	}

	public IntegerColored getValue(double y) {
		IntegerColored resultc = cache.get(y);
		if (resultc == null) {
			resultc = getValueSlow(new StairsPosition(y, false));
			cache.put(y, resultc);
		}
		return resultc;
	}

	private IntegerColored getValueSlow(StairsPosition y) {
		final int idx = Collections.binarySearch(ys, y);
		if (idx >= 0) {
			return values.get(idx);
		}
		final int insertPoint = -idx - 1;
		if (insertPoint == 0) {
			return new IntegerColored(0, null);
		}
		return values.get(insertPoint - 1);
	}

	public int getLastValue() {
		final int size = values.size();
		if (size == 0) {
			return 0;
		}
		return values.get(size - 1).getValue();
	}

}
