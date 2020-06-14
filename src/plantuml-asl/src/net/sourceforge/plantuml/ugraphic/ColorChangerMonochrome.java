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
package net.sourceforge.plantuml.ugraphic;

import java.awt.Color;

public class ColorChangerMonochrome {

	public Color getChangedColor(Color color) {
		if (color == null) {
			return null;
		}
		final int grayScale = getGrayScale(color);
		return new Color(grayScale, grayScale, grayScale);
	}

	private static int getGrayScale(Color color) {
		final int red = color.getRed();
		final int green = color.getGreen();
		final int blue = color.getBlue();
		return (int) (red * .3 + green * .59 + blue * .11);
	}

	public static int getGrayScale(int rgb) {
		final int red = rgb & 0x00FF0000;
		final int green = (rgb & 0x0000FF00) >> 8;
		final int blue = (rgb & 0x000000FF) >> 16;
		return (int) (red * .3 + green * .59 + blue * .11);
	}
}
