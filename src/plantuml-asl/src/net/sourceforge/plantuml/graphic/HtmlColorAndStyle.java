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

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.LinkStyle;
import net.sourceforge.plantuml.skin.rose.Rose;

public class HtmlColorAndStyle {

	private final static Rose rose = new Rose();

	private final HtmlColor color;
	private final LinkStyle style;

	@Override
	public String toString() {
		return color + " " + style;
	}

	public static Rainbow fromColor(HtmlColor color) {
		if (color == null) {
			return Rainbow.none();
		}
		return Rainbow.build(new HtmlColorAndStyle(color));
	}

	public static Rainbow build(ISkinParam skinParam) {
		return fromColor(rose.getHtmlColor(skinParam, ColorParam.arrow));
	}

	private HtmlColorAndStyle(HtmlColor color) {
		this(color, LinkStyle.NORMAL());
	}

	public HtmlColorAndStyle(HtmlColor color, LinkStyle style) {
		if (color == null) {
			throw new IllegalArgumentException();
		}
		this.color = color;
		this.style = style;
	}

	public HtmlColor getColor() {
		return color;
	}

	public LinkStyle getStyle() {
		return style;
	}

	public static HtmlColorAndStyle build(ISkinParam skinParam, String definition) {
		HtmlColor color = build(skinParam).getColors().get(0).color;
		LinkStyle style = LinkStyle.NORMAL();
		final IHtmlColorSet set = skinParam.getIHtmlColorSet();
		for (String s : definition.split(",")) {
			final LinkStyle tmpStyle = LinkStyle.fromString1(s);
			if (tmpStyle.isNormal() == false) {
				style = tmpStyle;
				continue;
			}
			final HtmlColor tmpColor = set.getColorIfValid(s);
			if (tmpColor != null) {
				color = tmpColor;
			}
		}
		return new HtmlColorAndStyle(color, style);
	}

}
