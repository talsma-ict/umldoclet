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

import java.util.List;

import net.sourceforge.plantuml.activitydiagram3.Instruction;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileBreak;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Genealogy;
import net.sourceforge.plantuml.activitydiagram3.ftile.Hexagon;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.WeldingPoint;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FtileFactoryDelegatorWhile extends FtileFactoryDelegator {

	public FtileFactoryDelegatorWhile(FtileFactory factory) {
		super(factory);
	}

	@Override
	public Ftile createWhile(LinkRendering outColor, Swimlane swimlane, Ftile whileBlock, Display test, Display yes,
			HColor color, Instruction specialOut, Ftile backward, LinkRendering incoming1, LinkRendering incoming2) {

		final ConditionStyle conditionStyle = skinParam().getConditionStyle();

		final Style styleArrow = getDefaultStyleDefinitionArrow().getMergedStyle(skinParam().getCurrentStyleBuilder());
		final Style styleDiamond = getDefaultStyleDefinitionDiamond()
				.getMergedStyle(skinParam().getCurrentStyleBuilder());
		final HColor borderColor = styleDiamond.value(PName.LineColor).asColor(skinParam().getIHtmlColorSet());
		final HColor backColor = styleDiamond.value(PName.BackGroundColor).asColor(skinParam().getIHtmlColorSet());
		final Rainbow arrowColor = Rainbow.build(styleArrow, skinParam().getIHtmlColorSet());
		final FontConfiguration fontArrow = styleArrow.getFontConfiguration(skinParam().getIHtmlColorSet());
		final FontConfiguration fcTest = styleDiamond.getFontConfiguration(skinParam().getIHtmlColorSet());

		incoming1 = ensureColor(incoming1, arrowColor);
		incoming2 = ensureColor(incoming2, arrowColor);
		outColor = ensureColor(outColor, arrowColor);

		Ftile result = FtileWhile.create(outColor, swimlane, whileBlock, test, borderColor, backColor, arrowColor, yes,
				fontArrow, getFactory(), conditionStyle, fcTest, specialOut, backward, incoming1, incoming2);

		final List<WeldingPoint> weldingPoints = whileBlock.getWeldingPoints();
		if (weldingPoints.size() > 0) {
			// printAllChild(repeat);

			final Genealogy genealogy = new Genealogy(result);

			for (WeldingPoint w : weldingPoints) {
				final FtileBreak ftileBreak = (FtileBreak) w;
				result = FtileUtils.addConnection(result, new Connection() {
					public void drawU(UGraphic ug) {
						final UTranslate tr1 = genealogy.getTranslate(ftileBreak, ug.getStringBounder());

						final Snake snake = Snake.create(skinParam(), arrowColor, skinParam().arrows().asToLeft());
						snake.addPoint(tr1.getDx(), tr1.getDy());
						snake.addPoint(Hexagon.hexagonHalfSize, tr1.getDy());
						ug.draw(snake);
					}

					public Ftile getFtile1() {
						return ftileBreak;
					}

					public Ftile getFtile2() {
						return null;
					}

				});
			}
		}

		return result;
	}

	private LinkRendering ensureColor(LinkRendering link, Rainbow color) {
		if (link.getRainbow().size() == 0)
			return link.withRainbow(color);

		return link;
	}

}
