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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stairs {

	private final List<Double> ys = new ArrayList<Double>();
	private final List<Integer> values = new ArrayList<Integer>();
	private final Map<Double, Integer> cache = new HashMap<Double, Integer>();

	@Override
	public String toString() {
		final List<Double> copy = new ArrayList<Double>(ys);
		Collections.sort(copy);
		final StringBuilder sb = new StringBuilder("[");
		for (Double y : copy) {
			sb.append(y + "=" + getValue(y) + " ");
		}
		sb.append("]");
		return sb.toString();
	}

	public void addStep(double y, int value) {
		assert ys.size() == values.size();
		if (ys.size() > 0) {
			final double lastY = ys.get(ys.size() - 1);
			if (y < lastY) {
				throw new IllegalArgumentException();
			}
			if (lastY == y) {
				values.set(ys.size() - 1, value);
				cache.clear();
				return;
			}
		}
		ys.add(y);
		values.add(value);
		cache.clear();
	}

	public int getMaxValue() {
		int max = Integer.MIN_VALUE;
		for (Integer v : values) {
			if (v > max) {
				max = v;
			}
		}
		return max;
	}

	public List<Double> getYs() {
		return Collections.unmodifiableList(ys);
	}

	private double getLastY() {
		if (ys.size() == 0) {
			return 0;
		}
		return ys.get(ys.size() - 1);
	}

	public int getValue(double y) {
		Integer result = cache.get(y);
		if (result == null) {
			result = getValueSlow(y);
			cache.put(y, result);
		}
		return result;
	}

	private int getValueSlow(double y) {
		final int idx = Collections.binarySearch(ys, y);
		if (idx >= 0) {
			return values.get(idx);
		}
		final int insertPoint = -idx - 1;
		if (insertPoint == 0) {
			return 0;
		}
		return values.get(insertPoint - 1);
	}

	public int getLastValue() {
		final int size = values.size();
		if (size == 0) {
			return 0;
		}
		return values.get(size - 1);
	}

}
