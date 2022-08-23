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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.activitydiagram3.ftile.Hexagon;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class Gtiles {

	static public GtileHexagonInside hexagonInside(Swimlane swimlane, StringBounder stringBounder, ISkinParam skinParam,
			StyleSignatureBasic styleSignature, HColor color, Display label) {

		final Style style = styleSignature.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());
		final HColor backColor = color == null
				? style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet())
				: color;
		final FontConfiguration fcTest = style.getFontConfiguration(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());

		final Sheet sheet = Parser
				.build(fcTest, skinParam.getDefaultTextAlignment(HorizontalAlignment.LEFT), skinParam, CreoleMode.FULL)
				.createSheet(label);
		final SheetBlock1 sheetBlock1 = new SheetBlock1(sheet, LineBreakStrategy.NONE, skinParam.getPadding());
		final TextBlock tbTest = new SheetBlock2(sheetBlock1, Hexagon.asStencil(sheetBlock1), new UStroke());

		return new GtileHexagonInside(stringBounder, tbTest, skinParam, backColor, borderColor, swimlane);
	}

	static public AbstractGtileRoot diamondEmpty(Swimlane swimlane, StringBounder stringBounder, ISkinParam skinParam,
			StyleSignatureBasic styleSignature, HColor color) {

		final Style style = styleSignature.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(),
				skinParam.getIHtmlColorSet());
		final HColor backColor = color == null
				? style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet())
				: color;

		return new GtileHexagonInside(stringBounder, TextBlockUtils.EMPTY_TEXT_BLOCK, skinParam, backColor, borderColor,
				swimlane);
	}

	static public Gtile withSouthMargin(Gtile orig, double south) {
		return new GtileWithMargin((AbstractGtileRoot) orig, 0, south, 0);

	}

	static public Gtile withNorthMargin(Gtile orig, double north) {
		return new GtileWithMargin((AbstractGtileRoot) orig, north, 0, 0);
	}

//	static public Gtile withEastMargin(Gtile orig, double east) {
//		return new GtileWithMargin((AbstractGtileRoot) orig, 0, 0, east);
//	}

	public static Gtile withIncomingArrow(Gtile orig, double margin) {
		if (orig instanceof GtileEmpty)
			return orig;
		return new GtileWithIncomingArrow((AbstractGtileRoot) orig, margin);
	}

	public static Gtile withOutgoingArrow(Gtile orig, double margin) {
		if (orig instanceof GtileEmpty)
			return orig;
		return new GtileWithOutgoingArrow((AbstractGtileRoot) orig, margin);
	}

}
