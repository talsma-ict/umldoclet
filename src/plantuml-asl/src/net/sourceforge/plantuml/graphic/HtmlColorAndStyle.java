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
package net.sourceforge.plantuml.graphic;

import java.util.Objects;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.cucadiagram.LinkStyle;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class HtmlColorAndStyle {

	private final HColor arrowHeadColor;
	private final HColor arrowColor;
	private final LinkStyle style;

	@Override
	public String toString() {
		return arrowColor + " " + style;
	}

	public HtmlColorAndStyle(HColor color, HColor arrowHeadColor) {
		this(color, LinkStyle.NORMAL(), arrowHeadColor);
	}

	public HtmlColorAndStyle(HColor arrowColor, LinkStyle style, HColor arrowHeadColor) {
		this.arrowColor = Objects.requireNonNull(arrowColor);
		this.arrowHeadColor = arrowHeadColor == null ? arrowColor : arrowHeadColor;
		this.style = style;
	}

	public HColor getArrowColor() {
		return arrowColor;
	}

	public HColor getArrowHeadColor() {
		return arrowHeadColor;
	}

	public LinkStyle getStyle() {
		return style;
	}

	static final public StyleSignatureBasic getDefaultStyleDefinitionArrow() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.arrow);
	}

	public static HtmlColorAndStyle build(ISkinParam skinParam, String definition) throws NoSuchColorException {

		final Style style = getDefaultStyleDefinitionArrow().getMergedStyle(skinParam.getCurrentStyleBuilder());
		HColor arrowColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());
		final HColor arrowHeadColor = null;

		LinkStyle linkStyle = LinkStyle.NORMAL();
		final HColorSet set = skinParam.getIHtmlColorSet();
		for (String s : definition.split(",")) {
			final LinkStyle tmpStyle = LinkStyle.fromString1(s);
			if (tmpStyle.isNormal() == false) {
				linkStyle = tmpStyle;
				continue;
			}
			final HColor tmpColor = s == null ? null : set.getColor(skinParam.getThemeStyle(), s);
			if (tmpColor != null)
				arrowColor = tmpColor;

		}
		return new HtmlColorAndStyle(arrowColor, linkStyle, arrowHeadColor);
	}

}
