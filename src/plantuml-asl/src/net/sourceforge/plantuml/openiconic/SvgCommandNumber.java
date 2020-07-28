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
package net.sourceforge.plantuml.openiconic;

import java.util.Locale;

public class SvgCommandNumber implements SvgCommand {

	final private String number;

	public SvgCommandNumber(String number) {
		if (number.matches("[-.0-9]+") == false) {
			throw new IllegalArgumentException();
		}
		this.number = number;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + number;
	}

	public SvgCommandNumber(double number) {
		this.number = String.format(Locale.US, "%1.4f", number);
	}

	public SvgCommandNumber add(SvgCommandNumber other) {
		return new SvgCommandNumber(getDouble() + other.getDouble());
	}

	public String toSvg() {
		return number;
	}

	public double getDouble() {
		return Double.parseDouble(number);
	}

}
