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

import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ClusterDecoration {

	private final UStroke defaultStroke;// = new UStroke(2);
	final private USymbol symbol;
	final private TextBlock title;
	final private TextBlock stereo;

	final private double minX;
	final private double minY;
	final private double maxX;
	final private double maxY;

	public ClusterDecoration(PackageStyle style, USymbol symbol, TextBlock title, TextBlock stereo, double minX,
			double minY, double maxX, double maxY, UStroke stroke) {
		this.symbol = guess(symbol, style);
		this.stereo = stereo;
		this.title = title;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.defaultStroke = stroke;
	}

	private static USymbol guess(USymbol symbol, PackageStyle style) {
		if (symbol != null) {
			return symbol;
		}
		return style.toUSymbol();
	}

	public final static int marginTitleX1 = 3;
	public final static int marginTitleX2 = 3;
	public final static int marginTitleX3 = 7;
	public final static int marginTitleY0 = 0;
	public final static int marginTitleY1 = 3;
	public final static int marginTitleY2 = 3;

	public void drawU(UGraphic ug, HtmlColor backColor, HtmlColor borderColor, boolean shadowing, double roundCorner,
			HorizontalAlignment titleAlignment, HorizontalAlignment stereoAlignment) {
		final SymbolContext biColor = new SymbolContext(backColor, borderColor);
		if (symbol == null) {
			throw new UnsupportedOperationException();
		}
		final SymbolContext symbolContext = biColor.withShadow(shadowing).withStroke(defaultStroke)
				.withCorner(roundCorner, 0);
		symbol.asBig(title, titleAlignment, stereo, maxX - minX, maxY - minY, symbolContext, stereoAlignment).drawU(
				ug.apply(new UTranslate(minX, minY)));
	}

}
