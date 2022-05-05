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
package net.sourceforge.plantuml.svek;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.image.EntityImageState;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public final class InnerStateAutonom extends AbstractTextBlock implements IEntityImage {

	private final IEntityImage im;
	private final TextBlock title;
	private final TextBlock attribute;
	private final HColor borderColor;
	private final HColor backColor;
	private final Url url;
	private final boolean withSymbol;
	private final UStroke stroke;
	private final double rounded;
	private final double shadowing;

	public InnerStateAutonom(IEntityImage im, TextBlock title, TextBlock attribute, HColor borderColor,
			HColor backColor, Url url, boolean withSymbol, UStroke stroke, double rounded, double shadowing) {
		this.im = im;
		this.withSymbol = withSymbol;
		this.title = title;
		this.borderColor = borderColor;
		this.backColor = backColor;
		this.shadowing = shadowing;
		this.attribute = attribute;
		this.url = url;
		this.stroke = stroke;
		this.rounded = rounded;
	}

	public void drawU(UGraphic ug) {
		final Dimension2D text = title.calculateDimension(ug.getStringBounder());
		final Dimension2D attr = attribute.calculateDimension(ug.getStringBounder());
		final Dimension2D total = calculateDimension(ug.getStringBounder());
		final double marginForFields = attr.getHeight() > 0 ? IEntityImage.MARGIN : 0;

		final double titreHeight = IEntityImage.MARGIN + text.getHeight() + IEntityImage.MARGIN_LINE;
		final RoundedContainer r = new RoundedContainer(total, titreHeight, attr.getHeight() + marginForFields,
				borderColor, backColor, im.getBackcolor(), stroke, rounded, shadowing);

		if (url != null)
			ug.startUrl(url);

		r.drawU(ug);
		title.drawU(ug.apply(new UTranslate((total.getWidth() - text.getWidth()) / 2, IEntityImage.MARGIN)));
		attribute.drawU(ug.apply(
				new UTranslate(0 + IEntityImage.MARGIN, IEntityImage.MARGIN + text.getHeight() + IEntityImage.MARGIN)));

		final double spaceYforURL = getSpaceYforURL(ug.getStringBounder());
		im.drawU(ug.apply(new UTranslate(IEntityImage.MARGIN, spaceYforURL)));

		if (withSymbol)
			EntityImageState.drawSymbol(ug.apply(borderColor), total.getWidth(), total.getHeight());

		if (url != null)
			ug.closeUrl();

	}

	private double getSpaceYforURL(StringBounder stringBounder) {
		final Dimension2D text = title.calculateDimension(stringBounder);
		final Dimension2D attr = attribute.calculateDimension(stringBounder);
		final double marginForFields = attr.getHeight() > 0 ? IEntityImage.MARGIN : 0;
		final double titreHeight = IEntityImage.MARGIN + text.getHeight() + IEntityImage.MARGIN_LINE;
		final double suppY = titreHeight + marginForFields + attr.getHeight();
		return suppY + IEntityImage.MARGIN_LINE;
	}

	public HColor getBackcolor() {
		return null;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D img = im.calculateDimension(stringBounder);
		final Dimension2D text = title.calculateDimension(stringBounder);
		final Dimension2D attr = attribute.calculateDimension(stringBounder);

		final Dimension2D dim = Dimension2DDouble.mergeTB(text, attr, img);
		final double marginForFields = attr.getHeight() > 0 ? IEntityImage.MARGIN : 0;

		final Dimension2D result = Dimension2DDouble.delta(dim,
				IEntityImage.MARGIN * 2 + 2 * IEntityImage.MARGIN_LINE + marginForFields);

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
