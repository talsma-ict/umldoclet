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
package net.sourceforge.plantuml.graphic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;

public class Rainbow {

	private final List<HtmlColorAndStyle> colors = new ArrayList<HtmlColorAndStyle>();
	private final int colorArrowSeparationSpace;

	private Rainbow(int colorArrowSeparationSpace) {
		this.colorArrowSeparationSpace = colorArrowSeparationSpace;
	}

	@Override
	public String toString() {
		return colors.toString();
	}

	public static Rainbow none() {
		return new Rainbow(0);
	}

	public Rainbow withDefault(Rainbow defaultColor) {
		if (this.size() == 0) {
			return defaultColor;
		}
		return this;
	}

	public static Rainbow build(HtmlColorAndStyle color) {
		if (color == null) {
			throw new IllegalArgumentException();
		}
		final Rainbow result = new Rainbow(0);
		result.colors.add(color);
		return result;
	}

	public static Rainbow build(ISkinParam skinParam, String colorString, int colorArrowSeparationSpace) {
		if (colorString == null) {
			return Rainbow.none();
		}
		final Rainbow result = new Rainbow(colorArrowSeparationSpace);
		for (String s : colorString.split(";")) {
			result.colors.add(HtmlColorAndStyle.build(skinParam, s));
		}
		return result;
	}

	public boolean isInvisible() {
		for (HtmlColorAndStyle style : colors) {
			if (style.getStyle().isInvisible()) {
				return true;
			}
		}
		return false;
	}

	public List<HtmlColorAndStyle> getColors() {
		return Collections.unmodifiableList(colors);
	}

	public HtmlColor getColor() {
		return colors.get(0).getColor();
	}

	public int getColorArrowSeparationSpace() {
		return colorArrowSeparationSpace;
	}

	public int size() {
		return colors.size();
	}

}
