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

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class ElementDroplist extends AbstractElementText implements Element {

	private final int box = 12;
	private final TextBlock openDrop;

	public ElementDroplist(String text, UFont font, ISkinSimple spriteContainer) {
		super(extract(text), font, true, spriteContainer);
		final StringTokenizer st = new StringTokenizer(text, "^");
		final List<String> drop = new ArrayList<>();
		while (st.hasMoreTokens()) {
			drop.add(st.nextToken());
		}
		if (drop.size() > 0) {
			drop.remove(0);
		}
		if (drop.size() == 0) {
			this.openDrop = null;
		} else {
			this.openDrop = Display.create(drop).create(getConfig(), HorizontalAlignment.LEFT, spriteContainer);
		}
	}

	private static String extract(String text) {
		final int idx = text.indexOf('^');
		if (idx == -1) {
			return text;
		}
		return text.substring(0, idx);
	}

	public Dimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		final Dimension2D dim = getTextDimensionAt(stringBounder, x + 2);
		return Dimension2DDouble.delta(dim, 4 + box, 4);
	}

	public void drawU(UGraphic ug, int zIndex, Dimension2D dimToUse) {
		final Dimension2D dim = getPreferredDimension(ug.getStringBounder(), 0, 0);
		ug = ug.apply(getBlack());
		
		if (zIndex == 0) {
			ug.apply(getColorEE().bg())
					.draw(new URectangle(dim.getWidth() - 1, dim.getHeight() - 1));
			drawText(ug, 2, 2);
			final double xline = dim.getWidth() - box;
			ug.apply(UTranslate.dx(xline)).draw(ULine.vline(dim.getHeight() - 1));

			final UPolygon poly = new UPolygon();
			poly.addPoint(0, 0);
			poly.addPoint(box - 6, 0);
			final Dimension2D dimText = getPureTextDimension(ug.getStringBounder());
			poly.addPoint((box - 6) / 2, dimText.getHeight() - 8);

			ug.apply(HColorUtils.changeBack(ug)).apply(new UTranslate(xline + 3, 6)).draw(poly);
		}

		if (openDrop != null) {
			final Dimension2D dimOpen = Dimension2DDouble.atLeast(openDrop.calculateDimension(ug.getStringBounder()),
					dim.getWidth() - 1, 0);
			ug = ug.apply(UTranslate.dy(dim.getHeight() - 1));
			ug.apply(getColorEE().bg())
					.draw(new URectangle(dimOpen.getWidth() - 1, dimOpen.getHeight() - 1));
			openDrop.drawU(ug);
		}
	}
}
