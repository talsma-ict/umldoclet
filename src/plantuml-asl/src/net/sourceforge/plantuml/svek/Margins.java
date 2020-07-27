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
package net.sourceforge.plantuml.svek;

public class Margins {

	private final double x1;
	private final double x2;
	private final double y1;
	private final double y2;

	static public Margins NONE = new Margins(0, 0, 0, 0);

	public static Margins uniform(double value) {
		return new Margins(value, value, value, value);
	}

	@Override
	public String toString() {
		return "MARGIN[" + x1 + "," + x2 + "," + y1 + "," + y2 + "]";
	}

	public Margins(double x1, double x2, double y1, double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public boolean isZero() {
		return x1 == 0 && x2 == 0 && y1 == 0 && y2 == 0;
	}

	public final double getX1() {
		return x1;
	}

	public final double getX2() {
		return x2;
	}

	public final double getY1() {
		return y1;
	}

	public final double getY2() {
		return y2;
	}

	public double getTotalWidth() {
		return x1 + x2;
	}

	public double getTotalHeight() {
		return y1 + y2;
	}

}
