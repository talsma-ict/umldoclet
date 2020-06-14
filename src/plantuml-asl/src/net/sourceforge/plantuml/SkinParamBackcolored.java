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
package net.sourceforge.plantuml;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class SkinParamBackcolored extends SkinParamDelegator {

	final private HtmlColor backColorElement;
	final private HtmlColor backColorGeneral;
	final private boolean forceClickage;

	public SkinParamBackcolored(ISkinParam skinParam, HtmlColor backColorElement) {
		this(skinParam, backColorElement, null, false);
	}

	public SkinParamBackcolored(ISkinParam skinParam, HtmlColor backColorElement, boolean forceClickage) {
		this(skinParam, backColorElement, null, forceClickage);
	}

	public SkinParamBackcolored(ISkinParam skinParam, HtmlColor backColorElement, HtmlColor backColorGeneral) {
		this(skinParam, backColorElement, backColorGeneral, false);
	}

	@Override
	public String toString() {
		return super.toString() + " " + backColorElement + " " + backColorGeneral;
	}

	public SkinParamBackcolored(ISkinParam skinParam, HtmlColor backColorElement, HtmlColor backColorGeneral,
			boolean forceClickage) {
		super(skinParam);
		this.forceClickage = forceClickage;
		this.backColorElement = backColorElement;
		this.backColorGeneral = backColorGeneral;
	}

	@Override
	public HtmlColor getBackgroundColor() {
		if (backColorGeneral != null) {
			return backColorGeneral;
		}
		return super.getBackgroundColor();
	}

	@Override
	public HtmlColor getHtmlColor(ColorParam param, Stereotype stereotype, boolean clickable) {
		if (param.isBackground() && backColorElement != null) {
			return backColorElement;
		}
		if (forceClickage) {
			final HtmlColor c1 = super.getHtmlColor(param, stereotype, true);
			if (c1 != null) {
				return c1;
			}
			// clickable = true;
		}
		final HtmlColor forcedColor = forced.get(param);
		if (forcedColor != null) {
			return forcedColor;
		}
		return super.getHtmlColor(param, stereotype, clickable);
	}

	private final Map<ColorParam, HtmlColor> forced = new EnumMap<ColorParam, HtmlColor>(ColorParam.class);

	public void forceColor(ColorParam param, HtmlColor color) {
		forced.put(param, color);
	}

}
