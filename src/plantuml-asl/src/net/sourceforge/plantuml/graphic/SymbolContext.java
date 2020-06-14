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

import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class SymbolContext {

	private final HtmlColor backColor;
	private final HtmlColor foreColor;
	private final UStroke stroke;
	private final boolean shadowing;
	private final double deltaShadow;
	private final double roundCorner;
	private final double diagonalCorner;

	private SymbolContext(HtmlColor backColor, HtmlColor foreColor, UStroke stroke, boolean shadowing,
			double deltaShadow, double roundCorner, double diagonalCorner) {
		this.backColor = backColor;
		this.foreColor = foreColor;
		this.stroke = stroke;
		this.shadowing = shadowing;
		this.deltaShadow = deltaShadow;
		this.roundCorner = roundCorner;
		this.diagonalCorner = diagonalCorner;
	}

	@Override
	public String toString() {
		return super.toString() + " backColor=" + backColor + " foreColor=" + foreColor;
	}

	final public UGraphic apply(UGraphic ug) {
		ug = applyColors(ug);
		ug = applyStroke(ug);
		return ug;
	}

	public UGraphic applyColors(UGraphic ug) {
		ug = ug.apply(new UChangeColor(foreColor));
		ug = ug.apply(new UChangeBackColor(backColor));
		return ug;
	}

	public UGraphic applyStroke(UGraphic ug) {
		return ug.apply(stroke);
	}

	public SymbolContext transparentBackColorToNull() {
		if (backColor instanceof HtmlColorTransparent) {
			return new SymbolContext(null, foreColor, stroke, shadowing, deltaShadow, roundCorner, diagonalCorner);
		}
		return this;
	}

	public SymbolContext(HtmlColor backColor, HtmlColor foreColor) {
		this(backColor, foreColor, new UStroke(), false, 0, 0, 0);
	}

	public SymbolContext withShadow(boolean newShadow) {
		return new SymbolContext(backColor, foreColor, stroke, newShadow, deltaShadow, roundCorner, diagonalCorner);
	}

	public SymbolContext withDeltaShadow(double deltaShadow) {
		return new SymbolContext(backColor, foreColor, stroke, deltaShadow != 0, deltaShadow, roundCorner,
				diagonalCorner);
	}

	public SymbolContext withStroke(UStroke newStroke) {
		return new SymbolContext(backColor, foreColor, newStroke, shadowing, deltaShadow, roundCorner, diagonalCorner);
	}

	public SymbolContext withBackColor(HtmlColor backColor) {
		return new SymbolContext(backColor, foreColor, stroke, shadowing, deltaShadow, roundCorner, diagonalCorner);
	}

	public SymbolContext withForeColor(HtmlColor foreColor) {
		return new SymbolContext(backColor, foreColor, stroke, shadowing, deltaShadow, roundCorner, diagonalCorner);
	}

	public SymbolContext withCorner(double roundCorner, double diagonalCorner) {
		return new SymbolContext(backColor, foreColor, stroke, shadowing, deltaShadow, roundCorner, diagonalCorner);
	}

	public HtmlColor getBackColor() {
		return backColor;
	}

	public HtmlColor getForeColor() {
		return foreColor;
	}

	public UStroke getStroke() {
		return stroke;
	}

	public boolean isShadowing() {
		return shadowing || deltaShadow > 0;
	}

	public double getDeltaShadow() {
		return deltaShadow;
	}

	public double getRoundCorner() {
		return roundCorner;
	}

	public double getDiagonalCorner() {
		return diagonalCorner;
	}

}
