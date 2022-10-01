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
import net.sourceforge.plantuml.ugraphic.UBackground;
import net.sourceforge.plantuml.ugraphic.UChange;

public abstract class HColor implements UChange {

	public UBackground bg() {
		return new UBackground() {
			public HColor getBackColor() {
				return HColor.this;
			}
		};
	}

	public Color toColor(ColorMapper mapper) {
		throw new UnsupportedOperationException();
	}
	
	final public String toRGB(ColorMapper mapper) {
		final Color color = toColor(mapper);
		return StringUtils.sharp000000(color.getRGB());
	}

	final public String toSvg(ColorMapper mapper) {
		if (this.isTransparent())
			return "#00000000";

		final Color color = toColor(mapper);
		final int alpha = color.getAlpha();
		if (alpha == 255)
			return toRGB(mapper);

		String s = "0" + Integer.toHexString(alpha).toUpperCase();
		s = s.substring(s.length() - 2);
		return toRGB(mapper) + s;
	}



	public HColor lighten(int ratio) {
		return this;
	}

	public HColor darken(int ratio) {
		return this;
	}

	public HColor reverseHsluv() {
		return this;
	}

	public HColor reverse() {
		return this;
	}

	public boolean isDark() {
		return true;
	}

	public String asString() {
		return "?" + getClass().getSimpleName();
	}

	public HColor darkSchemeTheme() {
		return this;
	}

	public HColor getAppropriateColor(HColor back) {
		return this;
	}

	public HColor withDark(HColor dark) {
		throw new UnsupportedOperationException();
	}

	public HColor opposite() {
		throw new UnsupportedOperationException();
	}

	public boolean isTransparent() {
		return false;

	}

}
