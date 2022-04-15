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
package net.sourceforge.plantuml.sprite;

import java.awt.Color;
import java.util.Objects;

import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;

public class ColorPalette {

	private static final String colorValue = "!#$%&*+-:;<=>?@^_~GHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public char getCharFor(Color dest) {
		return getCharFor(new HColorSimple(dest, false));
	}

	public char getCharFor(HColor dest) {
		char result = 0;
		double resultDist = Double.MAX_VALUE;
		for (int i = 0; i < colorValue.length(); i++) {
			final char c = colorValue.charAt(i);
			final double dist = ((HColorSimple) dest).distance(getHtmlColorSimpleFor(c));
			if (dist < resultDist) {
				result = c;
				resultDist = dist;
			}
		}
		assert result != 0;
		return result;
	}

	private HColorSimple getHtmlColorSimpleFor(char c) {
		final Color color = Objects.requireNonNull(getColorFor(c));
		return new HColorSimple(color, false);
	}

	public Color getColorFor(char c) {
		final int col = colorValue.indexOf(c);
		if (col == -1) {
			return null;
		}
		final int blue = (col % 4) * 85;
		final int green = ((col / 4) % 4) * 85;
		final int red = ((col / 16) % 4) * 85;
		return new Color(red, green, blue);
	}

}
