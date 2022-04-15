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
package net.sourceforge.plantuml.ugraphic;

import static net.sourceforge.plantuml.utils.ObjectUtils.instanceOfAny;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class TextLimitFinder extends UGraphicNo {

	private final MinMaxMutable minmax;

	@Override
	public UGraphic apply(UChange change) {
		if (!instanceOfAny(change, UBackground.class, HColor.class, UStroke.class, UTranslate.class))
			throw new UnsupportedOperationException(change.getClass().toString());
		final UTranslate tmp = change instanceof UTranslate ? this.getTranslate().compose((UTranslate) change)
				: this.getTranslate();
		return new TextLimitFinder(this.getStringBounder(), tmp, this.minmax);
	}

	public static TextLimitFinder create(StringBounder stringBounder, boolean initToZero) {
		return new TextLimitFinder(stringBounder, new UTranslate(), MinMaxMutable.getEmpty(initToZero));
	}

	private TextLimitFinder(StringBounder stringBounder, UTranslate translate, MinMaxMutable minmax) {
		super(stringBounder, translate);
		this.minmax = minmax;
	}

	public void draw(UShape shape) {
		if (shape instanceof UText) {
			final double x = getTranslate().getDx();
			final double y = getTranslate().getDy();
			drawText(x, y, (UText) shape);
		}
	}

	private void drawText(double x, double y, UText text) {
		final Dimension2D dim = getStringBounder().calculateDimension(text.getFontConfiguration().getFont(),
				text.getText());
		y -= dim.getHeight() - 1.5;
		minmax.addPoint(x, y);
		minmax.addPoint(x, y + dim.getHeight());
		minmax.addPoint(x + dim.getWidth(), y);
		minmax.addPoint(x + dim.getWidth(), y + dim.getHeight());
	}

	public double getMaxX() {
		return minmax.getMaxX();
	}

	public double getMaxY() {
		return minmax.getMaxY();
	}

	public double getMinX() {
		return minmax.getMinX();
	}

	public double getMinY() {
		return minmax.getMinY();
	}

}
