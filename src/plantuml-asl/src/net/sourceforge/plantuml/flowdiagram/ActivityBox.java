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
package net.sourceforge.plantuml.flowdiagram;

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.golem.Tile;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class ActivityBox extends AbstractTextBlock {

	private static final int CORNER = 25;
	private static final int MARGIN = 10;

	private final Tile tile;
	private final String id;
	private final String label;
	private final TextBlock tb;

	public ActivityBox(Tile tile, String id, String label) {
		this.tile = tile;
		this.id = id;
		this.label = label;
		final UFont font = UFont.serif(14);
		final FontConfiguration fc = FontConfiguration.blackBlueTrue(font);
		tb = Display.create(label).create(fc, HorizontalAlignment.LEFT, new SpriteContainerEmpty());
	}

	public Tile getTile() {
		return tile;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void drawU(UGraphic ug) {
		final Dimension2D dimTotal = calculateDimension(ug.getStringBounder());
		// final Dimension2D dimDesc = tb.calculateDimension(ug.getStringBounder());

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal).rounded(CORNER);
		ug = ug.apply(HColorUtils.MY_RED);
		ug = ug.apply(HColorUtils.MY_YELLOW.bg());
		ug.apply(new UStroke(1.5)).draw(rect);

		tb.drawU(ug.apply(new UTranslate(MARGIN, MARGIN)));
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = tb.calculateDimension(stringBounder);

		return Dimension2DDouble.delta(dim, 2 * MARGIN, 2 * MARGIN);
	}

}
