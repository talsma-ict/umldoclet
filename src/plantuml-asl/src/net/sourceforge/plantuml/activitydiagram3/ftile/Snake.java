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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColorAndStyle;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.comp.PiecewiseAffineTransform;

public class Snake implements UShape {

	static class Text {
		private final TextBlock textBlock;
		private final VerticalAlignment verticalAlignment;
		private final HorizontalAlignment horizontalAlignment;

		Text(TextBlock textBlock, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment) {
			this.textBlock = Objects.requireNonNull(textBlock);
			this.verticalAlignment = verticalAlignment;
			this.horizontalAlignment = horizontalAlignment;
		}

		private boolean hasText(StringBounder stringBounder) {
			return TextBlockUtils.isEmpty(this.textBlock, stringBounder) == false;
		}

	}

	private final Worm worm;
	private final UPolygon startDecoration;
	private final UPolygon endDecoration;
	private final Rainbow color;

	private final List<Text> texts;
	private final MergeStrategy mergeable;
	private final Direction emphasizeDirection;

	public Snake transformX(PiecewiseAffineTransform compressionTransform) {
		final Snake result = cloneEmpty();
		for (Point2D.Double pt : worm) {
			final double x = compressionTransform.transform(pt.x);
			final double y = pt.y;
			result.addPoint(x, y);
		}
		return result;
	}

	public Snake move(double dx, double dy) {
		final Snake result = cloneEmpty();
		for (Point2D pt : worm) {
			result.addPoint(pt.getX() + dx, pt.getY() + dy);
		}
		return result;
	}

	private Snake cloneEmpty() {
		return new Snake(startDecoration, color, endDecoration, worm.cloneEmpty(), mergeable, emphasizeDirection,
				texts);
	}

	public final Snake ignoreForCompression() {
		this.worm.setIgnoreForCompression();
		return this;
	}

	public Snake emphasizeDirection(Direction emphasizeDirection) {
		return new Snake(startDecoration, color, endDecoration, worm, mergeable, emphasizeDirection, texts);
	}

	public Snake withoutEndDecoration() {
		return new Snake(startDecoration, color, null, worm, mergeable, emphasizeDirection, texts);
	}

	public Snake withMerge(MergeStrategy mergeable) {
		return new Snake(startDecoration, color, endDecoration, worm, mergeable, emphasizeDirection, texts);
	}

	public Snake withLabel(TextBlock textBlock, HorizontalAlignment horizontalAlignment) {
		if (textBlock != null) {
			this.texts.add(new Text(textBlock, null, horizontalAlignment));
		}
		return this;
	}

	public Snake withLabel(TextBlock textBlock, VerticalAlignment verticalAlignment) {
		if (textBlock != null) {
			this.texts.add(new Text(textBlock, verticalAlignment, null));
		}
		if (verticalAlignment != VerticalAlignment.BOTTOM) {
			throw new UnsupportedOperationException();
		}
		return this;
	}

	public static Snake create(Rainbow color) {
		return new Snake(null, color, null, new Worm(), MergeStrategy.FULL, null, new ArrayList<Text>());
	}

	public static Snake create(Rainbow color, UPolygon endDecoration) {
		return new Snake(null, color, endDecoration, new Worm(), MergeStrategy.FULL, null, new ArrayList<Text>());
	}

	public static Snake create(UPolygon startDecoration, Rainbow color, UPolygon endDecoration) {
		return new Snake(startDecoration, color, endDecoration, new Worm(), MergeStrategy.FULL, null,
				new ArrayList<Text>());
	}

	private Snake(UPolygon startDecoration, Rainbow color, UPolygon endDecoration, Worm worm, MergeStrategy mergeable,
			Direction emphasizeDirection, List<Text> texts) {

		if (Objects.requireNonNull(color).size() == 0) {
			throw new IllegalArgumentException();
		}
		this.worm = worm;
		this.texts = Objects.requireNonNull(texts);
		this.emphasizeDirection = emphasizeDirection;
		this.startDecoration = startDecoration;
		this.endDecoration = endDecoration;
		this.color = color;
		this.mergeable = mergeable;
	}

	public Snake translate(UTranslate translate) {
		return move(translate.getDx(), translate.getDy());
	}

	@Override
	public String toString() {
		return worm.toString();
	}

	public void addPoint(double x, double y) {
		worm.addPoint(x, y);
	}

	public void addPoint(Point2D p) {
		addPoint(p.getX(), p.getY());
	}

	public void drawInternal(UGraphic ug) {
		if (color.size() > 1) {
			drawRainbow(ug);
		} else {
			worm.drawInternalOneColor(startDecoration, ug, color.getColors().get(0), 1.5, emphasizeDirection,
					endDecoration);
			drawInternalLabel(ug);
		}

	}

	private void drawRainbow(UGraphic ug) {
		List<HtmlColorAndStyle> colors = color.getColors();
		final int colorArrowSeparationSpace = color.getColorArrowSeparationSpace();
		final double move = 2 + colorArrowSeparationSpace;
		final WormMutation mutation = WormMutation.create(worm, move);
		if (mutation.isDxNegative()) {
			colors = new ArrayList<>(colors);
			Collections.reverse(colors);
		}
		final double globalMove = -1.0 * (colors.size() - 1) / 2.0;
		Worm current = worm.moveFirstPoint(mutation.getFirst().multiplyBy(globalMove));
		if (mutation.size() > 2) {
			current = current.moveLastPoint(mutation.getLast().multiplyBy(globalMove));
		}
		for (int i = 0; i < colors.size(); i++) {
			double stroke = 1.5;
			if (colorArrowSeparationSpace == 0) {
				stroke = i == colors.size() - 1 ? 2.0 : 3.0;
			}
			current.drawInternalOneColor(startDecoration, ug, colors.get(i), stroke, emphasizeDirection, endDecoration);
			current = mutation.mute(current);
		}
		final UTranslate textTranslate = mutation.getTextTranslate(colors.size());
		drawInternalLabel(ug.apply(textTranslate));
	}

	private void drawInternalLabel(UGraphic ug) {
		for (Text text : texts) {
			final Point2D position = getTextBlockPosition(ug.getStringBounder(), text);
			text.textBlock.drawU(ug.apply(new UTranslate(position)));
		}
	}

	public double getMaxX(StringBounder stringBounder) {
		double result = -Double.MAX_VALUE;
		for (Point2D pt : worm) {
			result = Math.max(result, pt.getX());
		}
		for (Text text : texts) {
			final Point2D position = getTextBlockPosition(stringBounder, text);
			final Dimension2D dim = text.textBlock.calculateDimension(stringBounder);
			result = Math.max(result, position.getX() + dim.getWidth());
		}
		return result;
	}

	private Point2D getTextBlockPosition(StringBounder stringBounder, Text text) {
		final Point2D pt1 = worm.get(0);
		final Point2D pt2 = worm.get(1);
		final Dimension2D dim = text.textBlock.calculateDimension(stringBounder);
		double x = Math.max(pt1.getX(), pt2.getX()) + 4;
		final boolean zigzag = worm.getDirectionsCode().startsWith("DLD") || worm.getDirectionsCode().startsWith("DRD");
		double y = (pt1.getY() + pt2.getY()) / 2 - dim.getHeight() / 2;
		if (text.verticalAlignment == VerticalAlignment.BOTTOM) {
			x = worm.getLast().getX();
		} else if (text.horizontalAlignment == HorizontalAlignment.CENTER && zigzag) {
			final Point2D pt3 = worm.get(2);
			x = (pt2.getX() + pt3.getX()) / 2 - dim.getWidth() / 2;
		} else if (text.horizontalAlignment == HorizontalAlignment.RIGHT && zigzag) {
			x = Math.max(pt1.getX(), pt2.getX()) - dim.getWidth() - 4;
		} else if (worm.getDirectionsCode().equals("RD")) {
			x = Math.max(pt1.getX(), pt2.getX());
			y = (pt1.getY() + worm.get(2).getY()) / 2 - dim.getHeight() / 2;
		} else if (worm.getDirectionsCode().equals("LD")) {
			x = Math.min(pt1.getX(), pt2.getX());
			y = (pt1.getY() + worm.get(2).getY()) / 2 - dim.getHeight() / 2;
		}
		return new Point2D.Double(x, y);
	}

	public List<Line2D> getHorizontalLines() {
		final List<Line2D> result = new ArrayList<>();
		for (int i = 0; i < worm.size() - 1; i++) {
			final Point2D pt1 = worm.get(i);
			final Point2D pt2 = worm.get(i + 1);
			if (pt1.getY() == pt2.getY()) {
				final Line2D line = new Line2D.Double(pt1, pt2);
				result.add(line);
			}
		}
		return result;

	}

	private Point2D getFirst() {
		return worm.get(0);
	}

	public Point2D getLast() {
		return worm.get(worm.size() - 1);
	}

	static boolean same(Point2D pt1, Point2D pt2) {
		return pt1.distance(pt2) < 0.001;
	}

	public Snake merge(Snake other, StringBounder stringBounder) {
		final MergeStrategy strategy = this.mergeable.max(other.mergeable);
		if (strategy == MergeStrategy.NONE) {
			return null;
		}
		for (Text text : other.texts) {
			if (text.hasText(stringBounder)) {
				return null;
			}
		}
		if (same(this.getLast(), other.getFirst())) {
			final UPolygon oneOf = other.endDecoration == null ? endDecoration : other.endDecoration;
			if (this.startDecoration != null || other.startDecoration != null) {
				throw new UnsupportedOperationException("Not yet coded: to be done");
			}
			final Snake result = new Snake(null, color, oneOf, this.worm.merge(other.worm, strategy), strategy,
					emphasizeDirection == null ? other.emphasizeDirection : emphasizeDirection, new ArrayList<Text>());
			// result.textBlock = oneOf(this.textBlock, other.textBlock, stringBounder);
			return result;
		}
		if (same(this.getFirst(), other.getLast())) {
			return other.merge(this, stringBounder);
		}
		return null;
	}

	public boolean touches(Snake other) {
		if (other.mergeable != MergeStrategy.FULL) {
			return false;
		}
		if (other.worm.isPureHorizontal()) {
			return false;
		}
		return same(this.getLast(), other.getFirst());
	}

	public boolean doesHorizontalCross(MinMax minMax) {
		return worm.doesHorizontalCross(minMax);
	}

}
