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
package net.sourceforge.plantuml.bpm;

import net.atmp.InnerStrategy;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.MagneticBorder;
import net.sourceforge.plantuml.klimt.geom.MagneticBorderNone;
import net.sourceforge.plantuml.klimt.geom.MinMax;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XRectangle2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.style.ISkinParam;

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

	public XDimension2D getDimension(StringBounder stringBounder, ISkinParam skinParam) {
		return new XDimension2D(20, 20);
	}

	public TextBlock toTextBlock(ISkinParam skinParam) {
		return this;
	}

	public String getId() {
		throw new UnsupportedOperationException();
	}

	public void drawU(UGraphic ug) {
		// System.err.println("DRAWING " + toString());
		ug = ug.apply(HColors.BLUE);
		for (Where w : Where.values()) {
			if (have(w)) {
				drawLine(ug, w);
			}
		}

	}

	private void drawLine(UGraphic ug, Where w) {
		if (w == Where.WEST) {
			ug.apply(UTranslate.dy(10)).draw(ULine.hline(10));
		}
		if (w == Where.EAST) {
			ug.apply(new UTranslate(10, 10)).draw(ULine.hline(10));
		}
		if (w == Where.NORTH) {
			ug.apply(UTranslate.dx(10)).draw(ULine.vline(10));
		}
		if (w == Where.SOUTH) {
			ug.apply(new UTranslate(10, 10)).draw(ULine.vline(10));
		}
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(20, 20);
	}

	public MinMax getMinMax(StringBounder stringBounder) {
		throw new UnsupportedOperationException();
	}

	public XRectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		return null;
	}

	@Override
	public MagneticBorder getMagneticBorder() {
		return new MagneticBorderNone();
	}

	@Override
	public HColor getBackcolor() {
		return null;
	}

}
