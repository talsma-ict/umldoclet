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
package net.sourceforge.plantuml.timingdiagram;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.WithLinkType;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TimeArrow implements UDrawable {

	private final Point2D start;
	private final Point2D end;
	private final Display label;
	private final ISkinSimple spriteContainer;
	private final WithLinkType type;

	public static TimeArrow create(IntricatedPoint pt1, IntricatedPoint pt2, Display label,
			ISkinSimple spriteContainer, WithLinkType type) {
		final TimeArrow arrow1 = new TimeArrow(pt1.getPointA(), pt2.getPointA(), label, spriteContainer, type);
		final TimeArrow arrow2 = new TimeArrow(pt1.getPointA(), pt2.getPointB(), label, spriteContainer, type);
		final TimeArrow arrow3 = new TimeArrow(pt1.getPointB(), pt2.getPointA(), label, spriteContainer, type);
		final TimeArrow arrow4 = new TimeArrow(pt1.getPointB(), pt2.getPointB(), label, spriteContainer, type);
		return shorter(arrow1, arrow2, arrow3, arrow4);
	}

	private TimeArrow(Point2D start, Point2D end, Display label, ISkinSimple spriteContainer, WithLinkType type) {
		this.start = start;
		this.type = type;
		this.end = end;
		this.label = label;
		this.spriteContainer = spriteContainer;
	}

	private double getAngle() {
		return Math.atan2(end.getX() - start.getX(), end.getY() - start.getY());
	}

	private static TimeArrow shorter(TimeArrow arrow1, TimeArrow arrow2) {
		if (arrow1.len() < arrow2.len()) {
			return arrow1;
		}
		return arrow2;
	}

	private static TimeArrow shorter(TimeArrow arrow1, TimeArrow arrow2, TimeArrow arrow3, TimeArrow arrow4) {
		return shorter(shorter(arrow1, arrow2), shorter(arrow3, arrow4));
	}

	private double len() {
		return start.distance(end);
	}

	public TimeArrow translate(UTranslate translate) {
		return new TimeArrow(translate.getTranslated(start), translate.getTranslated(end), label, spriteContainer, type);
	}

	public static Point2D onCircle(Point2D pt, double alpha) {
		final double radius = 8;
		final double x = pt.getX() - Math.sin(alpha) * radius;
		final double y = pt.getY() - Math.cos(alpha) * radius;
		return new Point2D.Double(x, y);
	}

	private FontConfiguration getFontConfiguration() {
		final UFont font = UFont.serif(14);

		return new FontConfiguration(font, type.getSpecificColor(), type.getSpecificColor(), false);
	}

	public void drawU(UGraphic ug) {
		final double angle = getAngle();
		ug = ug.apply(new UChangeColor(type.getSpecificColor())).apply(type.getType().getStroke3(new UStroke()));
		final ULine line = new ULine(end.getX() - start.getX(), end.getY() - start.getY());
		ug.apply(new UTranslate(start)).draw(line);

		final double delta = 20.0 * Math.PI / 180.0;
		final Point2D pt1 = onCircle(end, angle + delta);
		final Point2D pt2 = onCircle(end, angle - delta);

		final UPolygon polygon = new UPolygon();
		polygon.addPoint(pt1.getX(), pt1.getY());
		polygon.addPoint(pt2.getX(), pt2.getY());
		polygon.addPoint(end.getX(), end.getY());

		ug = ug.apply(new UChangeBackColor(type.getSpecificColor()));
		ug.draw(polygon);

		final TextBlock textLabel = label.create(getFontConfiguration(), HorizontalAlignment.LEFT, spriteContainer);
		double xText = (pt1.getX() + pt2.getX()) / 2;
		double yText = (pt1.getY() + pt2.getY()) / 2;
		if (start.getY() < end.getY()) {
			final Dimension2D dimLabel = textLabel.calculateDimension(ug.getStringBounder());
			yText -= dimLabel.getHeight();
		}
		textLabel.drawU(ug.apply(new UTranslate(xText, yText)));

		// final double radius = 4;
		// final UTranslate forCirle = new UTranslate(end.getX() - radius, end.getY() - radius);
		// ug.apply(forCirle).draw(new UEllipse(2 * radius, 2 * radius));
	}

}
