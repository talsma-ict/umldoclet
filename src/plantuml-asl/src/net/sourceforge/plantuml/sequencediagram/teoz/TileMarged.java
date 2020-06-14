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
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TileMarged extends AbstractTile implements Tile {

	private final Tile tile;
	private final double x1;
	private final double x2;
	private final double y1;
	private final double y2;

	public TileMarged(Tile tile, double x1, double x2, double y1, double y2) {
		this.tile = tile;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public void drawU(UGraphic ug) {
		tile.drawU(ug.apply(new UTranslate(x1, y1)));

	}

	public double getPreferredHeight(StringBounder stringBounder) {
		return tile.getPreferredHeight(stringBounder) + y1 + y2;
	}

	public void addConstraints(StringBounder stringBounder) {
		tile.addConstraints(stringBounder);
	}

	public Real getMinX(StringBounder stringBounder) {
		return tile.getMinX(stringBounder);
	}

	public Real getMaxX(StringBounder stringBounder) {
		return tile.getMaxX(stringBounder).addFixed(x1 + x2);
	}

	public Event getEvent() {
		return tile.getEvent();
	}

}
