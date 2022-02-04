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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.ThemeStyle;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;

public class HColorSet {

	private final static HColorSet singleton = new HColorSet();

	private final Map<String, String> htmlNames = new HashMap<String, String>();
	private final Set<String> names = new TreeSet<>();

	public static HColorSet instance() {
		return singleton;
	}

	public Collection<String> names() {
		return Collections.unmodifiableSet(names);
	}

	private HColorSet() {
		// Taken from http://perl.wikipedia.com/wiki/Named_colors ?
		// http://www.w3schools.com/HTML/html_colornames.asp
		register("AliceBlue", "#F0F8FF");
		register("AntiqueWhite", "#FAEBD7");
		register("Aqua", "#00FFFF");
		register("Aquamarine", "#7FFFD4");
		register("Azure", "#F0FFFF");
		register("Beige", "#F5F5DC");
		register("Bisque", "#FFE4C4");
		register("Black", "#000000");
		register("BlanchedAlmond", "#FFEBCD");
		register("Blue", "#0000FF");
		register("BlueViolet", "#8A2BE2");
		register("Brown", "#A52A2A");
		register("BurlyWood", "#DEB887");
		register("CadetBlue", "#5F9EA0");
		register("Chartreuse", "#7FFF00");
		register("Chocolate", "#D2691E");
		register("Coral", "#FF7F50");
		register("CornflowerBlue", "#6495ED");
		register("Cornsilk", "#FFF8DC");
		register("Crimson", "#DC143C");
		register("Cyan", "#00FFFF");
		register("DarkBlue", "#00008B");
		register("DarkCyan", "#008B8B");
		register("DarkGoldenRod", "#B8860B");
		register("DarkGray", "#A9A9A9");
		register("DarkGrey", "#A9A9A9");
		register("DarkGreen", "#006400");
		register("DarkKhaki", "#BDB76B");
		register("DarkMagenta", "#8B008B");
		register("DarkOliveGreen", "#556B2F");
		register("Darkorange", "#FF8C00");
		register("DarkOrchid", "#9932CC");
		register("DarkRed", "#8B0000");
		register("DarkSalmon", "#E9967A");
		register("DarkSeaGreen", "#8FBC8F");
		register("DarkSlateBlue", "#483D8B");
		register("DarkSlateGray", "#2F4F4F");
		register("DarkSlateGrey", "#2F4F4F");
		register("DarkTurquoise", "#00CED1");
		register("DarkViolet", "#9400D3");
		register("DeepPink", "#FF1493");
		register("DeepSkyBlue", "#00BFFF");
		register("DimGray", "#696969");
		register("DimGrey", "#696969");
		register("DodgerBlue", "#1E90FF");
		register("FireBrick", "#B22222");
		register("FloralWhite", "#FFFAF0");
		register("ForestGreen", "#228B22");
		register("Fuchsia", "#FF00FF");
		register("Gainsboro", "#DCDCDC");
		register("GhostWhite", "#F8F8FF");
		register("Gold", "#FFD700");
		register("GoldenRod", "#DAA520");
		register("Gray", "#808080");
		register("Grey", "#808080");
		register("Green", "#008000");
		register("GreenYellow", "#ADFF2F");
		register("HoneyDew", "#F0FFF0");
		register("HotPink", "#FF69B4");
		register("IndianRed", "#CD5C5C");
		register("Indigo", "#4B0082");
		register("Ivory", "#FFFFF0");
		register("Khaki", "#F0E68C");
		register("Lavender", "#E6E6FA");
		register("LavenderBlush", "#FFF0F5");
		register("LawnGreen", "#7CFC00");
		register("LemonChiffon", "#FFFACD");
		register("LightBlue", "#ADD8E6");
		register("LightCoral", "#F08080");
		register("LightCyan", "#E0FFFF");
		register("LightGoldenRodYellow", "#FAFAD2");
		register("LightGray", "#D3D3D3");
		register("LightGrey", "#D3D3D3");
		register("LightGreen", "#90EE90");
		register("LightPink", "#FFB6C1");
		register("LightSalmon", "#FFA07A");
		register("LightSeaGreen", "#20B2AA");
		register("LightSkyBlue", "#87CEFA");
		register("LightSlateGray", "#778899");
		register("LightSlateGrey", "#778899");
		register("LightSteelBlue", "#B0C4DE");
		register("LightYellow", "#FFFFE0");
		register("Lime", "#00FF00");
		register("LimeGreen", "#32CD32");
		register("Linen", "#FAF0E6");
		register("Magenta", "#FF00FF");
		register("Maroon", "#800000");
		register("MediumAquaMarine", "#66CDAA");
		register("MediumBlue", "#0000CD");
		register("MediumOrchid", "#BA55D3");
		register("MediumPurple", "#9370D8");
		register("MediumSeaGreen", "#3CB371");
		register("MediumSlateBlue", "#7B68EE");
		register("MediumSpringGreen", "#00FA9A");
		register("MediumTurquoise", "#48D1CC");
		register("MediumVioletRed", "#C71585");
		register("MidnightBlue", "#191970");
		register("MintCream", "#F5FFFA");
		register("MistyRose", "#FFE4E1");
		register("Moccasin", "#FFE4B5");
		register("NavajoWhite", "#FFDEAD");
		register("Navy", "#000080");
		register("OldLace", "#FDF5E6");
		register("Olive", "#808000");
		register("OliveDrab", "#6B8E23");
		register("Orange", "#FFA500");
		register("OrangeRed", "#FF4500");
		register("Orchid", "#DA70D6");
		register("PaleGoldenRod", "#EEE8AA");
		register("PaleGreen", "#98FB98");
		register("PaleTurquoise", "#AFEEEE");
		register("PaleVioletRed", "#D87093");
		register("PapayaWhip", "#FFEFD5");
		register("PeachPuff", "#FFDAB9");
		register("Peru", "#CD853F");
		register("Pink", "#FFC0CB");
		register("Plum", "#DDA0DD");
		register("PowderBlue", "#B0E0E6");
		register("Purple", "#800080");
		register("Red", "#FF0000");
		register("RosyBrown", "#BC8F8F");
		register("RoyalBlue", "#4169E1");
		register("SaddleBrown", "#8B4513");
		register("Salmon", "#FA8072");
		register("SandyBrown", "#F4A460");
		register("SeaGreen", "#2E8B57");
		register("SeaShell", "#FFF5EE");
		register("Sienna", "#A0522D");
		register("Silver", "#C0C0C0");
		register("SkyBlue", "#87CEEB");
		register("SlateBlue", "#6A5ACD");
		register("SlateGray", "#708090");
		register("SlateGrey", "#708090");
		register("Snow", "#FFFAFA");
		register("SpringGreen", "#00FF7F");
		register("SteelBlue", "#4682B4");
		register("Tan", "#D2B48C");
		register("Teal", "#008080");
		register("Thistle", "#D8BFD8");
		register("Tomato", "#FF6347");
		register("Turquoise", "#40E0D0");
		register("Violet", "#EE82EE");
		register("Wheat", "#F5DEB3");
		register("White", "#FFFFFF");
		register("WhiteSmoke", "#F5F5F5");
		register("Yellow", "#FFFF00");
		register("YellowGreen", "#9ACD32");
		// Archimate
		register("BUSINESS", "#FFFFCC");
		register("APPLICATION", "#C2F0FF");
		register("MOTIVATION", "#EFEFFF");
		register("STRATEGY", "#F8E7C0");
		register("TECHNOLOGY", "#C9FFC9");
		register("PHYSICAL", "#97FF97");
		register("IMPLEMENTATION", "#FFE0E0");
	}

	private void register(String s, String color) {
		htmlNames.put(StringUtils.goLowerCase(s), color);
		names.add(s);
	}

	class Gradient {
		private final String s1;
		private final char sep;
		private final String s2;

		Gradient(String s1, char sep, String s2) {
			this.s1 = s1;
			this.sep = sep;
			this.s2 = s2;
		}

		boolean isValid() {
			return isColorValid(s1) && isColorValid(s2);
		}

		HColorGradient buildInternal(HColor background) {
			return new HColorGradient(build(s1, background), build(s2, background), sep);
		}

	}

	class Automatic {
		private final String[] colors;

		public Automatic(String[] colors) {
			this.colors = colors;
		}

		boolean isValid() {
			for (String color : colors)
				if (isColorValid(color) == false)
					return false;

			return true;
		}

		HColorAutomatic buildInternal(ThemeStyle themeStyle, HColor background) {
			if (colors.length == 2)
				return new HColorAutomatic(themeStyle, build(colors[0], background), build(colors[1], background),
						null);

			return new HColorAutomatic(themeStyle, build(colors[0], background), build(colors[1], background),
					build(colors[2], background));
		}

	}

	private Gradient gradientFromString(String s) {
		final Matcher2 m = MyPattern.cmpile("[-\\\\|/]").matcher(s);
		if (m.find()) {
			final char sep = m.group(0).charAt(0);
			final int idx = s.indexOf(sep);
			final String s1 = s.substring(0, idx);
			final String s2 = s.substring(idx + 1);
			return new Gradient(s1, sep, s2);
		}
		return null;
	}

	private Automatic automaticFromString(String s) {
		if (s.startsWith("#"))
			s = s.substring(1);

		if (s.startsWith("?") == false)
			return null;

		final int idx = s.indexOf(':');
		if (idx != -1)
			return new Automatic(s.substring(1).split(":"));

		return null;
	}

	public HColor getColorOrWhite(String s) {
		return getColorOrWhite(ThemeStyle.LIGHT, s, null);
	}

	public HColor getColorOrWhite(ThemeStyle themeStyle, String s) {
		return getColorOrWhite(themeStyle, s, null);
	}

	public HColor getColorOrWhite(ThemeStyle themeStyle, String s, HColor background) {
		if (isColorValid(Objects.requireNonNull(s)) == false)
			return HColorUtils.WHITE;

		try {
			return getColor(themeStyle, s, background);
		} catch (NoSuchColorException e) {
			assert false;
			return HColorUtils.WHITE;
		}
	}

	public HColor getColor(ThemeStyle themeStyle, String s) throws NoSuchColorException {
		return getColor(themeStyle, s, null);
	}

	public HColor getColorLEGACY(String s) throws NoSuchColorException {
		return getColor(ThemeStyle.LIGHT, s, null);
	}

	public HColor getColor(ThemeStyle themeStyle, String s, HColor background) throws NoSuchColorException {
		if (isColorValid(Objects.requireNonNull(s)) == false)
			throw new NoSuchColorException();

		final Automatic automatic = automaticFromString(s);
		if (automatic != null)
			return automatic.buildInternal(themeStyle, background);

		final Gradient gradient = gradientFromString(s);
		if (gradient != null)
			return gradient.buildInternal(background);

		if (background == null && (s.equalsIgnoreCase("#transparent") || s.equalsIgnoreCase("transparent")))
			s = "#00000000";

		return build(s, background);
	}

	private boolean isColorValid(String s) {
		s = removeFirstDieseAndgoLowerCase(s);
		final Automatic automatic = automaticFromString(s);
		if (automatic != null)
			return automatic.isValid();

		final Gradient gradient = gradientFromString(s);
		if (gradient != null)
			return gradient.isValid();

		if (s.matches("[0-9A-Fa-f]|[0-9A-Fa-f]{3}|[0-9A-Fa-f]{6}|[0-9A-Fa-f]{8}|automatic|transparent"))
			return true;

		if (htmlNames.containsKey(s))
			return true;

		return false;

	}

	private HColor build(String s, HColor background) {
		s = removeFirstDieseAndgoLowerCase(s);
		final Color color;
		if (s.equalsIgnoreCase("transparent") || s.equalsIgnoreCase("background")) {
			return new HColorBackground(background);
		} else if (s.equalsIgnoreCase("automatic")) {
			return new HColorAutomaticLegacy();
		} else if (s.matches("[0-9A-Fa-f]")) {
			s = "" + s.charAt(0) + s.charAt(0) + s.charAt(0) + s.charAt(0) + s.charAt(0) + s.charAt(0);
			color = new Color(Integer.parseInt(s, 16));
		} else if (s.matches("[0-9A-Fa-f]{3}")) {
			s = "" + s.charAt(0) + s.charAt(0) + s.charAt(1) + s.charAt(1) + s.charAt(2) + s.charAt(2);
			color = new Color(Integer.parseInt(s, 16));
		} else if (s.matches("[0-9A-Fa-f]{6}")) {
			color = new Color(Integer.parseInt(s, 16));
		} else if (s.matches("[0-9A-Fa-f]{8}")) {
			color = fromRGBa(s);
		} else {
			final String value = Objects.requireNonNull(htmlNames.get(s));
			color = new Color(Integer.parseInt(value.substring(1), 16));
		}
		return new HColorSimple(color, false);
	}

	private Color fromRGBa(String s) {
		// https://forum.plantuml.net/11606/full-opacity-alpha-compositing-support-for-svg-and-png
		if (s.length() != 8)
			throw new IllegalArgumentException();

		final int red = Integer.parseInt(s.substring(0, 2), 16);
		final int green = Integer.parseInt(s.substring(2, 4), 16);
		final int blue = Integer.parseInt(s.substring(4, 6), 16);
		final int alpha = Integer.parseInt(s.substring(6, 8), 16);
		return new Color(red, green, blue, alpha);
	}

	private String removeFirstDieseAndgoLowerCase(String s) {
		s = StringUtils.goLowerCase(s);
		if (s.startsWith("#"))
			s = s.substring(1);

		return s;
	}

}
