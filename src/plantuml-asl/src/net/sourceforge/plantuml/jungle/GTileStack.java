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
package net.sourceforge.plantuml.jungle;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GTileStack extends AbstractTextBlock implements GTile {

	private final List<GTile> tiles;
	private final double space;

	public GTileStack(List<GTile> tiles, double space) {
		this.tiles = tiles;
		this.space = space;
		if (tiles.size() == 0) {
			throw new IllegalArgumentException();
		}
	}

	public void drawU(UGraphic ug) {
		for (GTile tile : tiles) {
			tile.drawU(ug);
			final Dimension2D dim = tile.calculateDimension(ug.getStringBounder());
			ug = ug.apply(new UTranslate(0, dim.getHeight() + space));
		}
	}

	public GTileGeometry calculateDimension(StringBounder stringBounder) {
		double width = 0;
		double height = 0;
		double delta = 0;
		final List<Double> wests = new ArrayList<Double>();
		for (GTile tile : tiles) {
			final GTileGeometry dim = tile.calculateDimension(stringBounder);
			wests.add(delta + dim.getWestPositions().get(0));
			height += dim.getHeight();
			delta += dim.getHeight() + space;
			width = Math.max(width, dim.getWidth());
		}
		height += (tiles.size() - 1) * space;
		return new GTileGeometry(width, height, wests);
	}

}
