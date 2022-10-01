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

import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class ClusterDecoration {

	private final UStroke defaultStroke;
	final private USymbol symbol;
	final private TextBlock title;
	final private TextBlock stereo;

	final private ClusterPosition clusterPosition;

	public ClusterDecoration(PackageStyle style, USymbol symbol, TextBlock title, TextBlock stereo,
			ClusterPosition clusterPosition, UStroke stroke) {
		this.symbol = guess(symbol, style);
		this.stereo = stereo;
		this.title = title;
		this.clusterPosition = clusterPosition;
		this.defaultStroke = stroke;
	}

	private static USymbol guess(USymbol symbol, PackageStyle style) {
		if (symbol != null)
			return symbol;

		return style.toUSymbol();
	}

	public void drawU(UGraphic ug, HColor backColor, HColor borderColor, double shadowing, double roundCorner,
			HorizontalAlignment titleAlignment, HorizontalAlignment stereoAlignment, double diagonalCorner) {
		final SymbolContext biColor = new SymbolContext(backColor, borderColor);
		if (symbol == null)
			throw new UnsupportedOperationException();

		final SymbolContext symbolContext = biColor.withShadow(shadowing).withStroke(defaultStroke)
				.withCorner(roundCorner, diagonalCorner);
		symbol.asBig(title, titleAlignment, stereo, clusterPosition.getWidth(), clusterPosition.getHeight(),
				symbolContext, stereoAlignment).drawU(ug.apply(clusterPosition.getPosition()));
	}

}
