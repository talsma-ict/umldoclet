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
package net.sourceforge.plantuml.creole;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Bullet extends AbstractAtom implements Atom {

	private final FontConfiguration fontConfiguration;
	private final int order;

	public Bullet(FontConfiguration fontConfiguration, int order) {
		this.fontConfiguration = fontConfiguration;
		this.order = order;
	}

	private double getWidth(StringBounder stringBounder) {
		final Dimension2D dim = stringBounder.calculateDimension(fontConfiguration.getFont(), "W");
		return dim.getWidth() * (order + 1);
	}

	public void drawU(UGraphic ug) {
		if (order == 0) {
			drawU0(ug);
		} else {
			drawU1(ug);
		}
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		if (order == 0) {
			return calculateDimension0(stringBounder);
		}
		return calculateDimension1(stringBounder);
	}

	private void drawU0(UGraphic ug) {
		final HtmlColor color = fontConfiguration.getColor();
		ug = ug.apply(new UChangeColor(color)).apply(new UChangeBackColor(color)).apply(new UStroke(0));
		// final double width = getWidth(ug.getStringBounder());
		ug = ug.apply(new UTranslate(3, 0));
		ug.draw(new UEllipse(5, 5));
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return -5;
	}

	private Dimension2D calculateDimension0(StringBounder stringBounder) {
		return new Dimension2DDouble(getWidth(stringBounder), 5);
	}

	private void drawU1(UGraphic ug) {
		final HtmlColor color = fontConfiguration.getColor();
		ug = ug.apply(new UChangeColor(color)).apply(new UChangeBackColor(color)).apply(new UStroke(0));
		final double width = getWidth(ug.getStringBounder());
		ug = ug.apply(new UTranslate(width - 5, 0));
		ug.draw(new URectangle(3.5, 3.5));
	}

	private Dimension2D calculateDimension1(StringBounder stringBounder) {
		return new Dimension2DDouble(getWidth(stringBounder), 3);
	}
	

}
