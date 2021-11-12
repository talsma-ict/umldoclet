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
package net.sourceforge.plantuml.ugraphic;

import static net.sourceforge.plantuml.utils.ObjectUtils.instanceOfAny;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class TextLimitFinder extends UGraphicNo {

	@Override
	public UGraphic apply(UChange change) {
		return new TextLimitFinder(this, change);
	}

	private final MinMaxMutable minmax;

	public TextLimitFinder(StringBounder stringBounder, boolean initToZero) {
		super(stringBounder);
		this.minmax = MinMaxMutable.getEmpty(initToZero);
	}

	private TextLimitFinder(TextLimitFinder other, UChange change) {
		super(other, change);
		if (!instanceOfAny(change,
				UBackground.class,
				HColor.class,
				UStroke.class,
				UTranslate.class
		)) {
			throw new UnsupportedOperationException(change.getClass().toString());
		}
		this.minmax = other.minmax;
	}

	public void draw(UShape shape) {
		if (shape instanceof UText) {
			final double x = getTranslate().getDx();
			final double y = getTranslate().getDy();
			drawText(x, y, (UText) shape);
		}
	}

	private void drawText(double x, double y, UText text) {
		final Dimension2D dim = getStringBounder().calculateDimension(text.getFontConfiguration().getFont(), text.getText());
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
