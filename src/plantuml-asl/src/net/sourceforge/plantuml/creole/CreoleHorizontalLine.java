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
package net.sourceforge.plantuml.creole;

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.creole.atom.AbstractAtom;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UHorizontalLine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class CreoleHorizontalLine extends AbstractAtom implements Atom {

	private final FontConfiguration fontConfiguration;
	private final String line;
	private final char style;
	private final ISkinSimple skinParam;

	private final static double defaultThickness = 1;

	public static CreoleHorizontalLine create(FontConfiguration fontConfiguration, String line, char style,
			ISkinSimple skinParam) {
		return new CreoleHorizontalLine(fontConfiguration, line, style, skinParam);
	}

	private CreoleHorizontalLine(FontConfiguration fontConfiguration, String line, char style, ISkinSimple skinParam) {
		this.fontConfiguration = fontConfiguration;
		this.line = line;
		this.style = style;
		this.skinParam = skinParam;
	}

	private UHorizontalLine getHorizontalLine() {
		if (line.length() == 0) {
			return UHorizontalLine.infinite(defaultThickness, 0, 0, style);
		}
		final TextBlock tb = getTitle();
		return UHorizontalLine.infinite(defaultThickness, 0, 0, tb, style);
	}

	private TextBlock getTitle() {
		if (line.length() == 0) {
			return TextBlockUtils.empty(0, 0);
		}
		final SheetBuilder parser = Parser.build(fontConfiguration, HorizontalAlignment.LEFT, skinParam,
				CreoleMode.FULL);
		final Sheet sheet = parser.createSheet(Display.getWithNewlines(line));
		final TextBlock tb = new SheetBlock1(sheet, LineBreakStrategy.NONE, skinParam.getPadding());
		return tb;
	}

	public void drawU(UGraphic ug) {
		// ug = ug.apply(UChangeColor.nnn(fontConfiguration.getColor()));
		final Dimension2D dim = calculateDimension(ug.getStringBounder());
		ug = ug.apply(UTranslate.dy(dim.getHeight() / 2));
		ug.draw(getHorizontalLine());
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		if (line.length() == 0) {
			return new Dimension2DDouble(10, 10);
		}
		final TextBlock tb = getTitle();
		return tb.calculateDimension(stringBounder);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

}
