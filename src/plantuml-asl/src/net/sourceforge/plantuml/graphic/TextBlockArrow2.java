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

import java.awt.geom.Point2D;
import java.util.Objects;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.svek.GuideLine;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class TextBlockArrow2 extends AbstractTextBlock implements TextBlock {

	private final double size;
	private final GuideLine angle;
	private final HColor color;

	public TextBlockArrow2(GuideLine angle, FontConfiguration fontConfiguration) {
		this.angle = Objects.requireNonNull(angle);
		this.size = fontConfiguration.getFont().getSize2D();
		this.color = fontConfiguration.getColor();

	}

	public void drawU(UGraphic ug) {
		// final double triSize = size * .80;
		final int triSize = (int) (size * .80);

		ug = ug.apply(color);
		ug = ug.apply(color.bg());
		ug = ug.apply(new UTranslate(triSize / 2, size / 2));

		final UPolygon triangle = new UPolygon();
		final double beta = Math.PI * 4 / 5;
		triangle.addPoint(getPoint(triSize / 2, angle.getArrowDirection2()));
		triangle.addPoint(getPoint(triSize / 2, angle.getArrowDirection2() + beta));
		triangle.addPoint(getPoint(triSize / 2, angle.getArrowDirection2() - beta));
		triangle.addPoint(getPoint(triSize / 2, angle.getArrowDirection2()));
		ug.draw(triangle);
	}

	private Point2D getPoint(double len, double alpha) {
		final double dx = len * Math.sin(alpha);
		final double dy = len * Math.cos(alpha);
		return new Point2D.Double(dx, dy);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(size, size);
	}
}
