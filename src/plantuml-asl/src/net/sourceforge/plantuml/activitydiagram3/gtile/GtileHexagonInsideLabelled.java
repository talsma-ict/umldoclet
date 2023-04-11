/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.utils.MathUtils;

public class GtileHexagonInsideLabelled extends GtileWithMargin {

	private final TextBlock southLabel;
	private final TextBlock eastLabel;
	private final TextBlock westLabel;

	public GtileHexagonInsideLabelled(GtileHexagonInside diamond, TextBlock southLabel, TextBlock eastLabel,
			TextBlock westLabel) {
		super(diamond, getNorthMargin(diamond, eastLabel, westLabel), getSouthMargin(diamond, southLabel), 0);
		this.eastLabel = eastLabel;
		this.westLabel = westLabel;
		this.southLabel = southLabel;
	}

	public GtileHexagonInsideLabelled withSouthLabel(TextBlock eastLabel) {
		return new GtileHexagonInsideLabelled((GtileHexagonInside) orig, southLabel, eastLabel, westLabel);
	}

	public GtileHexagonInsideLabelled withEastLabel(TextBlock eastLabel) {
		return new GtileHexagonInsideLabelled((GtileHexagonInside) orig, southLabel, eastLabel, westLabel);
	}

	public GtileHexagonInsideLabelled withWestLabel(TextBlock westLabel) {
		return new GtileHexagonInsideLabelled((GtileHexagonInside) orig, southLabel, eastLabel, westLabel);
	}

	private static double getNorthMargin(GtileHexagonInside diamond, TextBlock eastLabel, TextBlock westLabel) {
		final XDimension2D dimWest = westLabel.calculateDimension(diamond.getStringBounder());
		final XDimension2D dimEast = eastLabel.calculateDimension(diamond.getStringBounder());
		final UTranslate east = diamond.getCoord(GPoint.EAST_HOOK);
		final UTranslate west = diamond.getCoord(GPoint.WEST_HOOK);
		return MathUtils.max(0, dimWest.getHeight() - west.getDy(), dimEast.getHeight() - east.getDy());
	}

	private static double getSouthMargin(GtileHexagonInside diamond, TextBlock southLabel) {
		final XDimension2D dimSouth = southLabel.calculateDimension(diamond.getStringBounder());
		return dimSouth.getHeight();
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		super.drawUInternal(ug);

		final UTranslate south = getCoord(GPoint.SOUTH_HOOK);
		southLabel.drawU(ug.apply(south));

		final UTranslate east = getCoord(GPoint.EAST_HOOK);
		eastLabel.drawU(
				ug.apply(east.compose(UTranslate.dy(-eastLabel.calculateDimension(getStringBounder()).getHeight()))));

		final XDimension2D tmp = westLabel.calculateDimension(getStringBounder());
		final UTranslate west = getCoord(GPoint.WEST_HOOK);
		westLabel.drawU(ug.apply(west.compose(new UTranslate(-tmp.getWidth(), -tmp.getHeight()))));

	}

	@Override
	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D tmp = super.calculateDimension(stringBounder);
		final UTranslate south = getCoord(GPoint.SOUTH_HOOK);
		final XDimension2D southCorner = southLabel.calculateDimension(stringBounder).applyTranslate(south);

		final UTranslate east = getCoord(GPoint.EAST_HOOK);
		final XDimension2D eastCorner = eastLabel.calculateDimension(stringBounder).applyTranslate(east);

		return MathUtils.maxDim(tmp, southCorner, eastCorner);
	}

}
