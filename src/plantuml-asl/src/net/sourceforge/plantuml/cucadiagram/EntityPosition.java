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
package net.sourceforge.plantuml.cucadiagram;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.EnumSet;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public enum EntityPosition {

	NORMAL, ENTRY_POINT, EXIT_POINT, INPUT_PIN, OUTPUT_PIN, EXPANSION_INPUT, EXPANSION_OUTPUT;

	public static final double RADIUS = 6;

	public void drawSymbol(UGraphic ug, Rankdir rankdir) {
		if (this == NORMAL) {
			throw new IllegalStateException();
		} else if (this == ENTRY_POINT || this == EXIT_POINT) {
			final Shadowable circle = new UEllipse(RADIUS * 2, RADIUS * 2);
			ug.draw(circle);
			if (this == EntityPosition.EXIT_POINT) {
				final double xc = 0 + RADIUS + .5;
				final double yc = 0 + RADIUS + .5;
				final double radius = RADIUS - .5;
				drawLine(ug, getPointOnCircle(xc, yc, Math.PI / 4, radius),
						getPointOnCircle(xc, yc, Math.PI + Math.PI / 4, radius));
				drawLine(ug, getPointOnCircle(xc, yc, -Math.PI / 4, radius),
						getPointOnCircle(xc, yc, Math.PI - Math.PI / 4, radius));
			}
		} else if (this == INPUT_PIN || this == OUTPUT_PIN) {
			final Shadowable rectangle = new URectangle(RADIUS * 2, RADIUS * 2);
			ug.draw(rectangle);
		} else if (this == EXPANSION_INPUT || this == EXPANSION_OUTPUT) {
			if (rankdir == Rankdir.TOP_TO_BOTTOM) {
				final Shadowable rectangle = new URectangle(RADIUS * 2 * 4, RADIUS * 2);
				ug.draw(rectangle);
				final ULine vline = new ULine(0, RADIUS * 2);
				ug.apply(new UTranslate(RADIUS * 2, 0)).draw(vline);
				ug.apply(new UTranslate(RADIUS * 2 * 2, 0)).draw(vline);
				ug.apply(new UTranslate(RADIUS * 2 * 3, 0)).draw(vline);
			} else {
				final Shadowable rectangle = new URectangle(RADIUS * 2, RADIUS * 2 * 4);
				ug.apply(new UTranslate(0, 0)).draw(rectangle);
				final ULine hline = new ULine(RADIUS * 2, 0);
				ug.apply(new UTranslate(0, RADIUS * 2)).draw(hline);
				ug.apply(new UTranslate(0, RADIUS * 2 * 2)).draw(hline);
				ug.apply(new UTranslate(0, RADIUS * 2 * 3)).draw(hline);
			}
		}

	}

	public Dimension2D getDimension(Rankdir rankdir) {
		if (this == EXPANSION_INPUT || this == EXPANSION_OUTPUT) {
			if (rankdir == Rankdir.TOP_TO_BOTTOM) {
				return new Dimension2DDouble(EntityPosition.RADIUS * 2 * 4, EntityPosition.RADIUS * 2);
			}
			return new Dimension2DDouble(EntityPosition.RADIUS * 2, EntityPosition.RADIUS * 2 * 4);
		}
		return new Dimension2DDouble(EntityPosition.RADIUS * 2, EntityPosition.RADIUS * 2);
	}

	private Point2D getPointOnCircle(double xc, double yc, double angle, double radius) {
		final double x = xc + radius * Math.cos(angle);
		final double y = yc + radius * Math.sin(angle);
		return new Point2D.Double(x, y);
	}

	static private void drawLine(UGraphic ug, Point2D p1, Point2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(p1.getX(), p1.getY())).draw(new ULine(dx, dy));

	}

	public ShapeType getShapeType() {
		if (this == NORMAL) {
			throw new IllegalStateException();
		}
		if (this == ENTRY_POINT || this == EXIT_POINT) {
			return ShapeType.CIRCLE;
		}
		return ShapeType.RECTANGLE;
	}

	public static EntityPosition fromStereotype(String label) {
		if ("<<entrypoint>>".equalsIgnoreCase(label)) {
			return ENTRY_POINT;
		}
		if ("<<exitpoint>>".equalsIgnoreCase(label)) {
			return EXIT_POINT;
		}
		if ("<<inputpin>>".equalsIgnoreCase(label)) {
			return INPUT_PIN;
		}
		if ("<<outputpin>>".equalsIgnoreCase(label)) {
			return OUTPUT_PIN;
		}
		if ("<<expansioninput>>".equalsIgnoreCase(label)) {
			return EXPANSION_INPUT;
		}
		if ("<<expansionoutput>>".equalsIgnoreCase(label)) {
			return EXPANSION_OUTPUT;
		}
		return EntityPosition.NORMAL;
	}

	public static EnumSet<EntityPosition> getInputs() {
		return EnumSet.of(ENTRY_POINT, INPUT_PIN, EXPANSION_INPUT);
	}

	public static EnumSet<EntityPosition> getOutputs() {
		return EnumSet.of(EXIT_POINT, OUTPUT_PIN, EXPANSION_OUTPUT);
	}

}
