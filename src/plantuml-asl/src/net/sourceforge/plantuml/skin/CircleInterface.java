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
package net.sourceforge.plantuml.skin;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class CircleInterface implements UDrawable {

	private final float thickness;
	private final double headDiam;
	private final HColor backgroundColor;
	private final HColor foregroundColor;

	public CircleInterface(HColor backgroundColor, HColor foregroundColor, double headDiam, float thickness) {
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.headDiam = headDiam;
		this.thickness = thickness;
	}

	public void drawU(UGraphic ug) {
		final UEllipse head = new UEllipse(headDiam, headDiam);
		
		ug.apply(new UStroke(thickness)).apply(backgroundColor.bg())
		.apply(foregroundColor).apply(new UTranslate(thickness, thickness)).draw(head);
	}

	public double getPreferredWidth(StringBounder stringBounder) {
		return headDiam + 2 * thickness;
	}

	public double getPreferredHeight(StringBounder stringBounder) {
		return headDiam + 2 * thickness;
	}

}
