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
package net.sourceforge.plantuml.graphic;

import java.util.EnumSet;
import java.util.Map;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class FontConfiguration {

	private final EnumSet<FontStyle> styles;
	private final UFont currentFont;
	private final UFont motherFont;
	private final HColor motherColor;
	private final HColor hyperlinkColor;
	private final HColor currentColor;
	private final HColor extendedColor;
	private final FontPosition fontPosition;
	private final SvgAttributes svgAttributes;
	private final boolean hyperlink;
	private final boolean useUnderlineForHyperlink;
	private final int tabSize;

	public FontConfiguration(UFont font, HColor color, HColor hyperlinkColor, boolean useUnderlineForHyperlink) {
		this(font, color, hyperlinkColor, useUnderlineForHyperlink, 8);
	}

	public FontConfiguration(UFont font, HColor color, HColor hyperlinkColor, boolean useUnderlineForHyperlink,
			int tabSize) {
		this(getStyles(font), font, color, font, color, null, FontPosition.NORMAL, new SvgAttributes(), false,
				hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public static FontConfiguration blackBlueTrue(UFont font) {
		return new FontConfiguration(font, HColorUtils.BLACK, HColorUtils.BLUE, true, 8);
	}

	public FontConfiguration(ISkinParam skinParam, FontParam fontParam, Stereotype stereo) {
		this(SkinParamUtils.getFont(skinParam, fontParam, stereo),
				SkinParamUtils.getFontColor(skinParam, fontParam, stereo), skinParam.getHyperlinkColor(),
				skinParam.useUnderlineForHyperlink(), skinParam.getTabSize());
	}

	public FontConfiguration(Style style, ISkinParam skinParam, Stereotype stereo, FontParam fontParam) {
		this(style.getUFont(), style.value(PName.FontColor).asColor(skinParam.getIHtmlColorSet()),
				skinParam.getHyperlinkColor(), skinParam.useUnderlineForHyperlink(), skinParam.getTabSize());
	}

	// ---

	public final boolean useUnderlineForHyperlink() {
		return useUnderlineForHyperlink;
	}

	public final HColor getHyperlinkColor() {
		return hyperlinkColor;
	}

	// ---

	private static EnumSet<FontStyle> getStyles(UFont font) {
		final boolean bold = font.isBold();
		final boolean italic = font.isItalic();
		if (bold && italic) {
			return EnumSet.of(FontStyle.ITALIC, FontStyle.BOLD);
		}
		if (bold) {
			return EnumSet.of(FontStyle.BOLD);
		}
		if (italic) {
			return EnumSet.of(FontStyle.ITALIC);
		}
		return EnumSet.noneOf(FontStyle.class);
	}

	@Override
	public String toString() {
		return styles.toString() + " " + currentColor;
	}

	private FontConfiguration(EnumSet<FontStyle> styles, UFont motherFont, HColor motherColor, UFont currentFont,
			HColor currentColor, HColor extendedColor, FontPosition fontPosition, SvgAttributes svgAttributes,
			boolean hyperlink, HColor hyperlinkColor, boolean useUnderlineForHyperlink, int tabSize) {
		this.styles = styles;
		this.currentFont = currentFont;
		this.motherFont = motherFont;
		this.currentColor = currentColor;
		this.motherColor = motherColor;
		this.extendedColor = extendedColor;
		this.fontPosition = fontPosition;
		this.svgAttributes = svgAttributes;
		this.hyperlink = hyperlink;
		this.hyperlinkColor = hyperlinkColor;
		this.useUnderlineForHyperlink = useUnderlineForHyperlink;
		this.tabSize = tabSize;
	}

	public FontConfiguration forceFont(UFont newFont, HColor htmlColorForStereotype) {
		if (newFont == null) {
			return add(FontStyle.ITALIC);
		}
		FontConfiguration result = new FontConfiguration(styles, newFont, motherColor, newFont, currentColor,
				extendedColor, fontPosition, svgAttributes, hyperlink, hyperlinkColor, useUnderlineForHyperlink,
				tabSize);
		if (htmlColorForStereotype != null) {
			result = result.changeColor(htmlColorForStereotype);
		}
		return result;
	}

	public FontConfiguration changeAttributes(SvgAttributes toBeAdded) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, currentColor, extendedColor,
				fontPosition, svgAttributes.add(toBeAdded), hyperlink, hyperlinkColor, useUnderlineForHyperlink,
				tabSize);
	}

	private FontConfiguration withHyperlink() {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, currentColor, extendedColor,
				fontPosition, svgAttributes, true, hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public FontConfiguration changeColor(HColor htmlColor) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, htmlColor, extendedColor,
				fontPosition, svgAttributes, hyperlink, hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public FontConfiguration mute(Colors colors) {
		if (colors == null) {
			throw new IllegalArgumentException();
		}
		final HColor color = colors.getColor(ColorType.TEXT);
		if (color == null) {
			return this;
		}
		return changeColor(color);
	}

	FontConfiguration changeExtendedColor(HColor newExtendedColor) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, currentColor, newExtendedColor,
				fontPosition, svgAttributes, hyperlink, hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public FontConfiguration changeSize(float size) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont.withSize(size), currentColor,
				extendedColor, fontPosition, svgAttributes, hyperlink, hyperlinkColor, useUnderlineForHyperlink,
				tabSize);
	}

	public FontConfiguration bigger(double delta) {
		return changeSize((float) (currentFont.getSize() + delta));
	}

	public FontConfiguration changeFontPosition(FontPosition fontPosition) {
		return new FontConfiguration(styles, motherFont, motherColor, currentFont, currentColor, extendedColor,
				fontPosition, svgAttributes, hyperlink, hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public FontConfiguration changeFamily(String family) {
		return new FontConfiguration(styles, motherFont, motherColor,
				new UFont(family, currentFont.getStyle(), currentFont.getSize()), currentColor, extendedColor,
				fontPosition, svgAttributes, hyperlink, hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public FontConfiguration resetFont() {
		return new FontConfiguration(styles, motherFont, motherColor, motherFont, motherColor, null,
				FontPosition.NORMAL, new SvgAttributes(), hyperlink, hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public FontConfiguration add(FontStyle style) {
		final EnumSet<FontStyle> r = styles.clone();
		if (style == FontStyle.PLAIN) {
			r.clear();
		}
		r.add(style);
		return new FontConfiguration(r, motherFont, motherColor, currentFont, currentColor, extendedColor, fontPosition,
				svgAttributes, hyperlink, hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public FontConfiguration italic() {
		return add(FontStyle.ITALIC);
	}

	public FontConfiguration bold() {
		return add(FontStyle.BOLD);
	}

	public FontConfiguration unbold() {
		return remove(FontStyle.BOLD);
	}

	public FontConfiguration unitalic() {
		return remove(FontStyle.ITALIC);
	}

	public FontConfiguration underline() {
		return add(FontStyle.UNDERLINE);
	}

	public FontConfiguration wave(HColor color) {
		return add(FontStyle.WAVE).changeExtendedColor(color);
	}

	public FontConfiguration hyperlink() {
		if (useUnderlineForHyperlink) {
			return add(FontStyle.UNDERLINE).withHyperlink();
		}
		return withHyperlink();
	}

	public FontConfiguration remove(FontStyle style) {
		final EnumSet<FontStyle> r = styles.clone();
		r.remove(style);
		return new FontConfiguration(r, motherFont, motherColor, currentFont, currentColor, extendedColor, fontPosition,
				svgAttributes, hyperlink, hyperlinkColor, useUnderlineForHyperlink, tabSize);
	}

	public UFont getFont() {
		UFont result = currentFont;
		for (FontStyle style : styles) {
			result = style.mutateFont(result);
		}
		return fontPosition.mute(result);
	}

	public HColor getColor() {
		if (hyperlink) {
			return hyperlinkColor;
		}
		return currentColor;
	}

	public HColor getExtendedColor() {
		return extendedColor;
	}

	public boolean containsStyle(FontStyle style) {
		return styles.contains(style);
	}

	public int getSpace() {
		return fontPosition.getSpace();
	}

	public Map<String, String> getAttributes() {
		return svgAttributes.attributes();
	}

	public double getSize2D() {
		return currentFont.getSize2D();
	}

	public int getTabSize() {
		return tabSize;
	}

}
