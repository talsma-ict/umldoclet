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
package net.sourceforge.plantuml.svek.image;

import java.util.Collections;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGroupType;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class EntityImageState extends EntityImageStateCommon {

	final private TextBlock fields;

	final private static int MIN_WIDTH = 50;
	final private static int MIN_HEIGHT = 50;

	final private boolean withSymbol;

	final static private double smallRadius = 3;
	final static private double smallLine = 3;
	final static private double smallMarginX = 7;
	final static private double smallMarginY = 4;

	public EntityImageState(IEntity entity, ISkinParam skinParam) {
		super(entity, skinParam);

		final Stereotype stereotype = entity.getStereotype();

		this.withSymbol = stereotype != null && stereotype.isWithOOSymbol();
		final Display list = Display.create(entity.getBodier().getRawBody());
		final FontConfiguration fontConfiguration = getStyleState().getFontConfiguration(getSkinParam().getThemeStyle(),
				getSkinParam().getIHtmlColorSet());

		this.fields = list.create8(fontConfiguration, HorizontalAlignment.LEFT, skinParam, CreoleMode.FULL,
				skinParam.wrapWidth());

	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = Dimension2DDouble.mergeTB(desc.calculateDimension(stringBounder),
				fields.calculateDimension(stringBounder));
		double heightSymbol = 0;
		if (withSymbol)
			heightSymbol += 2 * smallRadius + smallMarginY;

		final Dimension2D result = Dimension2DDouble.delta(dim, MARGIN * 2 + 2 * MARGIN_LINE + heightSymbol);
		return Dimension2DDouble.atLeast(result, MIN_WIDTH, MIN_HEIGHT);
	}

	final public void drawU(UGraphic ug) {
		ug.startGroup(Collections.singletonMap(UGroupType.ID, getEntity().getIdent().toString(".")));
		if (url != null)
			ug.startUrl(url);

		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimDesc = desc.calculateDimension(stringBounder);

		final UStroke stroke = getStyleState().getStroke(lineConfig.getColors());

		ug = applyColor(ug);
		ug = ug.apply(stroke);
		ug.draw(getShape(dimTotal));

		final double yLine = MARGIN + dimDesc.getHeight() + MARGIN_LINE;
		ug.apply(UTranslate.dy(yLine)).draw(ULine.hline(dimTotal.getWidth()));

		if (withSymbol) {
			final double xSymbol = dimTotal.getWidth();
			final double ySymbol = dimTotal.getHeight();
			drawSymbol(ug, xSymbol, ySymbol);
		}

		final double xDesc = (dimTotal.getWidth() - dimDesc.getWidth()) / 2;
		final double yDesc = MARGIN;
		desc.drawU(ug.apply(new UTranslate(xDesc, yDesc)));

		final double xFields = MARGIN;
		final double yFields = yLine + MARGIN_LINE;
		fields.drawU(ug.apply(new UTranslate(xFields, yFields)));

		if (url != null)
			ug.closeUrl();

		ug.closeGroup();
	}

	public static void drawSymbol(UGraphic ug, double xSymbol, double ySymbol) {
		xSymbol -= 4 * smallRadius + smallLine + smallMarginX;
		ySymbol -= 2 * smallRadius + smallMarginY;
		final UEllipse small = new UEllipse(2 * smallRadius, 2 * smallRadius);
		ug.apply(new UTranslate(xSymbol, ySymbol)).draw(small);
		ug.apply(new UTranslate(xSymbol + smallLine + 2 * smallRadius, ySymbol)).draw(small);
		ug.apply(new UTranslate(xSymbol + 2 * smallRadius, ySymbol + smallLine)).draw(ULine.hline(smallLine));
	}

}
