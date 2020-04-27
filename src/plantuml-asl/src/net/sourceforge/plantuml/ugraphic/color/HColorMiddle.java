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

public class HColorMiddle extends HColorAbstract implements HColor {

	private final HColor c1;
	private final HColor c2;

	public HColorMiddle(HColor c1, HColor c2) {
		this.c1 = c1;
		this.c2 = c2;
	}

	public Color getMappedColor(ColorMapper colorMapper) {
		final Color cc1 = colorMapper.getMappedColor(c1);
		final Color cc2 = colorMapper.getMappedColor(c2);
		final int r1 = cc1.getRed();
		final int g1 = cc1.getGreen();
		final int b1 = cc1.getBlue();
		final int r2 = cc2.getRed();
		final int g2 = cc2.getGreen();
		final int b2 = cc2.getBlue();

		final int r = (r1 + r2) / 2;
		final int g = (g1 + g2) / 2;
		final int b = (b1 + b2) / 2;
		return new Color(r, g, b);
	}

}
