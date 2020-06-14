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
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.StringBounder;

public class TextLimitFinder implements UGraphic {

	public boolean matchesProperty(String propertyName) {
		return false;
	}
	
	public double dpiFactor() {
		return 1;
	}


	public UGraphic apply(UChange change) {
		if (change instanceof UTranslate) {
			return new TextLimitFinder(stringBounder, minmax, translate.compose((UTranslate) change));
		} else if (change instanceof UStroke) {
			return new TextLimitFinder(this);
		} else if (change instanceof UChangeBackColor) {
			return new TextLimitFinder(this);
		} else if (change instanceof UChangeColor) {
			return new TextLimitFinder(this);
		}
		throw new UnsupportedOperationException();
	}

	private final StringBounder stringBounder;
	private final UTranslate translate;
	private final MinMaxMutable minmax;

	public TextLimitFinder(StringBounder stringBounder, boolean initToZero) {
		this(stringBounder, MinMaxMutable.getEmpty(initToZero), new UTranslate());
	}

	private TextLimitFinder(StringBounder stringBounder, MinMaxMutable minmax, UTranslate translate) {
		this.stringBounder = stringBounder;
		this.minmax = minmax;
		this.translate = translate;
	}

	private TextLimitFinder(TextLimitFinder other) {
		this(other.stringBounder, other.minmax, other.translate);
	}

	public StringBounder getStringBounder() {
		return stringBounder;
	}

	public UParam getParam() {
		return new UParamNull();
	}

	public void draw(UShape shape) {
		if (shape instanceof UText) {
			final double x = translate.getDx();
			final double y = translate.getDy();
			drawText(x, y, (UText) shape);
		}
	}

	public ColorMapper getColorMapper() {
		throw new UnsupportedOperationException();
	}

	public void startUrl(Url url) {
	}

	public void closeAction() {
	}

	private void drawText(double x, double y, UText text) {
		final Dimension2D dim = stringBounder.calculateDimension(text.getFontConfiguration().getFont(), text.getText());
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

	public void flushUg() {
	}

}
