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

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.cucadiagram.LinkStyle;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class HtmlColorAndStyle {

	private final HColor color;
	private final LinkStyle style;

	@Override
	public String toString() {
		return color + " " + style;
	}

	public HtmlColorAndStyle(HColor color) {
		this(color, LinkStyle.NORMAL());
	}

	public HtmlColorAndStyle(HColor color, LinkStyle style) {
		if (color == null) {
			throw new IllegalArgumentException();
		}
		this.color = color;
		this.style = style;
	}

	public HColor getColor() {
		return color;
	}

	public LinkStyle getStyle() {
		return style;
	}

	static final public StyleSignature getDefaultStyleDefinitionArrow() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.arrow);
	}

	public static HtmlColorAndStyle build(ISkinParam skinParam, String definition) {
		HColor color;
		if (SkinParam.USE_STYLES()) {
			final Style style = getDefaultStyleDefinitionArrow().getMergedStyle(skinParam.getCurrentStyleBuilder());
			color = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
		} else {
			color = Rainbow.build(skinParam).getColors().get(0).color;
		}
		LinkStyle style = LinkStyle.NORMAL();
		final HColorSet set = skinParam.getIHtmlColorSet();
		for (String s : definition.split(",")) {
			final LinkStyle tmpStyle = LinkStyle.fromString1(s);
			if (tmpStyle.isNormal() == false) {
				style = tmpStyle;
				continue;
			}
			final HColor tmpColor = set.getColorIfValid(s);
			if (tmpColor != null) {
				color = tmpColor;
			}
		}
		return new HtmlColorAndStyle(color, style);
	}

}
