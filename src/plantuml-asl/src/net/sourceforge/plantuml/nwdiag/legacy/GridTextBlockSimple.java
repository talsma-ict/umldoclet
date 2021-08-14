/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.nwdiag.legacy;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GridTextBlockSimple implements TextBlock {

	protected final NwArray data;
	protected final ISkinParam skinparam;

	public GridTextBlockSimple(int lines, int cols, ISkinParam skinparam) {
		this.skinparam = skinparam;
		this.data = new NwArray(lines, cols);
	}

	protected void drawGrid(UGraphic ug) {
	}

	public void drawU(UGraphic ug) {
		drawGrid(ug);
		final StringBounder stringBounder = ug.getStringBounder();
		double y = 0;
		for (int i = 0; i < data.getNbLines(); i++) {
			final double lineHeight = lineHeight(stringBounder, i);
			double x = 0;
			for (int j = 0; j < data.getNbCols(); j++) {
				final double colWidth = colWidth(stringBounder, j);
				if (data.get(i, j) != null) {
					data.get(i, j).drawMe(ug.apply(new UTranslate(x, y)), colWidth, lineHeight);
				}
				x += colWidth;
			}
			y += lineHeight;
		}
	}

	protected double colWidth(StringBounder stringBounder, final int j) {
		double width = 0;
		for (int i = 0; i < data.getNbLines(); i++) {
			if (data.get(i, j) != null) {
				width = Math.max(width, data.get(i, j).naturalDimension(stringBounder).getWidth());
			}
		}
		return width;
	}

	public double lineHeight(StringBounder stringBounder, final int i) {
		double height = 50;
		for (int j = 0; j < data.getNbCols(); j++) {
			if (data.get(i, j) != null) {
				height = Math.max(height, data.get(i, j).naturalDimension(stringBounder).getHeight());
			}
		}
		return height;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		if (data.getNbLines() == 0) {
			return new Dimension2DDouble(0, 0);
		}
		double height = 0;
		for (int i = 0; i < data.getNbLines(); i++) {
			height += lineHeight(stringBounder, i);
		}
		double width = 0;
		for (int j = 0; j < data.getNbCols(); j++) {
			width += colWidth(stringBounder, j);
		}
		return new Dimension2DDouble(width, height);
	}

	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		throw new UnsupportedOperationException("member=" + member + " " + getClass().toString());
	}

	public MinMax getMinMax(StringBounder stringBounder) {
		throw new UnsupportedOperationException(getClass().toString());
	}

	public void add(int i, int j, LinkedElement value) {
		data.set(i, j, value);
	}

	public Footprint getFootprint(NwGroupLegacy group) {
		return data.getFootprint(group);
	}

}
