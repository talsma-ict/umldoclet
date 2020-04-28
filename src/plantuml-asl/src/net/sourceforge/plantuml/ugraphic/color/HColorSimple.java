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
package net.sourceforge.plantuml.ugraphic.color;

import java.awt.Color;

public class HColorSimple extends HColorAbstract implements HColor {

	private final Color color;
	private final boolean monochrome;

	@Override
	public int hashCode() {
		return color.hashCode();
	}

	@Override
	public String toString() {
		if (color.getAlpha() == 0) {
			return "transparent";
		}
		return color.toString() + " alpha=" + color.getAlpha() + " monochrome=" + monochrome;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof HColorSimple == false) {
			return false;
		}
		return this.color.equals(((HColorSimple) other).color);
	}

	public HColorSimple(Color c, boolean monochrome) {
		this.color = c;
		this.monochrome = monochrome;
	}

	public Color getColor999() {
		return color;
	}

	public HColorSimple asMonochrome() {
		if (monochrome) {
			throw new IllegalStateException();
		}
		return new HColorSimple(new ColorChangerMonochrome().getChangedColor(color), true);
	}

	public HColorSimple opposite() {
		final Color mono = new ColorChangerMonochrome().getChangedColor(color);
		final int grayScale = 255 - mono.getGreen() > 127 ? 255 : 0;
		return new HColorSimple(new Color(grayScale, grayScale, grayScale), true);
	}

	public double distance(HColorSimple other) {
		final int diffRed = Math.abs(this.color.getRed() - other.color.getRed());
		final int diffGreen = Math.abs(this.color.getGreen() - other.color.getGreen());
		final int diffBlue = Math.abs(this.color.getBlue() - other.color.getBlue());
		return diffRed * .3 + diffGreen * .59 + diffBlue * .11;
	}

}
