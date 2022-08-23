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
package net.sourceforge.plantuml.ugraphic.color;

import java.awt.Color;
import java.util.Objects;

public class HColorGradient extends HColorAbstract implements HColor {

	private final HColor color1;
	private final HColor color2;
	private final char policy;

	HColorGradient(HColor color1, HColor color2, char policy) {
		if (color1 instanceof HColorGradient)
			color1 = ((HColorGradient) color1).color1;

		if (color2 instanceof HColorGradient)
			color2 = ((HColorGradient) color2).color2;

		this.color1 = Objects.requireNonNull(color1);
		this.color2 = Objects.requireNonNull(color2);
		this.policy = policy;
	}

	public final HColor getColor1() {
		return color1;
	}

	public final HColor getColor2() {
		return color2;
	}

	public final Color getColor(ColorMapper mapper, double coeff) {
		if (coeff > 1 || coeff < 0)
			throw new IllegalArgumentException("c=" + coeff);

		final Color c1 = mapper.toColor(color1);
		final Color c2 = mapper.toColor(color2);
		final int vred = c2.getRed() - c1.getRed();
		final int vgreen = c2.getGreen() - c1.getGreen();
		final int vblue = c2.getBlue() - c1.getBlue();

		final int red = c1.getRed() + (int) (coeff * vred);
		final int green = c1.getGreen() + (int) (coeff * vgreen);
		final int blue = c1.getBlue() + (int) (coeff * vblue);

		return new Color(red, green, blue);

	}

	public final char getPolicy() {
		return policy;
	}

}
