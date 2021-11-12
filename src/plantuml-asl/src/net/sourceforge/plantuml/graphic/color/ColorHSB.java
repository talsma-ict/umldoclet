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

import java.awt.Color;
import java.util.Locale;

/**
 * {@link Color} with hue, saturation and brightness.
 */
public class ColorHSB extends Color {
	private final float hue;
	private final float saturation;
	private final float brightness;

	public ColorHSB(int rgba) {
		super(rgba, true);
		final float[] hsb = Color.RGBtoHSB(getRed(), getGreen(), getBlue(), null);
		hue = hsb[0];
		saturation = hsb[1];
		brightness = hsb[2];
	}

	public ColorHSB(Color other) {
		this(other.getRGB());
	}

	public float getHue() {
		return hue;
	}

	public float getSaturation() {
		return saturation;
	}

	public float getBrightness() {
		return brightness;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "%s[a=%02X r=%02X g=%02X b=%02X / h=%f s=%f b=%f]",
				getClass().getSimpleName(), getAlpha(), getRed(), getGreen(), getBlue(), getHue(), getSaturation(), getBrightness()
		);
	}
}
