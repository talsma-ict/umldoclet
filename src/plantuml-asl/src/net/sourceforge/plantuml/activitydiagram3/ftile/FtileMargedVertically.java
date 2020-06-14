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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDecorate;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileMargedVertically extends FtileDecorate {

	private final double margin1;
	private final double margin2;

	public FtileMargedVertically(Ftile tile, double margin1, double margin2) {
		super(tile);
		this.margin1 = margin1;
		this.margin2 = margin2;
	}

	public void drawU(UGraphic ug) {
		if (margin1 > 0) {
			ug = ug.apply(new UTranslate(0, margin1));
		}
		ug.draw(getFtileDelegated());
	}

	private FtileGeometry cached;

	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		if (cached == null) {
			this.cached = calculateDimensionSlow(stringBounder);
		}
		return this.cached;
	}

	private FtileGeometry calculateDimensionSlow(StringBounder stringBounder) {
		final FtileGeometry orig = getFtileDelegated().calculateDimension(stringBounder);
		return new FtileGeometry(orig.getWidth(), orig.getHeight() + margin1 + margin2, orig.getLeft(), orig.getInY()
				+ margin1, orig.hasPointOut() ? orig.getOutY() + margin1 : orig.getOutY());
	}

}
