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

public class StairsPosition implements Comparable<StairsPosition> {

	private final double value;
	private final boolean destroy;

	public StairsPosition(double value, boolean destroy) {
		this.value = value;
		this.destroy = destroy;
	}

	@Override
	public String toString() {
		return "" + value + "-(" + destroy + ")";
	}

	@Override
	public int hashCode() {
		return new Double(value).hashCode() + (destroy ? 17 : 37);
	}

	@Override
	public boolean equals(Object obj) {
		final StairsPosition other = (StairsPosition) obj;
		return this.value == other.value && this.destroy == other.destroy;
	}

	public double getValue() {
		return value;
	}

	public int compareTo(StairsPosition other) {
		if (this.value > other.value) {
			return 1;
		}
		if (this.value < other.value) {
			return -1;
		}
		return 0;
	}

	public boolean isDestroy() {
		return destroy;
	}

}
