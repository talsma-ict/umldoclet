/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.svek;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockWidth;
import net.sourceforge.plantuml.svek.image.EntityImageState;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public final class InnerStateAutonom extends AbstractTextBlock implements IEntityImage {

	private final IEntityImage im;
	private final TextBlock title;
	private final TextBlockWidth attribute;
	private final HtmlColor borderColor;
	private final HtmlColor backColor;
	private final boolean shadowing;
	private final Url url;
	private final boolean withSymbol;
	private final UStroke stroke;

	public InnerStateAutonom(final IEntityImage im, final TextBlock title, TextBlockWidth attribute,
			HtmlColor borderColor, HtmlColor backColor, boolean shadowing, Url url, boolean withSymbol, UStroke stroke) {
		this.im = im;
		this.withSymbol = withSymbol;
		this.title = title;
		this.borderColor = borderColor;
		this.backColor = backColor;
		this.shadowing = shadowing;
		this.attribute = attribute;
		this.url = url;
		this.stroke = stroke;
	}

	public void drawU(UGraphic ug) {
		final Dimension2D text = title.calculateDimension(ug.getStringBounder());
		final Dimension2D attr = attribute.calculateDimension(ug.getStringBounder());
		final Dimension2D total = calculateDimension(ug.getStringBounder());
		final double marginForFields = attr.getHeight() > 0 ? IEntityImage.MARGIN : 0;

		final double titreHeight = IEntityImage.MARGIN + text.getHeight() + IEntityImage.MARGIN_LINE;
		final RoundedContainer r = new RoundedContainer(total, titreHeight, attr.getHeight() + marginForFields,
				borderColor, backColor, im.getBackcolor(), stroke);

		if (url != null) {
			ug.startUrl(url);
		}

		r.drawU(ug, shadowing);
		title.drawU(ug.apply(new UTranslate((total.getWidth() - text.getWidth()) / 2, IEntityImage.MARGIN)));
		attribute.asTextBlock(total.getWidth()).drawU(
				ug.apply(new UTranslate(0 + IEntityImage.MARGIN, IEntityImage.MARGIN + text.getHeight()
						+ IEntityImage.MARGIN)));

		final double spaceYforURL = getSpaceYforURL(ug.getStringBounder());
		im.drawU(ug.apply(new UTranslate(IEntityImage.MARGIN, spaceYforURL)));

		if (withSymbol) {
			EntityImageState.drawSymbol(ug.apply(new UChangeColor(borderColor)), total.getWidth(), total.getHeight());

		}

		if (url != null) {
			ug.closeAction();
		}
	}

	private double getSpaceYforURL(StringBounder stringBounder) {
		final Dimension2D text = title.calculateDimension(stringBounder);
		final Dimension2D attr = attribute.calculateDimension(stringBounder);
		final double marginForFields = attr.getHeight() > 0 ? IEntityImage.MARGIN : 0;
		final double titreHeight = IEntityImage.MARGIN + text.getHeight() + IEntityImage.MARGIN_LINE;
		final double suppY = titreHeight + marginForFields + attr.getHeight();
		return suppY + IEntityImage.MARGIN_LINE;
	}

	public HtmlColor getBackcolor() {
		return null;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D img = im.calculateDimension(stringBounder);
		final Dimension2D text = title.calculateDimension(stringBounder);
		final Dimension2D attr = attribute.calculateDimension(stringBounder);

		final Dimension2D dim = Dimension2DDouble.mergeTB(text, attr, img);
		final double marginForFields = attr.getHeight() > 0 ? IEntityImage.MARGIN : 0;

		final Dimension2D result = Dimension2DDouble.delta(dim, IEntityImage.MARGIN * 2 + 2 * IEntityImage.MARGIN_LINE
				+ marginForFields);

		return result;
	}

	public ShapeType getShapeType() {
		return ShapeType.ROUND_RECTANGLE;
	}

	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}

	public boolean isHidden() {
		return im.isHidden();
	}
	
	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}


}
