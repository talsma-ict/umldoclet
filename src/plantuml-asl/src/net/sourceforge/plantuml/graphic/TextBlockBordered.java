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
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TextBlockBordered extends AbstractTextBlock implements TextBlock {

	private final double cornersize;
	private final HtmlColor backgroundColor;
	private final HtmlColor borderColor;
	private final double marginX;
	private final double marginY;
	private final UStroke stroke;
	private final boolean withShadow;

	private final TextBlock textBlock;

	TextBlockBordered(TextBlock textBlock, UStroke stroke, HtmlColor borderColor, HtmlColor backgroundColor,
			double cornersize, double marginX, double marginY) {
		this.marginX = marginX;
		this.marginY = marginY;
		this.cornersize = cornersize;
		this.textBlock = textBlock;
		this.withShadow = false;
		this.stroke = stroke;
		this.borderColor = borderColor;
		this.backgroundColor = backgroundColor;
	}

	TextBlockBordered(TextBlock textBlock, UStroke stroke, HtmlColor borderColor, HtmlColor backgroundColor,
			double cornersize) {
		this(textBlock, stroke, borderColor, backgroundColor, cornersize, 6, 5);
	}

	private double getTextHeight(StringBounder stringBounder) {
		final Dimension2D size = textBlock.calculateDimension(stringBounder);
		return size.getHeight() + 2 * marginY;
	}

	private double getPureTextWidth(StringBounder stringBounder) {
		final Dimension2D size = textBlock.calculateDimension(stringBounder);
		return size.getWidth();
	}

	private double getTextWidth(StringBounder stringBounder) {
		return getPureTextWidth(stringBounder) + 2 * marginX;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final double height = getTextHeight(stringBounder);
		final double width = getTextWidth(stringBounder);
		return new Dimension2DDouble(width + 1, height + 1);
	}

	private UGraphic applyStroke(UGraphic ug) {
		if (stroke == null) {
			return ug;
		}
		return ug.apply(stroke);
	}

	private boolean noBorder() {
		if (stroke == null) {
			return false;
		}
		return stroke.getThickness() == 0;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Shadowable polygon = getPolygonNormal(stringBounder);
		final UGraphic ugOriginal = ug;
		if (withShadow) {
			polygon.setDeltaShadow(4);
		}
		if (noBorder()) {
			ug = ug.apply(new UChangeBackColor(backgroundColor)).apply(new UChangeColor(backgroundColor));
		} else {
			ug = ug.apply(new UChangeBackColor(backgroundColor)).apply(new UChangeColor(borderColor));
			ug = applyStroke(ug);
		}
		ug.draw(polygon);
		textBlock.drawU(ugOriginal.apply(new UTranslate(marginX, marginY)));
	}

	private Shadowable getPolygonNormal(final StringBounder stringBounder) {
		final double height = getTextHeight(stringBounder);
		final double width = getTextWidth(stringBounder);
		return new URectangle(width, height, cornersize, cornersize);
	}

}
