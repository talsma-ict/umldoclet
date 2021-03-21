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
package net.sourceforge.plantuml.nwdiag;

public class Footprint {

	private final int min;
	private final int max;

	public Footprint(int min, int max) {
		if (max < min) {
			throw new IllegalArgumentException();
		}
		assert max >= min;
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString() {
		return "" + min + " -> " + max;
	}

	public Footprint intersection(Footprint other) {
		if (this.max < other.min) {
			return null;
		}
		if (this.min > other.max) {
			return null;
		}
		return new Footprint(Math.max(this.min, other.min), Math.min(this.max, other.max));
	}

	public final int getMin() {
		return min;
	}

	public final int getMax() {
		return max;
	}

}
