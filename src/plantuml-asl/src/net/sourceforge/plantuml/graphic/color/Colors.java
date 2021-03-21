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
package net.sourceforge.plantuml.graphic.color;

import java.util.EnumMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamColors;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.LinkStyle;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class Colors {

	private final Map<ColorType, HColor> map = new EnumMap<ColorType, HColor>(ColorType.class);
	private LinkStyle lineStyle = null;
	private Boolean shadowing = null;

	@Override
	public String toString() {
		return map.toString() + " " + lineStyle;
	}

	public static Colors empty() {
		return new Colors();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	private Colors copy() {
		final Colors result = new Colors();
		result.map.putAll(this.map);
		result.lineStyle = this.lineStyle;
		return result;
	}

	private Colors() {
	}

	public Colors(String data, HColorSet set, ColorType mainType) throws NoSuchColorException {
		data = StringUtils.goLowerCase(data);

		for (final StringTokenizer st = new StringTokenizer(data, "#;"); st.hasMoreTokens();) {
			final String s = st.nextToken();
			final int x = s.indexOf(':');
			if (x == -1) {
				if (s.contains(".") == false) {
					map.put(mainType, set.getColor(s));
				}
			} else {
				final String name = s.substring(0, x);
				final String value = s.substring(x + 1);
				if (name.equalsIgnoreCase("shadowing")) {
					this.shadowing = value.equalsIgnoreCase("true");
				} else {
					final ColorType key = ColorType.getType(name);
					final HColor color = set.getColor(value);
					map.put(key, color);
				}
			}
		}
		if (data.contains("line.dashed")) {
			lineStyle = LinkStyle.DASHED();
		} else if (data.contains("line.dotted")) {
			lineStyle = LinkStyle.DOTTED();
		} else if (data.contains("line.bold")) {
			lineStyle = LinkStyle.BOLD();
		}
	}

	public HColor getColor(ColorType key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		return map.get(key);
	}

	public HColor getColor(ColorType key1, ColorType key2) {
		final HColor result = getColor(key1);
		if (result != null) {
			return result;
		}
		return getColor(key2);
	}

	public UStroke getSpecificLineStroke() {
		if (lineStyle == null) {
			return null;
		}
		return lineStyle.getStroke3();
	}

	// public Colors addSpecificLineStroke(UStroke specificStroke) {
	// final Colors result = copy();
	// result.specificStroke = specificStroke;
	// return result;
	// }

	public Colors add(ColorType type, HColor color) {
		if (color == null) {
			return this;
		}
		final Colors result = copy();
		result.map.put(type, color);
		return result;
	}

	private Colors add(ColorType colorType, Colors other) {
		final Colors result = copy();
		result.map.putAll(other.map);
		if (other.lineStyle != null) {
			result.lineStyle = other.lineStyle;
		}
		return result;
	}

	public final LinkStyle getLineStyle() {
		return lineStyle;
	}

	public ISkinParam mute(ISkinParam skinParam) {
		return new SkinParamColors(skinParam, this);
	}

	public Colors addLegacyStroke(String s) {
		if (s == null) {
			throw new IllegalArgumentException();
		}
		final Colors result = copy();
		result.lineStyle = LinkStyle.fromString1(StringUtils.goUpperCase(s));
		return result;

	}

	public static UGraphic applyStroke(UGraphic ug, Colors colors) {
		if (colors == null) {
			return ug;
		}
		if (colors.lineStyle == null) {
			return ug;
		}
		return ug.apply(colors.lineStyle.getStroke3());
	}

	public Colors applyStereotype(Stereotype stereotype, ISkinParam skinParam, ColorParam param) throws NoSuchColorException {
		if (stereotype == null) {
			throw new IllegalArgumentException();
		}
		if (param == null) {
			throw new IllegalArgumentException();
		}
		final ColorType colorType = param.getColorType();
		if (colorType == null) {
			throw new IllegalArgumentException();
		}
		if (getColor(colorType) != null) {
			return this;
		}
		final Colors colors = skinParam.getColors(param, stereotype);
		return add(colorType, colors);
	}

	private Colors applyFontParamStereotype(Stereotype stereotype, ISkinParam skinParam, FontParam param) {
		if (stereotype == null) {
			throw new IllegalArgumentException();
		}
		if (param == null) {
			return this;
		}
		final ColorType colorType = ColorType.TEXT;
		if (getColor(colorType) != null) {
			return this;
		}
		final HColor col = skinParam.getFontHtmlColor(stereotype, param);
		return add(colorType, col);
	}

	public Colors applyStereotypeForNote(Stereotype stereotype, ISkinParam skinParam, FontParam fontParam,
			ColorParam... params) throws NoSuchColorException {
		if (stereotype == null) {
			throw new IllegalArgumentException();
		}
		if (params == null) {
			throw new IllegalArgumentException();
		}
		Colors result = this;
		for (ColorParam param : params) {
			result = result.applyStereotype(stereotype, skinParam, param);
		}
		result = result.applyFontParamStereotype(stereotype, skinParam, fontParam);
		result.shadowing = skinParam.shadowingForNote(stereotype);
		return result;
	}

	public Boolean getShadowing() {
		return shadowing;
	}

	public UStroke muteStroke(UStroke stroke) {
		if (lineStyle == null) {
			return stroke;
		}
		return lineStyle.muteStroke(stroke);
	}

}
