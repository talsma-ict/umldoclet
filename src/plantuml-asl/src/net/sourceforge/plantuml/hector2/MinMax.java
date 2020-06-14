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
package net.sourceforge.plantuml.hector2;

import java.util.Collection;

public class MinMax {

	private final int min;
	private final int max;

	private MinMax(int min, int max) {
		if (max < min) {
			throw new IllegalArgumentException();
		}
		this.min = min;
		this.max = max;
	}

	private MinMax(int value) {
		this(value, value);
	}

	public MinMax add(int value) {
		final int newMin = Math.min(min, value);
		final int newMax = Math.max(max, value);
		if (min == newMin && max == newMax) {
			return this;
		}
		return new MinMax(newMin, newMax);
	}

	public MinMax add(MinMax other) {
		final int newMin = Math.min(min, other.min);
		final int newMax = Math.max(max, other.max);
		if (min == newMin && max == newMax) {
			return this;
		}
		return new MinMax(newMin, newMax);
	}

	public final int getMin() {
		return min;
	}

	public final int getMax() {
		return max;
	}

	public static MinMax from(Collection<Integer> values) {
		MinMax result = null;
		for (Integer i : values) {
			if (result == null) {
				result = new MinMax(i);
			} else {
				result = result.add(i);
			}
		}
		return result;
	}

	public int getDiff() {
		return max - min;
	}

}
