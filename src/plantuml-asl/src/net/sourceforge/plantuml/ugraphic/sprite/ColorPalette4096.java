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
package net.sourceforge.plantuml.ugraphic.sprite;

import java.awt.Color;

import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorSimple;

public class ColorPalette4096 {

	private static final String colorValue = "!#$%&*+-:;<=>?@^_~GHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public String getStringFor(Color dest) {
		return getStringFor(new HtmlColorSimple(dest, false));
	}

	public String getStringFor(HtmlColor dest) {
		int result = 0;
		double resultDist = Double.MAX_VALUE;
		for (int i = 0; i < 4096; i++) {
			final double dist = ((HtmlColorSimple) dest).distance(getHtmlColorSimpleFor(i));
			if (dist < resultDist) {
				result = i;
				resultDist = dist;
			}
		}
		return encodeInt(result);
	}

	protected String encodeInt(int result) {
		final int v2 = result % 64;
		final int v1 = result / 64;
		assert v1 >= 0 && v1 <= 63 && v2 >= 0 && v2 <= 63;
		return "" + colorValue.charAt(v1) + colorValue.charAt(v2);
	}

	private HtmlColorSimple getHtmlColorSimpleFor(int s) {
		final Color color = getColorFor(s);
		if (color == null) {
			throw new IllegalArgumentException();
		}
		return new HtmlColorSimple(color, false);
	}

	public Color getColorFor(String s) {
		if (s.length() != 2) {
			throw new IllegalArgumentException();
		}
		final int v1 = colorValue.indexOf(s.charAt(0));
		if (v1 == -1) {
			return null;
		}
		final int v2 = colorValue.indexOf(s.charAt(1));
		if (v2 == -1) {
			return null;
		}
		final int code = v1 * 64 + v2;
		return getColorFor(code);
	}

	protected Color getColorFor(final int code) {
		final int blue = code % 16;
		final int green = (code / 16) % 16;
		final int red = (code / 256) % 16;
		return new Color(dup(red), dup(green), dup(blue));
	}

	private int dup(int v) {
		assert v >= 0 && v <= 15;
		return v * 16 + v;
	}

}
