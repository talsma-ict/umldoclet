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
package net.sourceforge.plantuml.skin.rose;

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.AbstractComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class ComponentRoseLine extends AbstractComponent {

	private final HColor color;
	private final boolean continueLine;
	private final UStroke stroke;

	public ComponentRoseLine(ThemeStyle themeStyle, Style style, HColor color, boolean continueLine, UStroke stroke,
			HColorSet set) {
		super(style);
		if (UseStyle.useBetaStyle()) {
			this.color = style.value(PName.LineColor).asColor(themeStyle, set);
			this.stroke = style.getStroke();
		} else {
			this.color = color;
			this.stroke = stroke;
		}
		this.continueLine = continueLine;
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		ug = ug.apply(color);
		if (UseStyle.useBetaStyle()) {
			ug = ug.apply(stroke);
		} else if (continueLine)
			ug = ug.apply(new UStroke());
		else
			ug = ArrowConfiguration.stroke(ug, 5, 5, stroke.getThickness());

		final int x = (int) (dimensionToUse.getWidth() / 2);
		ug.apply(UTranslate.dx(x)).draw(ULine.vline(dimensionToUse.getHeight()));
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return 20;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return 1;
	}

}
