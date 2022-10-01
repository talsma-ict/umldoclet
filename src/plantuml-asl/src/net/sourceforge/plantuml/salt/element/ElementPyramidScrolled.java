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
package net.sourceforge.plantuml.salt.element;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.salt.Positionner2;
import net.sourceforge.plantuml.salt.factory.ScrollStrategy;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ElementPyramidScrolled extends ElementPyramid {

	private final double v1 = 15;
	private final double v2 = 12;
	private final ScrollStrategy scrollStrategy;

	public ElementPyramidScrolled(Positionner2 positionner, ISkinSimple spriteContainer,
			ScrollStrategy scrollStrategy) {
		super(positionner, TableStrategy.DRAW_OUTSIDE, null, spriteContainer);
		this.scrollStrategy = scrollStrategy;
	}

	@Override
	public XDimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		final XDimension2D result = super.getPreferredDimension(stringBounder, x, y);
		if (scrollStrategy == ScrollStrategy.HORIZONTAL_ONLY)
			return XDimension2D.delta(result, 0, 30);

		if (scrollStrategy == ScrollStrategy.VERTICAL_ONLY)
			return XDimension2D.delta(result, 30, 0);

		return XDimension2D.delta(result, 30);
	}

	@Override
	public void drawU(UGraphic ug, int zIndex, XDimension2D dimToUse) {
		super.drawU(ug, zIndex, dimToUse);
		ug = ug.apply(getBlack());
		final XDimension2D dim = super.getPreferredDimension(ug.getStringBounder(), 0, 0);
		if (scrollStrategy == ScrollStrategy.BOTH || scrollStrategy == ScrollStrategy.VERTICAL_ONLY)
			drawV(ug.apply(UTranslate.dx(dim.getWidth() + 4)), v1, dim.getHeight());

		if (scrollStrategy == ScrollStrategy.BOTH || scrollStrategy == ScrollStrategy.HORIZONTAL_ONLY)
			drawH(ug.apply(UTranslate.dy(dim.getHeight() + 4)), dim.getWidth(), v1);

	}

	private UPath getTr0() {
		final UPath poly = new UPath();
		poly.moveTo(3, 0);
		poly.lineTo(6, 5);
		poly.lineTo(0, 5);
		poly.lineTo(3, 0);
		poly.closePath();
		return poly;
	}

	private UPath getTr180() {
		final UPath poly = new UPath();
		poly.moveTo(3, 5);
		poly.lineTo(6, 0);
		poly.lineTo(0, 0);
		poly.lineTo(3, 5);
		poly.closePath();
		return poly;
	}

	private UPath getTr90() {
		final UPath poly = new UPath();
		poly.moveTo(0, 3);
		poly.lineTo(5, 6);
		poly.lineTo(5, 0);
		poly.lineTo(0, 3);
		poly.closePath();
		return poly;
	}

	private UPath getTr270() {
		final UPath poly = new UPath();
		poly.moveTo(5, 3);
		poly.lineTo(0, 6);
		poly.lineTo(0, 0);
		poly.lineTo(5, 3);
		poly.closePath();
		return poly;
	}

	private void drawV(UGraphic ug, double width, double height) {
		ug.draw(new URectangle(width, height));
		ug.apply(UTranslate.dy(v2)).draw(ULine.hline(width));
		ug.apply(UTranslate.dy(height - v2)).draw(ULine.hline(width));
		ug.apply(new UTranslate(4, 4)).apply(getBlack().bg()).draw(getTr0());
		ug.apply(new UTranslate(4, height - v2 + 4)).apply(getBlack().bg()).draw(getTr180());
	}

	private void drawH(UGraphic ug, double width, double height) {
		ug.draw(new URectangle(width, height));
		ug.apply(UTranslate.dx(v2)).draw(ULine.vline(height));
		ug.apply(UTranslate.dx(width - v2)).draw(ULine.vline(height));
		ug.apply(new UTranslate(4, 4)).apply(getBlack().bg()).draw(getTr90());
		ug.apply(new UTranslate(width - v2 + 4, 4)).apply(getBlack().bg()).draw(getTr270());
	}

}
