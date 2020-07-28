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
package net.sourceforge.plantuml.ugraphic.comp;

public class Slot implements Comparable<Slot> {

	private final double start;
	private final double end;

	public Slot(double start, double end) {
		if (start >= end) {
			throw new IllegalArgumentException();
		}
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		return "(" + start + "," + end + ")";
	}

	public double getStart() {
		return start;
	}

	public double getEnd() {
		return end;
	}

	public double size() {
		return end - start;
	}

	public boolean contains(double v) {
		return v >= start && v <= end;
	}

	public boolean intersect(Slot other) {
		return contains(other.start) || contains(other.end) || other.contains(start) || other.contains(end);
	}

	public Slot merge(Slot other) {
		return new Slot(Math.min(start, other.start), Math.max(end, other.end));
	}

	public Slot intersect(double otherStart, double otherEnd) {
		if (otherStart >= end) {
			return null;
		}
		if (otherEnd <= start) {
			return null;
		}
		return new Slot(Math.max(start, otherStart), Math.min(end, otherEnd));
	}

	public int compareTo(Slot other) {
		if (this.start < other.start) {
			return -1;
		}
		if (this.start > other.start) {
			return 1;
		}
		return 0;
	}

}
