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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineConfigurable;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGroupType;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageState extends AbstractEntityImage {

	final private TextBlock desc;
	final private TextBlock fields;
	final private Url url;

	final private static int MIN_WIDTH = 50;
	final private static int MIN_HEIGHT = 50;

	final private boolean withSymbol;

	final static private double smallRadius = 3;
	final static private double smallLine = 3;
	final static private double smallMarginX = 7;
	final static private double smallMarginY = 4;

	final private LineConfigurable lineConfig;

	public EntityImageState(IEntity entity, ISkinParam skinParam) {
		super(entity, skinParam);
		this.lineConfig = entity;
		final Stereotype stereotype = entity.getStereotype();
		this.withSymbol = stereotype != null && stereotype.isWithOOSymbol();

		this.desc = entity.getDisplay().create8(new FontConfiguration(getSkinParam(), FontParam.STATE, stereotype),
				HorizontalAlignment.CENTER, skinParam, CreoleMode.FULL, skinParam.wrapWidth());

//		Display list = Display.empty();
//		for (Member att : entity.getBodier().getFieldsToDisplay()) {
//			list = list.addAll(Display.getWithNewlines(att.getDisplay(true)));
//		}
		final Display list = Display.create(entity.getBodier().getRawBody());

		this.url = entity.getUrl99();

		this.fields = list.create8(new FontConfiguration(getSkinParam(), FontParam.STATE_ATTRIBUTE, stereotype),
				HorizontalAlignment.LEFT, skinParam, CreoleMode.FULL, skinParam.wrapWidth());

	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = Dimension2DDouble.mergeTB(desc.calculateDimension(stringBounder),
				fields.calculateDimension(stringBounder));
		double heightSymbol = 0;
		if (withSymbol) {
			heightSymbol += 2 * smallRadius + smallMarginY;
		}
		final Dimension2D result = Dimension2DDouble.delta(dim, MARGIN * 2 + 2 * MARGIN_LINE + heightSymbol);
		return Dimension2DDouble.atLeast(result, MIN_WIDTH, MIN_HEIGHT);
	}

	final public void drawU(UGraphic ug) {
		ug.startGroup(UGroupType.ID, getEntity().getIdent().toString("."));
		if (url != null) {
			ug.startUrl(url);
		}
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimDesc = desc.calculateDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal).rounded(CORNER);
		if (getSkinParam().shadowing(getEntity().getStereotype())) {
			rect.setDeltaShadow(4);
		}

		HColor classBorder = lineConfig.getColors(getSkinParam()).getColor(ColorType.LINE);
		if (classBorder == null) {
			classBorder = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.stateBorder);
		}
		ug = ug.apply(getStroke()).apply(classBorder);
		HColor backcolor = getEntity().getColors(getSkinParam()).getColor(ColorType.BACK);
		if (backcolor == null) {
			backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.stateBackground);
		}
		ug = ug.apply(backcolor.bg());

		ug.draw(rect);

		final double yLine = MARGIN + dimDesc.getHeight() + MARGIN_LINE;
		ug.apply(UTranslate.dy(yLine)).draw(ULine.hline(widthTotal));

		ug = ug.apply(new UStroke());

		if (withSymbol) {
			final double xSymbol = dimTotal.getWidth();
			final double ySymbol = dimTotal.getHeight();
			drawSymbol(ug, xSymbol, ySymbol);
		}

		final double xDesc = (widthTotal - dimDesc.getWidth()) / 2;
		final double yDesc = MARGIN;
		desc.drawU(ug.apply(new UTranslate(xDesc, yDesc)));

		final double xFields = MARGIN;
		final double yFields = yLine + MARGIN_LINE;
		fields.drawU(ug.apply(new UTranslate(xFields, yFields)));

		if (url != null) {
			ug.closeUrl();
		}
		ug.closeGroup();
	}

	private UStroke getStroke() {
		UStroke stroke = lineConfig.getColors(getSkinParam()).getSpecificLineStroke();
		if (stroke == null) {
			stroke = new UStroke(1.5);
		}
		return stroke;
	}

	public static void drawSymbol(UGraphic ug, double xSymbol, double ySymbol) {
		xSymbol -= 4 * smallRadius + smallLine + smallMarginX;
		ySymbol -= 2 * smallRadius + smallMarginY;
		final UEllipse small = new UEllipse(2 * smallRadius, 2 * smallRadius);
		ug.apply(new UTranslate(xSymbol, ySymbol)).draw(small);
		ug.apply(new UTranslate(xSymbol + smallLine + 2 * smallRadius, ySymbol)).draw(small);
		ug.apply(new UTranslate(xSymbol + 2 * smallRadius, ySymbol + smallLine)).draw(ULine.hline(smallLine));
	}

	public ShapeType getShapeType() {
		return ShapeType.ROUND_RECTANGLE;
	}

}
