/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import java.util.List;

import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.style.ISkinSimple;

public class ElementRadioCheckbox extends AbstractElement {

	private static final int RECTANGLE = 10;
	private static final int ELLIPSE = 10;
	private static final int ELLIPSE2 = 4;
	private final TextBlock block;
	private final int margin = 20;
	private final double stroke = 1.5;
	private final boolean radio;
	private final boolean checked;

	public ElementRadioCheckbox(List<String> text, UFont font, boolean radio, boolean checked,
			ISkinSimple spriteContainer) {
		final FontConfiguration config = FontConfiguration.blackBlueTrue(font);
		this.block = Display.create(text).create(config, HorizontalAlignment.LEFT, spriteContainer);
		this.radio = radio;
		this.checked = checked;
	}

	public XDimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		final XDimension2D dim = block.calculateDimension(stringBounder);
		return dim.delta(margin, 0);
	}

	public void drawU(UGraphic ug, int zIndex, XDimension2D dimToUse) {
		if (zIndex != 0)
			return;

		ug = ug.apply(getBlack());
		block.drawU(ug.apply(UTranslate.dx(margin)));

		final XDimension2D dim = getPreferredDimension(ug.getStringBounder(), 0, 0);
		final double height = dim.getHeight();

		ug = ug.apply(UStroke.withThickness(stroke));
		if (radio) {
			drawRadio(ug, height);
		} else {
			drawOther(ug, height);
		}
	}

	private void drawOther(UGraphic ug, final double height) {
		ug.apply(new UTranslate(2, (height - RECTANGLE) / 2)).draw(URectangle.build(RECTANGLE, RECTANGLE));
		if (checked) {
			final UPolygon poly = new UPolygon();
			poly.addPoint(0, 0);
			poly.addPoint(3, 3);
			poly.addPoint(10, -6);
			poly.addPoint(3, 1);
			ug = ug.apply(HColors.changeBack(ug));
			ug = ug.apply(new UTranslate(3, 6));
			ug.draw(poly);
		}
	}

	private void drawRadio(UGraphic ug, final double height) {
		ug.apply(new UTranslate(2, (height - ELLIPSE) / 2)).draw(UEllipse.build(ELLIPSE, ELLIPSE));
		if (checked) {
			ug = ug.apply(HColors.changeBack(ug));
			ug = ug.apply(new UTranslate(2 + (ELLIPSE - ELLIPSE2) / 2, (height - ELLIPSE2) / 2));
			ug.draw(UEllipse.build(ELLIPSE2, ELLIPSE2));
		}
	}
}
