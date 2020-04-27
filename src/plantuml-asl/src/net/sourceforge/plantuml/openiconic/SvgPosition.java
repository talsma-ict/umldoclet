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

public class SvgPosition {

	final private SvgCommandNumber x;
	final private SvgCommandNumber y;

	public SvgPosition() {
		this(new SvgCommandNumber("0"), new SvgCommandNumber("0"));
	}

	public SvgPosition(SvgCommandNumber x, SvgCommandNumber y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return x.toSvg() + "," + y.toSvg();
	}

	public SvgPosition(double x, double y) {
		this.x = new SvgCommandNumber(x);
		this.y = new SvgCommandNumber(y);
	}

	public SvgCommandNumber getX() {
		return x;
	}

	public SvgCommandNumber getY() {
		return y;
	}

	public double getXDouble() {
		return x.getDouble();
	}

	public double getYDouble() {
		return y.getDouble();
	}

	public SvgPosition add(SvgPosition other) {
		return new SvgPosition(x.add(other.x), y.add(other.y));
	}

	public SvgPosition getMirror(SvgPosition tobeMirrored) {
		final double centerX = getXDouble();
		final double centerY = getYDouble();
		// x1+x2 = 2*xc
		final double x = 2 * centerX - tobeMirrored.getXDouble();
		final double y = 2 * centerY - tobeMirrored.getYDouble();
		return new SvgPosition(x, y);
	}
}
