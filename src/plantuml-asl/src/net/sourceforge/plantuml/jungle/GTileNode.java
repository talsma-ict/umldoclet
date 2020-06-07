/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.jungle;

import java.awt.geom.Dimension2D;
import java.util.Arrays;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class GTileNode extends AbstractTextBlock implements GTile {

	private final TextBlock tb;

	public GTileNode(GNode node) {
		final Display display = node.getDisplay();
		final SheetBlock1 sheetBlock1 = getTextBlock(display);

		final SymbolContext symbolContext = new SymbolContext(HColorUtils.MY_YELLOW, HColorUtils.BLACK);
		tb = USymbol.RECTANGLE.asSmall(null, sheetBlock1, TextBlockUtils.empty(0, 0), symbolContext,
				HorizontalAlignment.CENTER);
	}

	public static SheetBlock1 getTextBlock(final Display display) {
		final Rose rose = new Rose();
		final SkinParam skinParam = SkinParam.create(null);
		final HColor fontColor = rose.getFontColor(skinParam, FontParam.NOTE);
		final UFont fontNote = skinParam.getFont(null, false, FontParam.NOTE);

		final FontConfiguration fc = new FontConfiguration(skinParam, FontParam.NOTE, null);

		final Sheet sheet9 = Parser.build(fc, HorizontalAlignment.LEFT, skinParam, CreoleMode.FULL)
				.createSheet(display);
		final SheetBlock1 sheetBlock1 = new SheetBlock1(sheet9, LineBreakStrategy.NONE, 0);
		return sheetBlock1;
	}

	public void drawU(UGraphic ug) {
		tb.drawU(ug);
	}

	public GTileGeometry calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = tb.calculateDimension(stringBounder);
		return new GTileGeometry(dim, Arrays.asList(dim.getHeight() / 2));
	}

}
