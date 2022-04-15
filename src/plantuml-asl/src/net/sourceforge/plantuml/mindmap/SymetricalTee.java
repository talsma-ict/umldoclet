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

public class SymetricalTee {

	private final double thickness1;
	private final double elongation1;
	private final double thickness2;
	private final double elongation2;

	@Override
	public String toString() {
		return "t1=" + thickness1 + " e1=" + elongation1 + " t2=" + thickness2 + " e2=" + elongation2;
	}

	public SymetricalTee(double thickness1, double elongation1, double thickness2, double elongation2) {
		this.thickness1 = thickness1;
		this.elongation1 = elongation1;
		this.thickness2 = thickness2;
		this.elongation2 = elongation2;
	}

	public double getThickness1() {
		return thickness1;
	}

	public double getElongation1() {
		return elongation1;
	}

	public double getThickness2() {
		return thickness2;
	}

	public double getElongation2() {
		return elongation2;
	}

	public double getFullElongation() {
		return elongation1 + elongation2;
	}

	public double getFullThickness() {
		return Math.max(thickness1, thickness2);
	}

}
