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

import net.sourceforge.plantuml.StringUtils;

public abstract class AbstractColorMapper implements ColorMapper {

	final public String toRGB(HColor hcolor) {
		if (hcolor == null)
			return null;

		final Color color = toColor(hcolor);
		return StringUtils.sharp000000(color.getRGB());
	}

	final public String toSvg(HColor hcolor) {
		if (hcolor == null)
			return "none";

		if (HColors.isTransparent(hcolor))
			return "#00000000";

		final Color color = toColor(hcolor);
		final int alpha = color.getAlpha();
		if (alpha == 255)
			return toRGB(hcolor);

		String s = "0" + Integer.toHexString(alpha).toUpperCase();
		s = s.substring(s.length() - 2);
		return toRGB(hcolor) + s;
	}

	private static String sharpAlpha(int color) {
		final int v = color & 0xFFFFFF;
		String s = "00000" + Integer.toHexString(v).toUpperCase();
		s = s.substring(s.length() - 6);
		final int alpha = (int) (((long) color) & 0x000000FF) << 24;
		final String s2 = "0" + Integer.toHexString(alpha).toUpperCase();
		return "#" + s + s2.substring(0, 2);
	}

}
