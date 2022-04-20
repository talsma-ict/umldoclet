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

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.cucadiagram.BodyFactory;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.USymbols;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageState2 extends AbstractEntityImage {

	final private Url url;
	private final SName sname;

	private final TextBlock asSmall;

	public EntityImageState2(ILeaf entity, ISkinParam skinParam, SName sname) {
		super(entity, skinParam);
		this.sname = sname;
		final Stereotype stereotype = entity.getStereotype();

		final USymbol symbol = USymbols.FRAME;
		final SymbolContext ctx;

		if (UseStyle.useBetaStyle()) {
			ctx = getStyle().getSymbolContext(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());
		} else {
			HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);
			if (backcolor == null)
				backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), symbol.getColorParamBack());

			final HColor forecolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), symbol.getColorParamBorder());

			ctx = new SymbolContext(backcolor, forecolor).withStroke(new UStroke(1.5))
					.withShadow(getSkinParam().shadowing(getEntity().getStereotype()) ? 3 : 0);
		}

		this.url = entity.getUrl99();
		TextBlock stereo = TextBlockUtils.empty(0, 0);

		final TextBlock desc = BodyFactory.create2(skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER),
				entity.getDisplay(), symbol.getFontParam(), skinParam, stereotype, entity, getStyle());

		asSmall = symbol.asSmall(null, desc, stereo, ctx, skinParam.getStereotypeAlignment());

	}

	private Style getStyle() {
		return getStyleSignature().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, sname, SName.state);
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return asSmall.calculateDimension(stringBounder);
	}

	final public void drawU(UGraphic ug) {
		if (url != null)
			ug.startUrl(url);

		asSmall.drawU(ug);

		if (url != null)
			ug.closeUrl();

	}

}
