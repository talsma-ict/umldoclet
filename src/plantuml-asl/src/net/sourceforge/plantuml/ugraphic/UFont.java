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
package net.sourceforge.plantuml.ugraphic;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.plantuml.StringUtils;

public class UFont {

	private final Font font;
	private final String family;

	private static final Set<String> names = new HashSet<>();

	static {
		for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
			names.add(name.toLowerCase());
		}
	}

	public String toStringDebug() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getPortableFontName());
		sb.append("/");
		sb.append(font.getSize());
		return sb.toString();
	}

	public UFont(String fontFamily, int fontStyle, int fontSize) {
		this(buildFont(fontFamily, fontStyle, fontSize), fontFamily);
	}

	private static Font buildFont(String fontFamily, int fontStyle, int fontSize) {
		if (fontFamily.contains(",")) {
			for (String name : fontFamily.split(",")) {
				name = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(name).trim();
				if (doesFamilyExists(name)) {
					return new Font(fontFamily, fontStyle, fontSize);
				}
			}
		}
		return new Font(fontFamily, fontStyle, fontSize);
	}

	private static boolean doesFamilyExists(String name) {
		return names.contains(name.toLowerCase());
	}

	public static UFont serif(int size) {
		return new UFont("Serif", Font.PLAIN, size);
	}

	public static UFont sansSerif(int size) {
		return new UFont("SansSerif", Font.PLAIN, size);
	}

	public static UFont courier(int size) {
		return new UFont("Courier", Font.PLAIN, size);
	}

	public static UFont byDefault(int size) {
		return sansSerif(12);
	}

	public UFont goTikz(int delta) {
		return new UFont(new Font("Serif", getStyle(), getSize() + delta), "Serif");
	}

	public static UFont monospaced(int size) {
		return new UFont("Monospaced", Font.PLAIN, size);
	}

	private UFont(Font font, String family) {
		this.font = font;
		this.family = family;
	}

	public final Font getUnderlayingFont() {
		return font;
	}

	public UFont withSize(float size) {
		return new UFont(font.deriveFont(size), family);
	}

	public UFont withStyle(int style) {
		return new UFont(font.deriveFont(style), family);
	}

	public UFont bold() {
		return withStyle(Font.BOLD);
	}

	public UFont italic() {
		return withStyle(Font.ITALIC);
	}

	public int getStyle() {
		return font.getStyle();
	}

	public int getSize() {
		return font.getSize();
	}

	public double getSize2D() {
		return font.getSize2D();
	}

	public boolean isBold() {
		return font.isBold();
	}

	public boolean isItalic() {
		return font.isItalic();
	}

	public String getFamily(UFontContext context) {
		if (context == UFontContext.EPS) {
			if (family == null) {
				return "Times-Roman";
			}
			return font.getPSName();
		}
		if (context == UFontContext.SVG) {
			if (family.equalsIgnoreCase("sansserif")) {
				return "sans-serif";
			}
			return family;
		}
		return family;
	}

	// Kludge for testing because font names on some machines (only macOS?) do not end with <DOT><STYLE>
	// See https://github.com/plantuml/plantuml/issues/720
	private String getPortableFontName() {
		final String name = font.getFontName();
		if (font.isBold() && font.isItalic())
			return name.endsWith(".bolditalic") ? name : name + ".bolditalic";
		else if (font.isBold())
			return name.endsWith(".bold") ? name : name + ".bold";
		else if (font.isItalic())
			return name.endsWith(".italic") ? name : name + ".italic";
		else
			return name.endsWith(".plain") ? name : name + ".plain";
	}

	@Override
	public String toString() {
		return font.toString()/* + " " + font.getPSName() */;
	}

	@Override
	public int hashCode() {
		return font.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UFont == false) {
			return false;
		}
		return this.font.equals(((UFont) obj).font);
	}

}
