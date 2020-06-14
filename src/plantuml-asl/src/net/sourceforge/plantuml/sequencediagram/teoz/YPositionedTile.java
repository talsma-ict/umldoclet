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
package net.sourceforge.plantuml.sequencediagram.teoz;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class YPositionedTile {

	private final Tile tile;
	private final double y;

	public boolean inArea(double ymin, double ymax) {
		return y >= ymin && y < ymax;
	}

	public YPositionedTile(Tile tile, double y) {
		this.tile = tile;
		this.y = y;
		if (tile instanceof TileWithCallbackY) {
			((TileWithCallbackY) tile).callbackY(y);
		}
	}

	@Override
	public String toString() {
		return "y=" + y + " " + tile;
	}

	public void drawInArea(UGraphic ug) {
		// System.err.println("YPositionedTile::drawU y=" + y + " " + tile);
		ug.apply(new UTranslate(0, y)).draw(tile);
	}

	public boolean matchAnchor(String anchor) {
		return tile.matchAnchor(anchor);
	}

	public final double getY(StringBounder stringBounder) {
		final TileWithUpdateStairs communicationTile = (TileWithUpdateStairs) tile;
		return y + communicationTile.getYPoint(stringBounder);
	}

	public double getMiddleX(StringBounder stringBounder) {
		final double max = tile.getMaxX(stringBounder).getCurrentValue();
		final double min = tile.getMinX(stringBounder).getCurrentValue();
		return (min + max) / 2;
	}

}
