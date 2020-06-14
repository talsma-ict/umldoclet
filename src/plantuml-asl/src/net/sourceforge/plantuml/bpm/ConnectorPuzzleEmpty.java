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
package net.sourceforge.plantuml.bpm;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ConnectorPuzzleEmpty extends AbstractConnectorPuzzle implements Placeable, TextBlock, ConnectorPuzzle {

	public static ConnectorPuzzleEmpty get(String value) {
		final ConnectorPuzzleEmpty result = new ConnectorPuzzleEmpty();
		for (Where w : Where.values()) {
			if (value.contains(w.toShortString())) {
				result.append(w);
			}
		}
		return result;
	}

	public boolean checkDirections(String directions) {
		return connections().equals(get(directions).connections());
	}

	@Override
	public String toString() {
		if (connections().size() == 0) {
			return "NONE";

		}
		return connections().toString();
	}

	public Dimension2D getDimension(StringBounder stringBounder, ISkinParam skinParam) {
		return new Dimension2DDouble(20, 20);
	}

	public TextBlock toTextBlock(ISkinParam skinParam) {
		return this;
	}

	public String getId() {
		throw new UnsupportedOperationException();
	}

	public void drawU(UGraphic ug) {
		// System.err.println("DRAWING " + toString());
		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLUE));
		for (Where w : Where.values()) {
			if (have(w)) {
				drawLine(ug, w);
			}
		}

	}

	private void drawLine(UGraphic ug, Where w) {
		if (w == Where.WEST) {
			ug.apply(new UTranslate(0, 10)).draw(new ULine(10, 0));
		}
		if (w == Where.EAST) {
			ug.apply(new UTranslate(10, 10)).draw(new ULine(10, 0));
		}
		if (w == Where.NORTH) {
			ug.apply(new UTranslate(10, 0)).draw(new ULine(0, 10));
		}
		if (w == Where.SOUTH) {
			ug.apply(new UTranslate(10, 10)).draw(new ULine(0, 10));
		}
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(20, 20);
	}
	
	public MinMax getMinMax(StringBounder stringBounder) {
		throw new UnsupportedOperationException();
	}


	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		return null;
	}

}
