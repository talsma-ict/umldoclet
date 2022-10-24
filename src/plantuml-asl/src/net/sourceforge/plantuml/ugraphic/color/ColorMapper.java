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

public abstract class ColorMapper {

	private ColorMapper() {
	}

	public abstract Color fromColorSimple(HColorSimple simple);

	public final static ColorMapper IDENTITY = new ColorMapper() {
		@Override
		public Color fromColorSimple(HColorSimple simple) {
			return simple.getAwtColor();
		}
	};
	public final static ColorMapper DARK_MODE = new ColorMapper() {
		@Override
		public Color fromColorSimple(HColorSimple simple) {
			return ((HColorSimple) simple.darkSchemeTheme()).getAwtColor();
		}
	};
	public final static ColorMapper LIGTHNESS_INVERSE = new ColorMapper() {
		@Override
		public Color fromColorSimple(HColorSimple simple) {
			return ColorUtils.getReversed(simple.getAwtColor());
		}
	};
	public static final ColorMapper MONOCHROME = new ColorMapper() {
		@Override
		public Color fromColorSimple(HColorSimple simple) {
			return ColorUtils.getGrayScaleColor(simple.getAwtColor());
		}
	};
	public static final ColorMapper MONOCHROME_REVERSE = new ColorMapper() {
		@Override
		public Color fromColorSimple(HColorSimple simple) {
			return ColorUtils.getGrayScaleColorReverse(simple.getAwtColor());
		}
	};

	public static ColorMapper reverse(final ColorOrder order) {
		return new ColorMapper() {
			@Override
			public Color fromColorSimple(HColorSimple simple) {
				return order.getReverse(simple.getAwtColor());
			}
		};
	}

}
