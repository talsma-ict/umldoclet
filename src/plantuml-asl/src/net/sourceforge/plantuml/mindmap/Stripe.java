/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.mindmap;

public class Stripe implements Comparable<Stripe> {

	private final double x1;
	private final double x2;
	private final double value;

	@Override
	public String toString() {
		return "" + (int) x1 + "->" + (int) x2 + " (" + (int) value + ")";
	}

	public Stripe(double x1, double x2, double value) {
		if (x2 <= x1) {
			System.err.println("x1=" + x1);
			System.err.println("x2=" + x2);
			throw new IllegalArgumentException();
		}
		this.x1 = x1;
		this.x2 = x2;
		this.value = value;
	}

	public boolean contains(double x) {
		return x >= x1 && x <= x2;
	}

	public int compareTo(Stripe other) {
		return (int) Math.signum(this.x1 - other.x1);
	}

	public double getValue() {
		return value;
	}

	public final double getStart() {
		return x1;
	}

	public final double getEnd() {
		return x2;
	}

}
