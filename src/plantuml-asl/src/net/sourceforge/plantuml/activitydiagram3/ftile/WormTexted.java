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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Iterator;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.graphic.HtmlColorAndStyle;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class WormTexted implements Iterable<Point2D.Double> {

	private final Worm worm;
	private TextBlock textBlock;

	public WormTexted() {
		this(new Worm());
	}

	private WormTexted(Worm worm) {
		this.worm = worm;
	}

	public Iterator<Point2D.Double> iterator() {
		return worm.iterator();
	}

	public void addPoint(double x, double y) {
		worm.addPoint(x, y);
	}

	public void drawInternalOneColor(UPolygon startDecoration, UGraphic ug, HtmlColorAndStyle color, double stroke,
			Direction emphasizeDirection, UPolygon endDecoration) {
		worm.drawInternalOneColor(startDecoration, ug, color, stroke, emphasizeDirection, endDecoration);
	}

	public Worm getWorm() {
		return worm;
	}

	public Point2D get(int i) {
		return worm.get(i);
	}

	public int size() {
		return worm.size();
	}

	public WormTexted merge(WormTexted other, MergeStrategy merge) {
		final Worm result = worm.merge(other.worm, merge);
		return new WormTexted(result);
	}

	public void addAll(WormTexted other) {
		this.worm.addAll(other.worm);

	}

	public void setLabel(TextBlock label) {
		if (textBlock != null) {
			throw new IllegalStateException();
		}
		this.textBlock = label;
	}

	public boolean isEmptyText(StringBounder stringBounder) {
		return TextBlockUtils.isEmpty(textBlock, stringBounder);
	}

	private Point2D getTextBlockPosition(StringBounder stringBounder) {
		final Point2D pt1 = get(0);
		final Point2D pt2 = get(1);
		final Dimension2D dim = textBlock.calculateDimension(stringBounder);
		// if (worm.getDirectionsCode().startsWith("LD")) {
		// final double y = pt1.getY() - dim.getHeight();
		// return new Point2D.Double(Math.max(pt1.getX(), pt2.getX()) - dim.getWidth(), y);
		// }
		final double y = (pt1.getY() + pt2.getY()) / 2 - dim.getHeight() / 2;
		return new Point2D.Double(Math.max(pt1.getX(), pt2.getX()) + 4, y);
	}

	public double getMaxX(StringBounder stringBounder) {
		double result = -Double.MAX_VALUE;
		for (Point2D pt : this) {
			result = Math.max(result, pt.getX());
		}
		if (textBlock != null) {
			final Point2D position = getTextBlockPosition(stringBounder);
			final Dimension2D dim = textBlock.calculateDimension(stringBounder);
			result = Math.max(result, position.getX() + dim.getWidth());
		}
		return result;
	}

	void drawInternalLabel(UGraphic ug) {
		if (textBlock != null) {
			final Point2D position = getTextBlockPosition(ug.getStringBounder());
			textBlock.drawU(ug.apply(new UTranslate(position)));
		}
	}

	public void copyLabels(WormTexted other) {
		this.textBlock = other.textBlock;
	}

}
