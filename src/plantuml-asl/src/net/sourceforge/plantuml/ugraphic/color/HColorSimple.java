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

public class HColorSimple extends HColorAbstract implements HColor {

	private final Color color;
	private final boolean monochrome;
	private HColor dark;

	@Override
	public int hashCode() {
		return color.hashCode();
	}

	@Override
	public String toString() {
		if (isTransparent())
			return "transparent";

		final boolean withDark = this != dark;

		final StringBuilder sb = new StringBuilder();
		if (withDark)
			sb.append("WITHDARK ");
		sb.append(color.toString());
		sb.append(" \u03B1=");
		sb.append(color.getAlpha());
		if (monochrome)
			sb.append("MONOCHROME");
		return sb.toString();
	}

	@Override
	public String asString() {
		if (isTransparent())
			return "transparent";

		if (color.getAlpha() == 255)
			return StringUtils.sharp000000(color.getRGB());

		return "#" + Integer.toHexString(color.getRGB());
	}

	@Override
	public HColor lighten(int ratio) {
		final float[] hsl = new HSLColor(color).getHSL();
		hsl[2] += hsl[2] * (ratio / 100.0);
		return new HColorSimple(new HSLColor(hsl).getRGB(), false);
	}

	@Override
	public HColor darken(int ratio) {
		final float[] hsl = new HSLColor(color).getHSL();
		hsl[2] -= hsl[2] * (ratio / 100.0);
		return new HColorSimple(new HSLColor(hsl).getRGB(), false);
	}

	@Override
	public HColor reverseHsluv() {
		return new HColorSimple(ColorUtils.reverseHsluv(color), false);
	}

	@Override
	public HColor reverse() {
		return new HColorSimple(ColorOrder.RGB.getReverse(color), false);
	}

	@Override
	public boolean isDark() {
		return ColorUtils.getGrayScale(color) < 128;
	}

	public boolean isTransparent() {
		return color.getAlpha() == 0;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof HColorSimple == false)
			return false;

		return this.color.equals(((HColorSimple) other).color);
	}

	public HColorSimple(Color c, boolean monochrome) {
		this.color = c;
		this.monochrome = monochrome;
		this.dark = this;
	}

	private HColorSimple(Color c, boolean monochrome, HColor dark) {
		this.color = c;
		this.monochrome = monochrome;
		this.dark = dark;
	}

	public Color getColor999() {
		return color;
	}

	public HColorSimple asMonochrome() {
		return new HColorSimple(new ColorChangerMonochrome().getChangedColor(color), monochrome);
	}

	public HColor asMonochrome(HColorSimple colorForMonochrome, double minGray, double maxGray) {
		final Color tmp = new ColorChangerMonochrome().getChangedColor(color);
		final int gray = tmp.getGreen();
		assert gray == tmp.getBlue();
		assert gray == tmp.getRed();

		final double coef = (gray - minGray) / 256.0;
		final Color result = ColorUtils.grayToColor(coef, colorForMonochrome.color);
		return new HColorSimple(result, monochrome);
	}

	public HColorSimple opposite() {
		final Color mono = new ColorChangerMonochrome().getChangedColor(color);
		final int grayScale = 255 - mono.getGreen() > 127 ? 255 : 0;
		return new HColorSimple(new Color(grayScale, grayScale, grayScale), true);
	}

	public double distance(HColorSimple other) {
		final int diffRed = Math.abs(this.color.getRed() - other.color.getRed());
		final int diffGreen = Math.abs(this.color.getGreen() - other.color.getGreen());
		final int diffBlue = Math.abs(this.color.getBlue() - other.color.getBlue());
		return diffRed * .3 + diffGreen * .59 + diffBlue * .11;
	}

	public final boolean isMonochrome() {
		return monochrome;
	}

	public boolean isGray() {
		if (monochrome)
			return true;
		if (color.getRed() == color.getGreen() && color.getGreen() == color.getBlue())
			return true;
		return false;
	}

	public static HColorSimple unlinear(HColorSimple color1, HColorSimple color2, int completionInt) {
		final HSLColor col1 = new HSLColor(color1.color);
		final HSLColor col2 = new HSLColor(color2.color);

		final float[] hsl1 = col1.getHSL();
		final float[] hsl2 = col2.getHSL();

		if (completionInt > 100)
			completionInt = 100;

		float completion = (float) (completionInt / 100.0);
		completion = completion * completion * completion;
		final float[] hsl = linear(completion, hsl1, hsl2);

		final HSLColor col = new HSLColor(hsl);

		return new HColorSimple(col.getRGB(), color1.monochrome);
	}

	private static float[] linear(float factor, float[] hsl1, float[] hsl2) {
		final float h = linear(factor, hsl1[0], hsl2[0]);
		final float s = linear(factor, hsl1[1], hsl2[1]);
		final float l = linear(factor, hsl1[2], hsl2[2]);
		return new float[] { h, s, l };
	}

	private static float linear(float factor, float x, float y) {
		return (x + (y - x) * factor);
	}

	public HColor withDark(HColor dark) {
		return new HColorSimple(color, monochrome, dark);
	}

	@Override
	public HColor darkSchemeTheme() {
		return dark;
	}

}
