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
package net.sourceforge.plantuml.salt.element;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.salt.Cell;
import net.sourceforge.plantuml.salt.Positionner2;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ElementPyramid extends AbstractElement {

	private int rows;
	private int cols;
	private final TextBlock title;
	private final TableStrategy tableStrategy;
	private final Map<Element, Cell> positions1;
	private final Map<Cell, Element> positions2 = new HashMap<Cell, Element>();

	private double rowsStart[];
	private double colsStart[];

	public ElementPyramid(Positionner2 positionner, TableStrategy tableStrategy, String title,
			ISkinSimple spriteContainer) {
		positions1 = positionner.getAll();
		for (Map.Entry<Element, Cell> ent : positions1.entrySet()) {
			positions2.put(ent.getValue(), ent.getKey());
		}

		if (title != null) {
			final FontConfiguration fontConfiguration = FontConfiguration.blackBlueTrue(UFont.byDefault(10));
			this.title = Display.getWithNewlines(title).create(fontConfiguration, HorizontalAlignment.LEFT,
					spriteContainer);
		} else {
			this.title = TextBlockUtils.empty(0, 0);
		}
		this.rows = positionner.getNbRows();
		this.cols = positionner.getNbCols();
		this.tableStrategy = tableStrategy;

		for (Cell c : positions1.values()) {
			rows = Math.max(rows, c.getMaxRow());
			cols = Math.max(cols, c.getMaxCol());
		}

	}

	public Dimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		init(stringBounder);
		return new Dimension2DDouble(colsStart[colsStart.length - 1], rowsStart[rowsStart.length - 1]
				+ title.calculateDimension(stringBounder).getHeight());
	}

	public void drawU(UGraphic ug, int zIndex, Dimension2D dimToUse) {
		init(ug.getStringBounder());
		final double titleHeight = title.calculateDimension(ug.getStringBounder()).getHeight();
		final Grid grid = new Grid(rowsStart, colsStart, tableStrategy, title);
		for (Map.Entry<Element, Cell> ent : positions1.entrySet()) {
			final Element elt = ent.getKey();
			final Cell cell = ent.getValue();
			final double xcell = colsStart[cell.getMinCol()];
			final double supY = cell.getMinRow() == 0 ? titleHeight / 2 : 0;
			final double ycell = rowsStart[cell.getMinRow()] + supY;
			final double width = colsStart[cell.getMaxCol() + 1] - colsStart[cell.getMinCol()] - 1;
			final double height = rowsStart[cell.getMaxRow() + 1] - rowsStart[cell.getMinRow()] - 1;
			grid.addCell(cell);
			elt.drawU(ug.apply(new UTranslate(xcell + 1, ycell + 1)), zIndex, new Dimension2DDouble(width, height));
		}
		if (zIndex == 0) {
			grid.drawU(ug, 0, 0);
		}
	}

	private void init(StringBounder stringBounder) {
		if (rowsStart != null) {
			return;
		}
		final double titleHeight = title.calculateDimension(stringBounder).getHeight();
		rowsStart = new double[rows + 1];
		rowsStart[0] = titleHeight / 2;
		for (int i = 1; i < rows + 1; i++) {
			rowsStart[i] = titleHeight / 2;
		}
		colsStart = new double[cols + 1];
		final List<Cell> all = new ArrayList<>(positions1.values());
		Collections.sort(all, new LeftFirst());
		for (Cell cell : all) {
			final Element elt = positions2.get(cell);
			final Dimension2D dim = elt.getPreferredDimension(stringBounder, 0, 0);
			ensureColWidth(cell.getMinCol(), cell.getMaxCol() + 1, dim.getWidth() + 2);
		}
		Collections.sort(all, new TopFirst());
		for (Cell cell : all) {
			final Element elt = positions2.get(cell);
			final double supY = cell.getMinRow() == 0 ? titleHeight / 2 : 0;
			final Dimension2D dim = elt.getPreferredDimension(stringBounder, 0, 0);
			ensureRowHeight(cell.getMinRow(), cell.getMaxRow() + 1, dim.getHeight() + supY + 2);
		}
	}

	private void ensureColWidth(int first, int last, double width) {
		final double actual = colsStart[last] - colsStart[first];
		final double missing = width - actual;
		if (missing > 0) {
			for (int i = last; i < colsStart.length; i++) {
				colsStart[i] += missing;
			}
		}
	}

	private void ensureRowHeight(int first, int last, double height) {
		final double actual = rowsStart[last] - rowsStart[first];
		final double missing = height - actual;
		if (missing > 0) {
			for (int i = last; i < rowsStart.length; i++) {
				rowsStart[i] += missing;
			}
		}
	}

	public final int getNbRows() {
		return rows + 1;
	}

	public final int getNbCols() {
		return cols + 1;
	}

}
