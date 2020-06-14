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
package net.sourceforge.plantuml.graphic;

import java.awt.Color;

import net.sourceforge.plantuml.ugraphic.ColorMapper;

public class HtmlColorGradient implements HtmlColor {

	private final HtmlColor color1;
	private final HtmlColor color2;
	private final char policy;

	public HtmlColorGradient(HtmlColor color1, HtmlColor color2, char policy) {
		if (color1 == null || color2 == null) {
			throw new IllegalArgumentException();
		}
		if (color1 instanceof HtmlColorGradient) {
			color1 = ((HtmlColorGradient) color1).color1;
		}
		if (color2 instanceof HtmlColorGradient) {
			color2 = ((HtmlColorGradient) color2).color2;
		}
		this.color1 = color1;
		this.color2 = color2;
		this.policy = policy;
	}

	public final HtmlColor getColor1() {
		return color1;
	}

	public final HtmlColor getColor2() {
		return color2;
	}

	public final Color getColor(ColorMapper mapper, double coeff) {
		if (coeff > 1 || coeff < 0) {
			throw new IllegalArgumentException("c=" + coeff);
		}
		final Color c1 = mapper.getMappedColor(color1);
		final Color c2 = mapper.getMappedColor(color2);
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
