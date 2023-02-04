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

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.USegmentType;
import net.sourceforge.plantuml.ugraphic.UTranslate;

// Created from Luc Trudeau original work
public enum BoxStyle {
	PLAIN(null, '\0', 0) {
		@Override
		protected Shadowable getShape(double width, double height, double roundCorner) {
			return new URectangle(width, height).rounded(roundCorner);
		}
	},
	SDL_INPUT("input", '<', 10) {
		@Override
		protected Shadowable getShape(double width, double height, double roundCorner) {
			final UPolygon result = new UPolygon();
			result.addPoint(0, 0);
			result.addPoint(width + DELTA_INPUT_OUTPUT, 0);
			result.addPoint(width, height / 2);
			result.addPoint(width + DELTA_INPUT_OUTPUT, height);
			result.addPoint(0, height);
			return result;
		}
	},
	SDL_OUTPUT("output", '>', 10) {
		@Override
		protected Shadowable getShape(double width, double height, double roundCorner) {
			final UPolygon result = new UPolygon();
			result.addPoint(0.0, 0.0);
			result.addPoint(width, 0.0);
			result.addPoint(width + DELTA_INPUT_OUTPUT, height / 2);
			result.addPoint(width, height);
			result.addPoint(0.0, height);
			return result;
		}
	},
	SDL_PROCEDURE("procedure", '|', 0) {
		@Override
		protected void drawInternal(UGraphic ug, double width, double height, double shadowing, double roundCorner) {
			final URectangle rect = new URectangle(width, height);
			rect.setDeltaShadow(shadowing);
			ug.draw(rect);
			final ULine vline = ULine.vline(height);
			ug.apply(UTranslate.dx(PADDING)).draw(vline);
			ug.apply(UTranslate.dx(width - PADDING)).draw(vline);
		}
	},
	SDL_SAVE("save", '\\', 0) {
		@Override
		protected Shadowable getShape(double width, double height, double roundCorner) {
			final UPolygon result = new UPolygon();
			result.addPoint(0.0, 0.0);
			result.addPoint(width - DELTA_INPUT_OUTPUT, 0.0);
			result.addPoint(width, height);
			result.addPoint(DELTA_INPUT_OUTPUT, height);
			return result;
		}
	},
	SDL_ANTISAVE("load", '/', 0) {
		@Override
		protected Shadowable getShape(double width, double height, double roundCorner) {
			final UPolygon result = new UPolygon();
			result.addPoint(DELTA_INPUT_OUTPUT, 0.0);
			result.addPoint(width, 0.0);
			result.addPoint(width - DELTA_INPUT_OUTPUT, height);
			result.addPoint(0, height);
			return result;
		}
	},
	SDL_CONTINUOUS("continuous", '}', 0) {
		@Override
		protected Shadowable getShape(double width, double height, double roundCorner) {
			final UPath result = new UPath();
			final double c1[] = { DELTA_CONTINUOUS, 0 };
			final double c2[] = { 0, height / 2 };
			final double c3[] = { DELTA_CONTINUOUS, height };

			result.add(c1, USegmentType.SEG_MOVETO);
			result.add(c2, USegmentType.SEG_LINETO);
			result.add(c3, USegmentType.SEG_LINETO);

			final double c4[] = { width - DELTA_CONTINUOUS, 0 };
			final double c5[] = { width, height / 2 };
			final double c6[] = { width - DELTA_CONTINUOUS, height };

			result.add(c4, USegmentType.SEG_MOVETO);
			result.add(c5, USegmentType.SEG_LINETO);
			result.add(c6, USegmentType.SEG_LINETO);
			return result;
		}
	},
	SDL_TASK("task", ']', 0) {
		@Override
		protected Shadowable getShape(double width, double height, double roundCorner) {
			return new URectangle(width, height);
		}
	};

	private final String stereotype;
	private final char style;
	private final double shield;

	private static int DELTA_INPUT_OUTPUT = 10;
	private static double DELTA_CONTINUOUS = 5.0;
	private static int PADDING = 5;

	private BoxStyle(String stereotype, char style, double shield) {
		this.stereotype = stereotype;
		this.style = style;
		this.shield = shield;
	}

	public static BoxStyle fromString(String style) {
		if (style.length() == 1)
			for (BoxStyle bs : BoxStyle.values())
				if (bs.style == style.charAt(0))
					return bs;

		style = style.replaceAll("\\W", "");

		for (BoxStyle bs : BoxStyle.values())
			if (style.equalsIgnoreCase(bs.stereotype))
				return bs;

		return PLAIN;
	}

	public final UDrawable getUDrawable(final double width, final double height, final double shadowing,
			final double roundCorner) {
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				drawInternal(ug, width - getShield(), height, shadowing, roundCorner);
			}
		};
	}

	protected Shadowable getShape(double width, double height, double roundCorner) {
		return null;
	}

	protected void drawInternal(UGraphic ug, double width, double height, double shadowing, double roundCorner) {
		final Shadowable s = getShape(width, height, roundCorner);
		s.setDeltaShadow(shadowing);
		ug.draw(s);

	}

	public final double getShield() {
		return shield;
	}

}
