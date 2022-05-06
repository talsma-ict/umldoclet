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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.utils.MathUtils;

public class Opale extends AbstractTextBlock implements TextBlock {

	private static final int cornersize = 10;
	private final HColor noteBackgroundColor;
	private final HColor borderColor;
	private final int marginX1 = 6;
	private final int marginX2 = 15;
	private final int marginY = 5;
	private final double shadowing2;
	private Direction strategy;
	private Point2D pp1;
	private Point2D pp2;
	private final boolean withLink;
	private double roundCorner;
	private final UStroke stroke;

	private final TextBlock textBlock;

	public Opale(double shadowing, HColor borderColor, HColor noteBackgroundColor, TextBlock textBlock,
			boolean withLink, UStroke stroke) {
		this.noteBackgroundColor = noteBackgroundColor;
		this.withLink = withLink;
		this.shadowing2 = shadowing;
		this.borderColor = borderColor;
		this.textBlock = textBlock;
		this.stroke = stroke;
	}

	public void setRoundCorner(double roundCorner) {
		this.roundCorner = roundCorner;
	}

	public void setOpale(Direction strategy, Point2D pp1, Point2D pp2) {
		this.strategy = strategy;
		this.pp1 = pp1;
		this.pp2 = pp2;
	}

	final private double getWidth(StringBounder stringBounder) {
		return textBlock.calculateDimension(stringBounder).getWidth() + marginX1 + marginX2;
	}

	final private double getHeight(StringBounder stringBounder) {
		final Dimension2D size = textBlock.calculateDimension(stringBounder);
		return size.getHeight() + 2 * marginY;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final double height = getHeight(stringBounder);
		final double width = getWidth(stringBounder);
		return new Dimension2DDouble(width, height);
	}

	final public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug = ug.apply(noteBackgroundColor.bg()).apply(borderColor);
		final UPath polygon;
		if (withLink == false) {
			polygon = getPolygonNormal(stringBounder);
		} else if (strategy == Direction.LEFT) {
			polygon = getPolygonLeft(stringBounder, pp1, pp2);
		} else if (strategy == Direction.RIGHT) {
			polygon = getPolygonRight(stringBounder, pp1, pp2);
		} else if (strategy == Direction.UP) {
			polygon = getPolygonUp(stringBounder, pp1, pp2);
		} else if (strategy == Direction.DOWN) {
			polygon = getPolygonDown(stringBounder, pp1, pp2);
		} else {
			throw new IllegalArgumentException();
		}
		polygon.setDeltaShadow(shadowing2);
		if (stroke != null)
			ug = ug.apply(stroke);
		ug.draw(polygon);
		ug.draw(getCorner(getWidth(stringBounder), roundCorner));
		textBlock.drawU(ug.apply(new UTranslate(marginX1, marginY)));
	}

	private UPath getPolygonNormal(final StringBounder stringBounder) {
		return getPolygonNormal(getWidth(stringBounder), getHeight(stringBounder), roundCorner);
	}

	public static UPath getCorner(double width, double roundCorner) {
		final UPath path = new UPath();
		path.moveTo(width - cornersize, 0);
		if (roundCorner == 0) {
			path.lineTo(width - cornersize, cornersize);
		} else {
			path.lineTo(width - cornersize, cornersize - roundCorner / 4);
			path.arcTo(new Point2D.Double(width - cornersize + roundCorner / 4, cornersize), roundCorner / 4, 0, 0);
		}
		path.lineTo(width, cornersize);
		path.lineTo(width - cornersize, 0);
		path.closePath();
		return path;
	}

	public static UPath getPolygonNormal(double width, double height, double roundCorner) {
		final UPath polygon = new UPath();
		if (roundCorner == 0) {
			polygon.moveTo(0, 0);
			polygon.lineTo(0, height);
			polygon.lineTo(width, height);
			polygon.lineTo(width, cornersize);
			polygon.lineTo(width - cornersize, 0);
			polygon.lineTo(0, 0);
		} else {
			polygon.moveTo(0, roundCorner / 2);
			polygon.lineTo(0, height - roundCorner / 2);
			polygon.arcTo(new Point2D.Double(roundCorner / 2, height), roundCorner / 2, 0, 0);
			polygon.lineTo(width - roundCorner / 2, height);
			polygon.arcTo(new Point2D.Double(width, height - roundCorner / 2), roundCorner / 2, 0, 0);
			polygon.lineTo(width, cornersize);
			polygon.lineTo(width - cornersize, 0);
			polygon.lineTo(roundCorner / 2, 0);
			polygon.arcTo(new Point2D.Double(0, roundCorner / 2), roundCorner / 2, 0, 0);
		}
		polygon.closePath();
		return polygon;
	}

	private final double delta = 4;

	private UPath getPolygonLeft(final StringBounder stringBounder, final Point2D pp1, final Point2D pp2) {
		final UPath polygon = new UPath();
		polygon.moveTo(0, roundCorner / 2);

		double y1 = pp1.getY() - delta;
		y1 = MathUtils.limitation(y1, 0, getHeight(stringBounder) - 2 * delta);
		polygon.lineTo(0, y1);
		polygon.lineTo(pp2.getX(), pp2.getY());
		polygon.lineTo(0, y1 + 2 * delta);

		polygon.lineTo(0, getHeight(stringBounder) - roundCorner / 2);
		polygon.arcTo(new Point2D.Double(roundCorner / 2, getHeight(stringBounder)), roundCorner / 2, 0, 0);
		polygon.lineTo(getWidth(stringBounder) - roundCorner / 2, getHeight(stringBounder));
		polygon.arcTo(new Point2D.Double(getWidth(stringBounder), getHeight(stringBounder) - roundCorner / 2),
				roundCorner / 2, 0, 0);
		polygon.lineTo(getWidth(stringBounder), cornersize);
		polygon.lineTo(getWidth(stringBounder) - cornersize, 0);
		polygon.lineTo(roundCorner / 2, 0);
		polygon.arcTo(new Point2D.Double(0, roundCorner / 2), roundCorner / 2, 0, 0);
		polygon.closePath();
		return polygon;
	}

	private UPath getPolygonRight(final StringBounder stringBounder, final Point2D pp1, final Point2D pp2) {
		final UPath polygon = new UPath();
		polygon.moveTo(0, roundCorner / 2);
		polygon.lineTo(0, getHeight(stringBounder) - roundCorner / 2);
		polygon.arcTo(new Point2D.Double(roundCorner / 2, getHeight(stringBounder)), roundCorner / 2, 0, 0);
		polygon.lineTo(getWidth(stringBounder) - roundCorner / 2, getHeight(stringBounder));
		polygon.arcTo(new Point2D.Double(getWidth(stringBounder), getHeight(stringBounder) - roundCorner / 2),
				roundCorner / 2, 0, 0);

		double y1 = pp1.getY() - delta;
		y1 = MathUtils.limitation(y1, cornersize, getHeight(stringBounder) - 2 * delta);
		polygon.lineTo(getWidth(stringBounder), y1 + 2 * delta);
		polygon.lineTo(pp2.getX(), pp2.getY());
		polygon.lineTo(getWidth(stringBounder), y1);

		polygon.lineTo(getWidth(stringBounder), cornersize);
		polygon.lineTo(getWidth(stringBounder) - cornersize, 0);
		polygon.lineTo(roundCorner / 2, 0);
		polygon.arcTo(new Point2D.Double(0, roundCorner / 2), roundCorner / 2, 0, 0);
		polygon.closePath();
		return polygon;
	}

	private UPath getPolygonUp(final StringBounder stringBounder, final Point2D pp1, final Point2D pp2) {
		final UPath polygon = new UPath();
		polygon.moveTo(0, roundCorner / 2);
		polygon.lineTo(0, getHeight(stringBounder) - roundCorner / 2);
		polygon.arcTo(new Point2D.Double(roundCorner / 2, getHeight(stringBounder)), roundCorner / 2, 0, 0);
		polygon.lineTo(getWidth(stringBounder) - roundCorner / 2, getHeight(stringBounder));
		polygon.arcTo(new Point2D.Double(getWidth(stringBounder), getHeight(stringBounder) - roundCorner / 2),
				roundCorner / 2, 0, 0);
		polygon.lineTo(getWidth(stringBounder), cornersize);
		polygon.lineTo(getWidth(stringBounder) - cornersize, 0);

		double x1 = pp1.getX() - delta;
		x1 = MathUtils.limitation(x1, 0, getWidth(stringBounder) - cornersize);
		polygon.lineTo(x1 + 2 * delta, 0);
		polygon.lineTo(pp2.getX(), pp2.getY());

		polygon.lineTo(x1, 0);
		polygon.lineTo(roundCorner / 2, 0);
		polygon.arcTo(new Point2D.Double(0, roundCorner / 2), roundCorner / 2, 0, 0);
		polygon.closePath();
		return polygon;
	}

	private UPath getPolygonDown(final StringBounder stringBounder, final Point2D pp1, final Point2D pp2) {
		final UPath polygon = new UPath();
		polygon.moveTo(0, roundCorner / 2);
		polygon.lineTo(0, getHeight(stringBounder) - roundCorner / 2);
		polygon.arcTo(new Point2D.Double(roundCorner / 2, getHeight(stringBounder)), roundCorner / 2, 0, 0);

		double x1 = pp1.getX() - delta;
		x1 = MathUtils.limitation(x1, 0, getWidth(stringBounder));
		polygon.lineTo(x1, getHeight(stringBounder));
		polygon.lineTo(pp2.getX(), pp2.getY());
		polygon.lineTo(x1 + 2 * delta, getHeight(stringBounder));

		polygon.lineTo(getWidth(stringBounder) - roundCorner / 2, getHeight(stringBounder));
		polygon.arcTo(new Point2D.Double(getWidth(stringBounder), getHeight(stringBounder) - roundCorner / 2),
				roundCorner / 2, 0, 0);
		polygon.lineTo(getWidth(stringBounder), cornersize);
		polygon.lineTo(getWidth(stringBounder) - cornersize, 0);
		polygon.lineTo(roundCorner / 2, 0);
		polygon.arcTo(new Point2D.Double(0, roundCorner / 2), roundCorner / 2, 0, 0);
		polygon.closePath();
		return polygon;
	}

	public final int getMarginX1() {
		return marginX1;
	}

}
