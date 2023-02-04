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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.Iterator;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.graphic.HtmlColorAndStyle;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.Direction;

public class WormTexted implements Iterable<XPoint2D> {

	private final Worm worm;
	private TextBlock textBlock;

	private WormTexted(Style style, Arrows arrows) {
		this(new Worm(style, arrows));
	}

	private WormTexted(Worm worm) {
		this.worm = worm;
	}

	public Iterator<XPoint2D> iterator() {
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

	public XPoint2D get(int i) {
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

	private XPoint2D getTextBlockPosition(StringBounder stringBounder) {
		final XPoint2D pt1 = get(0);
		final XPoint2D pt2 = get(1);
		final XDimension2D dim = textBlock.calculateDimension(stringBounder);
		// if (worm.getDirectionsCode().startsWith("LD")) {
		// final double y = pt1.getY() - dim.getHeight();
		// return new XPoint2D(Math.max(pt1.getX(), pt2.getX()) - dim.getWidth(), y);
		// }
		final double y = (pt1.getY() + pt2.getY()) / 2 - dim.getHeight() / 2;
		return new XPoint2D(Math.max(pt1.getX(), pt2.getX()) + 4, y);
	}

	public double getMaxX(StringBounder stringBounder) {
		double result = -Double.MAX_VALUE;
		for (XPoint2D pt : this) {
			result = Math.max(result, pt.getX());
		}
		if (textBlock != null) {
			final XPoint2D position = getTextBlockPosition(stringBounder);
			final XDimension2D dim = textBlock.calculateDimension(stringBounder);
			result = Math.max(result, position.getX() + dim.getWidth());
		}
		return result;
	}

	void drawInternalLabel(UGraphic ug) {
		if (textBlock != null) {
			final XPoint2D position = getTextBlockPosition(ug.getStringBounder());
			textBlock.drawU(ug.apply(new UTranslate(position)));
		}
	}

	public void copyLabels(WormTexted other) {
		this.textBlock = other.textBlock;
	}

}
