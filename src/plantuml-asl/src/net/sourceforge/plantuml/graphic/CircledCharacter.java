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

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class CircledCharacter extends AbstractTextBlock implements TextBlock {

	private final String c;
	private final UFont font;
	private final HtmlColor innerCircle;
	private final HtmlColor circle;
	private final HtmlColor fontColor;
	private final double radius;

	public CircledCharacter(char c, double radius, UFont font, HtmlColor innerCircle, HtmlColor circle,
			HtmlColor fontColor) {
		this.c = "" + c;
		this.radius = radius;
		this.font = font;
		this.innerCircle = innerCircle;
		this.circle = circle;
		this.fontColor = fontColor;
	}

	// public void draw(ColorMapper colorMapper, Graphics2D g2d, int x, int y, double dpiFactor) {
	// drawU(new UGraphicG2d(colorMapper, g2d, null, 1.0), x, y);
	// }

	public void drawU(UGraphic ug) {
		if (circle != null) {
			ug = ug.apply(new UChangeColor(circle));
		}
		// final HtmlColor back = ug.getParam().getBackcolor();
		ug = ug.apply(new UChangeBackColor(innerCircle));
		ug.draw(new UEllipse(radius * 2, radius * 2));
		ug = ug.apply(new UChangeColor(fontColor));
		ug = ug.apply(new UTranslate(radius, radius));
		ug.draw(new UCenteredCharacter(c.charAt(0), font));
	}

	final public double getPreferredWidth(StringBounder stringBounder) {
		return 2 * radius;
	}

	final public double getPreferredHeight(StringBounder stringBounder) {
		return 2 * radius;
	}

	// private PathIterator getPathIteratorCharacter(FontRenderContext frc) {
	// final TextLayout textLayout = new TextLayout(c, font.getFont(), frc);
	// final Shape s = textLayout.getOutline(null);
	// return s.getPathIterator(null);
	// }
	//
	// private UPath getUPath(FontRenderContext frc) {
	// final UPath result = new UPath();
	//
	// final PathIterator path = getPathIteratorCharacter(frc);
	//
	// final double coord[] = new double[6];
	// while (path.isDone() == false) {
	// final int code = path.currentSegment(coord);
	// result.add(coord, USegmentType.getByCode(code));
	// path.next();
	// }
	//
	// return result;
	// }

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(getPreferredWidth(stringBounder), getPreferredHeight(stringBounder));
	}
}
