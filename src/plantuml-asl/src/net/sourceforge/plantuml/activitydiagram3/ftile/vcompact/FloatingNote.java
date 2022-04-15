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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.image.Opale;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FloatingNote extends AbstractTextBlock implements Stencil, TextBlock {

	private final Opale opale;

	public FloatingNote(Display note, ISkinParam skinParam) {

		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.note)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final LineBreakStrategy wrapWidth = style.wrapWidth();
		final FontConfiguration fc = FontConfiguration.create(skinParam, style);
		final HColor noteBackgroundColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());
		final HColor borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());
		final UStroke stroke = style.getStroke();
		final double shadowing = style.value(PName.Shadowing).asDouble();

		final Sheet sheet = Parser
				.build(fc, skinParam.getDefaultTextAlignment(HorizontalAlignment.LEFT), skinParam, CreoleMode.FULL)
				.createSheet(note);
		final SheetBlock2 sheetBlock2 = new SheetBlock2(new SheetBlock1(sheet, wrapWidth, skinParam.getPadding()), this,
				stroke);
		this.opale = new Opale(shadowing, borderColor, noteBackgroundColor, sheetBlock2, false, stroke);

	}

	public void drawU(UGraphic ug) {
		opale.drawU(ug);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return opale.calculateDimension(stringBounder);
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return -opale.getMarginX1();
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return opale.calculateDimension(stringBounder).getWidth() - opale.getMarginX1();
	}

}
