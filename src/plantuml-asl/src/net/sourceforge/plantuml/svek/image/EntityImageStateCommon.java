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

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineConfigurable;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public abstract class EntityImageStateCommon extends AbstractEntityImage {

	final protected TextBlock desc;
	final protected Url url;

	final protected LineConfigurable lineConfig;

	public EntityImageStateCommon(IEntity entity, ISkinParam skinParam) {
		super(entity, skinParam);

		this.lineConfig = entity;
		final Stereotype stereotype = entity.getStereotype();

		final FontConfiguration fontConfiguration;

		if (UseStyle.useBetaStyle())
			fontConfiguration = getStyleStateHeader().getFontConfiguration(getSkinParam().getThemeStyle(),
					getSkinParam().getIHtmlColorSet());
		else
			fontConfiguration = FontConfiguration.create(getSkinParam(), FontParam.STATE, stereotype);

		this.desc = entity.getDisplay().create8(fontConfiguration, HorizontalAlignment.CENTER, skinParam,
				CreoleMode.FULL, skinParam.wrapWidth());
		this.url = entity.getUrl99();

	}

	private Style getStyleStateHeader() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.stateDiagram, SName.state, SName.header)
				.withTOBECHANGED(getEntity().getStereotype()).getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	final protected Style getStyleState() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.stateDiagram, SName.state)
				.withTOBECHANGED(getEntity().getStereotype()).getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private UStroke getStrokeWIP() {
		UStroke stroke = lineConfig.getColors().getSpecificLineStroke();
		if (stroke == null) {
			stroke = new UStroke(1.5);
		}
		return stroke;
	}

	final public ShapeType getShapeType() {
		return ShapeType.ROUND_RECTANGLE;
	}

	final protected URectangle getShape(final Dimension2D dimTotal) {
		double deltaShadow = 0;
		final double corner;
		if (UseStyle.useBetaStyle()) {
			corner = getStyleState().value(PName.RoundCorner).asDouble();
			deltaShadow = getStyleState().value(PName.Shadowing).asDouble();
		} else {
			corner = CORNER;
			if (getSkinParam().shadowing(getEntity().getStereotype()))
				deltaShadow = 4;
		}

		final URectangle rect = new URectangle(dimTotal).rounded(corner);
		rect.setDeltaShadow(deltaShadow);
		return rect;
	}

	final protected UGraphic applyColorAndStroke(UGraphic ug) {

		HColor border = lineConfig.getColors().getColor(ColorType.LINE);
		if (border == null) {
			if (UseStyle.useBetaStyle())
				border = getStyleState().value(PName.LineColor).asColor(getSkinParam().getThemeStyle(),
						getSkinParam().getIHtmlColorSet());
			else
				border = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.stateBorder);
		}
		if (UseStyle.useBetaStyle() == false)
			ug = ug.apply(getStrokeWIP());
		ug = ug.apply(border);
		HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);
		if (backcolor == null) {
			if (UseStyle.useBetaStyle())
				backcolor = getStyleState().value(PName.BackGroundColor).asColor(getSkinParam().getThemeStyle(),
						getSkinParam().getIHtmlColorSet());
			else
				backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.stateBackground);

		}
		ug = ug.apply(backcolor.bg());

		return ug;
	}

}
