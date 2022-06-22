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
package net.sourceforge.plantuml.skin.rose;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ArrowDecoration;
import net.sourceforge.plantuml.skin.ArrowDirection;
import net.sourceforge.plantuml.skin.ArrowDressing;
import net.sourceforge.plantuml.skin.ArrowHead;
import net.sourceforge.plantuml.skin.ArrowPart;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ComponentRoseArrow extends AbstractComponentRoseArrow {

	private final HorizontalAlignment messagePosition;
	private final boolean niceArrow;
	private final boolean belowForResponse;
	private final int inclination1;
	private final int inclination2;

	public ComponentRoseArrow(Style style, Display stringsToDisplay, ArrowConfiguration arrowConfiguration,
			HorizontalAlignment messagePosition, ISkinSimple spriteContainer, LineBreakStrategy maxMessageSize,
			boolean niceArrow, boolean belowForResponse) {
		super(style, stringsToDisplay, arrowConfiguration, spriteContainer, maxMessageSize);
		this.messagePosition = messagePosition;
		this.niceArrow = niceArrow;
		this.belowForResponse = belowForResponse;
		this.inclination1 = arrowConfiguration.getInclination1();
		this.inclination2 = arrowConfiguration.getInclination2();
	}

	public static final double spaceCrossX = 6;
	public static final double diamCircle = 8;
	public static final double thinCircle = 1.5;

	@Override
	public void drawInternalU(UGraphic ug, Area area) {
		if (getArrowConfiguration().isHidden())
			return;

		final Dimension2D dimensionToUse = area.getDimensionToUse();
		final StringBounder stringBounder = ug.getStringBounder();
		ug = ug.apply(getForegroundColor());

		final ArrowDressing dressing1 = getArrowConfiguration().getDressing1();
		final ArrowDressing dressing2 = getArrowConfiguration().getDressing2();

		double start = 0;
		double len = dimensionToUse.getWidth() - 1;
		final double lenFull = dimensionToUse.getWidth();

		final double pos1 = start + 1;
		final double pos2 = len - 1;

		if (getArrowConfiguration().getDecoration2() == ArrowDecoration.CIRCLE && dressing2.getHead() == ArrowHead.NONE)
			len -= diamCircle / 2;

		if (getArrowConfiguration().getDecoration2() == ArrowDecoration.CIRCLE && dressing2.getHead() != ArrowHead.NONE)
			len -= diamCircle / 2 + thinCircle;

		if (getArrowConfiguration().getDecoration1() == ArrowDecoration.CIRCLE
				&& dressing1.getHead() == ArrowHead.NONE) {
			start += diamCircle / 2;
			len -= diamCircle / 2;
		}
		if (getArrowConfiguration().getDecoration1() == ArrowDecoration.CIRCLE
				&& dressing1.getHead() == ArrowHead.NORMAL) {
			start += diamCircle + thinCircle;
			len -= diamCircle + thinCircle;
		}

		if (dressing2.getPart() == ArrowPart.FULL && dressing2.getHead() == ArrowHead.NORMAL)
			len -= getArrowDeltaX() / 2;

		if (dressing1.getPart() == ArrowPart.FULL && dressing1.getHead() == ArrowHead.NORMAL) {
			start += getArrowDeltaX() / 2;
			len -= getArrowDeltaX() / 2;
		}

		if (dressing2.getHead() == ArrowHead.CROSSX)
			len -= 2 * spaceCrossX;

		if (dressing1.getHead() == ArrowHead.CROSSX) {
			start += 2 * spaceCrossX;
			len -= 2 * spaceCrossX;
		}

		final double posArrow;
		final double yText;
		if (isBelowForResponse()) {
			posArrow = 0;
			yText = getMarginY();
		} else {
			posArrow = getTextHeight(stringBounder);
			yText = 0;
		}

		drawDressing1(ug.apply(new UTranslate(pos1, posArrow + inclination1)), dressing1,
				getArrowConfiguration().getDecoration1(), lenFull);
		drawDressing2(ug.apply(new UTranslate(pos2, posArrow + inclination2)), dressing2,
				getArrowConfiguration().getDecoration2(), lenFull);

		if (inclination1 == 0 && inclination2 == 0)
			getArrowConfiguration().applyStroke(ug).apply(new UTranslate(start, posArrow)).draw(new ULine(len, 0));
		else if (inclination1 != 0) {
			drawLine(getArrowConfiguration().applyStroke(ug), start + len, posArrow, 0, posArrow + inclination1);
		} else if (inclination2 != 0) {
			drawLine(getArrowConfiguration().applyStroke(ug), start, posArrow, pos2, posArrow + inclination2);
		}

		final ArrowDirection direction2 = getDirection2();
		final double textPos;
		if (messagePosition == HorizontalAlignment.CENTER) {
			final double textWidth = getTextBlock().calculateDimension(stringBounder).getWidth();
			textPos = (dimensionToUse.getWidth() - textWidth) / 2;
		} else if (messagePosition == HorizontalAlignment.RIGHT) {
			final double textWidth = getTextBlock().calculateDimension(stringBounder).getWidth();
			textPos = dimensionToUse.getWidth() - textWidth - getMarginX2()
					- (direction2 == ArrowDirection.LEFT_TO_RIGHT_NORMAL ? getArrowDeltaX() : 0);
		} else {
			textPos = getMarginX1()
					+ (direction2 == ArrowDirection.RIGHT_TO_LEFT_REVERSE || direction2 == ArrowDirection.BOTH_DIRECTION
							? getArrowDeltaX()
							: 0);
		}
		getTextBlock().drawU(ug.apply(new UTranslate(textPos, yText)));
	}

	private void drawLine(UGraphic ug, double x1, double y1, double x2, double y2) {
		ug = ug.apply(new UTranslate(x1, y1));
		ug.draw(new ULine(x2 - x1, y2 - y1));

	}

	public double getPosArrow(StringBounder stringBounder) {
		if (isBelowForResponse())
			return 0;

		return getTextHeight(stringBounder) - 2 * getMarginY();
	}

	private boolean isBelowForResponse() {
		return belowForResponse && getDirection2() == ArrowDirection.RIGHT_TO_LEFT_REVERSE;
	}

	private void drawDressing1(UGraphic ug, ArrowDressing dressing, ArrowDecoration decoration, double lenFull) {

		if (decoration == ArrowDecoration.CIRCLE) {
			final UEllipse circle = new UEllipse(diamCircle, diamCircle);
			ug.apply(new UStroke(thinCircle)).apply(getForegroundColor())
					.apply(new UTranslate(-diamCircle / 2 - thinCircle, -diamCircle / 2 - thinCircle / 2)).draw(circle);
			if (dressing.getHead() != ArrowHead.CROSSX)
				ug = ug.apply(UTranslate.dx(diamCircle / 2 + thinCircle));
		}

		if (dressing.getHead() == ArrowHead.ASYNC) {
			if (dressing.getPart() != ArrowPart.BOTTOM_PART)
				getArrowConfiguration().applyThicknessOnly(ug).draw(new ULine(getArrowDeltaX(), -getArrowDeltaY()));

			if (dressing.getPart() != ArrowPart.TOP_PART)
				getArrowConfiguration().applyThicknessOnly(ug).draw(new ULine(getArrowDeltaX(), getArrowDeltaY()));

		} else if (dressing.getHead() == ArrowHead.CROSSX) {
			ug = ug.apply(new UStroke(2));
			ug.apply(new UTranslate(spaceCrossX, -getArrowDeltaX() / 2))
					.draw(new ULine(getArrowDeltaX(), getArrowDeltaX()));
			ug.apply(new UTranslate(spaceCrossX, getArrowDeltaX() / 2))
					.draw(new ULine(getArrowDeltaX(), -getArrowDeltaX()));
		} else if (dressing.getHead() == ArrowHead.NORMAL) {
			final UPolygon polygon = getPolygonReverse(dressing.getPart());

			if (inclination1 != 0)
				polygon.rotate(Math.atan2(-inclination1, lenFull));

			ug.apply(getForegroundColor().bg()).draw(polygon);
		}

	}

	private void drawDressing2(UGraphic ug, ArrowDressing dressing, ArrowDecoration decoration, double lenFull) {

		if (decoration == ArrowDecoration.CIRCLE) {
			ug = ug.apply(new UStroke(thinCircle)).apply(getForegroundColor());
			final UEllipse circle = new UEllipse(diamCircle, diamCircle);
			ug.apply(new UTranslate(-diamCircle / 2 + thinCircle, -diamCircle / 2 - thinCircle / 2)).draw(circle);
			ug = ug.apply(new UStroke());
			ug = ug.apply(UTranslate.dx(-diamCircle / 2 - thinCircle));
		}

		if (dressing.getHead() == ArrowHead.ASYNC) {
			if (dressing.getPart() != ArrowPart.BOTTOM_PART)
				getArrowConfiguration().applyThicknessOnly(ug).draw(new ULine(-getArrowDeltaX(), -getArrowDeltaY()));

			if (dressing.getPart() != ArrowPart.TOP_PART)
				getArrowConfiguration().applyThicknessOnly(ug).draw(new ULine(-getArrowDeltaX(), getArrowDeltaY()));

		} else if (dressing.getHead() == ArrowHead.CROSSX) {
			ug = ug.apply(new UStroke(2));
			ug.apply(new UTranslate(-spaceCrossX - getArrowDeltaX(), -getArrowDeltaX() / 2))
					.draw(new ULine(getArrowDeltaX(), getArrowDeltaX()));
			ug.apply(new UTranslate(-spaceCrossX - getArrowDeltaX(), getArrowDeltaX() / 2))
					.draw(new ULine(getArrowDeltaX(), -getArrowDeltaX()));
		} else if (dressing.getHead() == ArrowHead.NORMAL) {
			final UPolygon polygon = getPolygonNormal(dressing.getPart());

			if (inclination2 != 0)
				polygon.rotate(Math.atan2(inclination2, lenFull));

			ug.apply(getForegroundColor().bg()).draw(polygon);
		}

	}

	private UPolygon getPolygonNormal(ArrowPart part) {
		UPolygon polygon = new UPolygon();
		if (part == ArrowPart.TOP_PART) {
			polygon.addPoint(-getArrowDeltaX(), -getArrowDeltaY());
			polygon.addPoint(0, 0);
			polygon.addPoint(-getArrowDeltaX(), 0);
		} else if (part == ArrowPart.BOTTOM_PART) {
			polygon.addPoint(-getArrowDeltaX(), 1);
			polygon.addPoint(0, 1);
			polygon.addPoint(-getArrowDeltaX(), getArrowDeltaY() + 1);
		} else {
			polygon.addPoint(-getArrowDeltaX(), -getArrowDeltaY());
			polygon.addPoint(0, 0);
			polygon.addPoint(-getArrowDeltaX(), +getArrowDeltaY());
			if (niceArrow)
				polygon.addPoint(-getArrowDeltaX() + 4, 0);
		}
		return polygon;
	}

	private UPolygon getPolygonReverse(ArrowPart part) {
		final UPolygon polygon = new UPolygon();
		if (part == ArrowPart.TOP_PART) {
			polygon.addPoint(getArrowDeltaX(), -getArrowDeltaY());
			polygon.addPoint(0, 0);
			polygon.addPoint(getArrowDeltaX(), 0);
		} else if (part == ArrowPart.BOTTOM_PART) {
			polygon.addPoint(getArrowDeltaX(), 1);
			polygon.addPoint(0, 1);
			polygon.addPoint(getArrowDeltaX(), getArrowDeltaY() + 1);
		} else {
			polygon.addPoint(getArrowDeltaX(), -getArrowDeltaY());
			polygon.addPoint(0, 0);
			polygon.addPoint(getArrowDeltaX(), getArrowDeltaY());
			if (niceArrow)
				polygon.addPoint(getArrowDeltaX() - 4, 0);

		}
		return polygon;
	}

	public Point2D getStartPoint(StringBounder stringBounder, Dimension2D dimensionToUse) {
		final double y = getYPoint(stringBounder);
		if (getDirection2() == ArrowDirection.LEFT_TO_RIGHT_NORMAL)
			return new Point2D.Double(getPaddingX(), y + inclination2);

		return new Point2D.Double(dimensionToUse.getWidth() + getPaddingX(), y + inclination2);
	}

	public Point2D getEndPoint(StringBounder stringBounder, Dimension2D dimensionToUse) {
		final double y = getYPoint(stringBounder);
		if (getDirection2() == ArrowDirection.LEFT_TO_RIGHT_NORMAL)
			return new Point2D.Double(dimensionToUse.getWidth() + getPaddingX(), y);

		return new Point2D.Double(getPaddingX(), y);
	}

	@Override
	public double getYPoint(StringBounder stringBounder) {
		if (isBelowForResponse())
			return getPaddingY();

		return getTextHeight(stringBounder) + getPaddingY();
	}

	final private ArrowDirection getDirection2() {
		return getArrowConfiguration().getArrowDirection();
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + getArrowDeltaY() + 2 * getPaddingY() + inclination1 + inclination2;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return getTextWidth(stringBounder) + getArrowDeltaX();
	}

}
