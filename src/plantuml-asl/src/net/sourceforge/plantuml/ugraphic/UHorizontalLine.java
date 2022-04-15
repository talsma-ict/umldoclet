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
package net.sourceforge.plantuml.ugraphic;

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;

public class UHorizontalLine implements UShape {

	private final double skipAtStart;
	private final double skipAtEnd;
	private final TextBlock title;
	private final boolean blankTitle;
	private final char style;
	private final double defaultThickness;

	private UHorizontalLine(double defaultThickness, double skipAtStart, double skipAtEnd, TextBlock title,
			boolean blankTitle, char style) {
		this.defaultThickness = defaultThickness;
		this.skipAtEnd = skipAtEnd;
		this.skipAtStart = skipAtStart;
		this.title = title;
		this.blankTitle = blankTitle;
		this.style = style;
	}

	public static UHorizontalLine infinite(double defaultThickness, double skipAtStart, double skipAtEnd, char style) {
		return new UHorizontalLine(defaultThickness, skipAtStart, skipAtEnd, null, false, style);
	}

	public static UHorizontalLine infinite(double defaultThickness, double skipAtStart, double skipAtEnd,
			TextBlock title, char style) {
		return new UHorizontalLine(defaultThickness, skipAtStart, skipAtEnd, title, false, style);
	}

	public boolean isDouble() {
		return style == '=';
	}

	// static public UHorizontalLine infinite(UStroke stroke) {
	// return new UHorizontalLine(0, 0, null, false, stroke);
	// }

	public void drawLineInternal(final UGraphic ug, Stencil stencil, double y, UStroke defaultStroke) {
		stencil = addSkip(stencil);
		final UStroke strokeToUse = style == '\0' ? defaultStroke : getStroke();
		final UGraphic ugStroke = ug.apply(strokeToUse);
		if (title == null) {
			drawHLine(stencil, y, ugStroke);
			return;
		}
		final Dimension2D dimTitle = title.calculateDimension(ug.getStringBounder());
		drawHLine(firstHalf(stencil, dimTitle.getWidth()), y, ugStroke);
		final double startingX = stencil.getStartingX(ug.getStringBounder(), y);
		final double endingX = stencil.getEndingX(ug.getStringBounder(), y);
		drawTitleInternal(ug, startingX, endingX, y, false);
		drawHLine(secondHalf(stencil, dimTitle.getWidth()), y, ugStroke);
	}

	private Stencil addSkip(final Stencil stencil) {
		return new Stencil() {
			public double getStartingX(StringBounder stringBounder, double y) {
				return stencil.getStartingX(stringBounder, y) + skipAtStart;
			}

			public double getEndingX(StringBounder stringBounder, double y) {
				return stencil.getEndingX(stringBounder, y) - skipAtEnd;
			}
		};
	}

	private static Stencil firstHalf(final Stencil stencil, final double widthTitle) {
		return new Stencil() {
			public double getStartingX(StringBounder stringBounder, double y) {
				return stencil.getStartingX(stringBounder, y);
			}

			public double getEndingX(StringBounder stringBounder, double y) {
				final double start = stencil.getStartingX(stringBounder, y);
				final double end = stencil.getEndingX(stringBounder, y);
				final double len = (end - start - widthTitle) / 2;
				return start + len;
			}
		};
	}

	private static Stencil secondHalf(final Stencil stencil, final double widthTitle) {
		return new Stencil() {
			public double getStartingX(StringBounder stringBounder, double y) {
				final double start = stencil.getStartingX(stringBounder, y);
				final double end = stencil.getEndingX(stringBounder, y);
				final double len = (end - start - widthTitle) / 2;
				return end - len;
			}

			public double getEndingX(StringBounder stringBounder, double y) {
				return stencil.getEndingX(stringBounder, y);
			}
		};
	}

	private void drawHLine(Stencil stencil, double y, final UGraphic ug) {
		drawSimpleHline(ug, stencil, y);
		if (style == '=')
			drawSimpleHline(ug, stencil, y + 2);

	}

	private static void drawSimpleHline(UGraphic ug, Stencil stencil, double y) {
		final double startingX = stencil.getStartingX(ug.getStringBounder(), y);
		final double endingX = stencil.getEndingX(ug.getStringBounder(), y);
		ug.apply(new UTranslate(startingX, y)).draw(ULine.hline(endingX - startingX));
	}

	public void drawTitleInternal(UGraphic ug, double startingX, double endingX, double y, boolean clearArea) {
		if (title == null || blankTitle) {
			return;
		}
		final double widthToUse = endingX - startingX;
		final Dimension2D dimTitle = title.calculateDimension(ug.getStringBounder());
		final double space = (widthToUse - dimTitle.getWidth()) / 2;
		final double x1 = startingX + space;
		final double y1 = y - dimTitle.getHeight() / 2 - 0.5;
		ug = ug.apply(new UTranslate(x1, y1));
		if (clearArea) {
			ug.apply(getStroke()).draw(new URectangle(dimTitle));
		}
		title.drawU(ug);
	}

	public void drawMe(UGraphic ug) {
		ug.draw(this);
	}

	public UStroke getStroke() {
		if (style == '\0')
			throw new IllegalStateException();
		else if (style == '=')
			return new UStroke();
		else if (style == '.')
			return new UStroke(1, 2, 1);
		else if (style == '-')
			return new UStroke();
		else
			return new UStroke(defaultThickness);

	}

}
