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
package net.sourceforge.plantuml.flowdiagram;

import static net.sourceforge.plantuml.ugraphic.ImageBuilder.imageBuilder;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.golem.MinMaxGolem;
import net.sourceforge.plantuml.golem.Path;
import net.sourceforge.plantuml.golem.Position;
import net.sourceforge.plantuml.golem.Tile;
import net.sourceforge.plantuml.golem.TileArea;
import net.sourceforge.plantuml.golem.TileGeometry;
import net.sourceforge.plantuml.golem.TilesField;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class FlowDiagram extends UmlDiagram implements TextBlock {

	private static double SINGLE_SIZE_X = 100;
	private static double SINGLE_SIZE_Y = 35;

	private TilesField field;
	private final Map<Tile, ActivityBox> tilesBoxes = new HashMap<Tile, ActivityBox>();
	private Tile lastTile;

	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		throw new UnsupportedOperationException();
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("Flow Diagram");
	}

	public FlowDiagram(ThemeStyle style, UmlSource source) {
		super(style, source, UmlDiagramType.FLOW, null);
	}

	public void lineSimple(TileGeometry orientation, String idDest, String label) {
		final Tile newTile;
		if (field == null) {
			field = new TilesField();
			tilesBoxes.clear();
			newTile = field.getRoot();
		} else {
			newTile = field.createTile(lastTile, orientation);
		}
		final ActivityBox box = new ActivityBox(newTile, idDest, label);
		tilesBoxes.put(newTile, box);
		lastTile = newTile;
		return;
	}

	public void linkSimple(TileGeometry orientation, String idDest) {
		final Tile tile = getTileById(idDest);
		field.addPath(lastTile, tile, orientation);
	}

	private Tile getTileById(String id) {
		for (Map.Entry<Tile, ActivityBox> ent : tilesBoxes.entrySet()) {
			if (ent.getValue().getId().equals(id)) {
				return ent.getKey();
			}
		}
		throw new IllegalArgumentException(id);
	}

	@Override
	public ImageBuilder createImageBuilder(FileFormatOption fileFormatOption) throws IOException {
		return imageBuilder(fileFormatOption)
				.dimension(calculateDimension(fileFormatOption.getDefaultStringBounder(getSkinParam())))
				.margin(getDefaultMargins()).metadata(fileFormatOption.isWithMetadata() ? getMetadata() : null)
				.seed(seed());
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {

		return createImageBuilder(fileFormatOption).drawable(this).write(os);
	}

	public void drawU(UGraphic ug) {
		double x = 0;
		double y = 0;
		final MinMaxGolem minMax = getMinMax();
		x -= minMax.getMinX() * SINGLE_SIZE_X;
		y -= minMax.getMinY() * SINGLE_SIZE_Y;
		final StringBounder stringBounder = ug.getStringBounder();
		for (Map.Entry<Tile, ActivityBox> ent : tilesBoxes.entrySet()) {
			final Tile tile = ent.getKey();
			final Position pos = field.getPosition(tile);
			final int xmin = pos.getXmin();
			final int ymin = pos.getYmin();
			final ActivityBox box = ent.getValue();
			final Dimension2D dimBox = box.calculateDimension(stringBounder);
			final double deltaX = SINGLE_SIZE_X * 2 - dimBox.getWidth();
			final double deltaY = SINGLE_SIZE_Y * 2 - dimBox.getHeight();
			box.drawU(ug.apply(
					new UTranslate((x + xmin * SINGLE_SIZE_X + deltaX / 2), (y + ymin * SINGLE_SIZE_Y + deltaY / 2))));
		}
		ug = ug.apply(HColorUtils.MY_RED);
		ug = ug.apply(HColorUtils.MY_RED.bg());
		final UShape arrow = new UEllipse(7, 7);
		for (Path p : field.getPaths()) {
			final TileArea start = p.getStart();
			final TileArea dest = p.getDest();
			final Point2D pStart = movePoint(getCenter(start), start.getTile(), start.getGeometry(), stringBounder);
			final Point2D pDest = movePoint(getCenter(dest), dest.getTile(), dest.getGeometry(), stringBounder);
			final ULine line = new ULine(pDest.getX() - pStart.getX(), pDest.getY() - pStart.getY());
			ug.apply(new UTranslate(x + pStart.getX(), y + pStart.getY())).draw(line);
			ug.apply(new UTranslate(x + pDest.getX() - 3, y + pDest.getY() - 3)).draw(arrow);

		}
	}

	private Point2D getCenter(TileArea area) {
		final Tile tile = area.getTile();
		final Position position = field.getPosition(tile);
		final double x = position.getCenterX();
		final double y = position.getCenterY();
		return new Point2D.Double(x * SINGLE_SIZE_X, y * SINGLE_SIZE_Y);
	}

	private Point2D movePoint(Point2D pt, Tile tile, TileGeometry tileGeometry, StringBounder stringBounder) {
		final Dimension2D dim = tilesBoxes.get(tile).calculateDimension(stringBounder);
		final double width = dim.getWidth();
		final double height = dim.getHeight();
		double x = pt.getX();
		double y = pt.getY();
		switch (tileGeometry) {
		case SOUTH:
			y += height / 2;
			break;
		case NORTH:
			y -= height / 2;
			break;
		case EAST:
			x += width / 2;
			break;
		case WEST:
			x -= width / 2;
			break;
		default:
			throw new IllegalStateException();
		}
		return new Point2D.Double(x, y);
	}

	private MinMaxGolem getMinMax() {
		final MinMaxGolem minMax = new MinMaxGolem();
		for (Tile tile : tilesBoxes.keySet()) {
			minMax.manage(field.getPosition(tile));
		}
		return minMax;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final MinMaxGolem minMax = getMinMax();
		return new Dimension2DDouble(minMax.getWidth() * SINGLE_SIZE_X, minMax.getHeight() * SINGLE_SIZE_Y);
	}

	public MinMax getMinMax(StringBounder stringBounder) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ClockwiseTopRightBottomLeft getDefaultMargins() {
		return ClockwiseTopRightBottomLeft.same(0);
	}
}
